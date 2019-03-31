package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.LogManager;

public enum Config {
    INSTANCE;

    private static final String APP_PROPERTIES = "app.properties";
    private static final String LOGGING_PROPERTIES = "logging.properties";

    private final Properties properties;

    Config() {
        this.properties = new Properties();
        try {
            LogManager.getLogManager().readConfiguration(new FileInputStream(LOGGING_PROPERTIES));
            properties.load(new FileInputStream(APP_PROPERTIES));
        } catch (IOException e) {
            throw new RuntimeException("Properties can't be read");
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
