package main.fs;

import main.io.IOSystem;
import main.io.LogicalBlock;
import main.util.Config;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(FileSystem.class.getName());

    private IOSystem ioSystem;

    public FileSystem() {
        ioSystem = new IOSystem();

        int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
        BitMap bitMap = new BitMap(numberOfBlocks);
        ioSystem.saveBitmapToBlock(0, bitMap);

        FileDescriptor directory = new FileDescriptor(0);
//        ioSystem.writeBlock();
    }

    public void create(String name) {
        LOGGER.log(Level.INFO, String.format("Create: name=%s", name));

        BitMap bitMap = ioSystem.loadBitmapFromBlock(0);

        List<FileDescriptor> descriptors = ioSystem.loadFileDescriptorsFromBlock(1);
        FileDescriptor directory = descriptors.get(0);

        byte[] bytes = ioSystem.loadDataByDescriptor(directory);
        System.out.println(Arrays.toString(bytes));

        ioSystem.saveBitmapToBlock(0, bitMap);
    }

    public void destroy(String name) {
        LOGGER.log(Level.INFO, String.format("Destroy: name=%s", name));
    }

    public void open(String name) {
        LOGGER.log(Level.INFO, String.format("Open: name=%s", name));
    }

    public void close(int index) {
        LOGGER.log(Level.INFO, String.format("Close: index=%d", index));
    }

    public void read(int index, int count) {
        LOGGER.log(Level.INFO, String.format("Read: index=%d, count=%d", index, count));
    }

    public void write(int index, byte data, int count) {
        LOGGER.log(Level.INFO, String.format("Write: index=%d, data=%c, count=%d", index, data, count));
    }

    public void seek(int index, int position) {
        LOGGER.log(Level.INFO, String.format("Seek: index=%d, position=%d", index, position));
    }

    public void directory() {
        LOGGER.log(Level.INFO, "Directory");
    }

    public void input(String name) {
        LOGGER.log(Level.INFO, String.format("Input: name=%s", name));
    }

    public void save(String name) {
        LOGGER.log(Level.INFO, String.format("Save: name=%s", name));
    }
}
