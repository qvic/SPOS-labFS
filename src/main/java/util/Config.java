package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;

public class Config {

    private static final String APP_PROPERTIES = "app.properties";
    private static final String LOGGING_PROPERTIES = "logging.properties";
    private static final Properties properties;

    static {
        properties = new Properties();
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(LOGGING_PROPERTIES));
            properties.load(new FileInputStream(APP_PROPERTIES));
        } catch (IOException e) {
            throw new RuntimeException("Properties can't be read");
        }
    }

    public static final int BLOCKS = Integer.parseInt(properties.getProperty("blocks"));
    public static final int BLOCK_SIZE = Integer.parseInt(properties.getProperty("blockSize"));
    public static final int BLOCKS_FOR_DESCRIPTORS = Integer.parseInt(properties.getProperty("blocksForDescriptors"));
    public static final int BLOCK_INDICES_IN_DESCRIPTOR = Integer.parseInt(properties.getProperty("blockIndicesInDescriptor"));
    public static final int DESCRIPTORS_IN_BLOCK = Integer.parseInt(properties.getProperty("descriptorsInBlock"));
    public static final String COMMANDS_FILE_PATH=properties.getProperty("commandsFilePath");
}
