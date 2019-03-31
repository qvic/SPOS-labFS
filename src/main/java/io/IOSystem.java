package io;

import util.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;
import java.util.logging.Level;


public class IOSystem {
    private static final Logger LOGGER = Logger.getLogger(IOSystem.class.getName());
    private static final int BLOCK_SIZE = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));
    private static final int NUMBER_OF_BLOCKS = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));

    private LogicalBlock[] blocks;

    public IOSystem() {
        blocks = new LogicalBlock[NUMBER_OF_BLOCKS];

        for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
            blocks[i] = new LogicalBlock();
        }
    }

    public void updateFromFile(String filename) {
        LOGGER.log(Level.INFO, String.format("Update from file: %s", filename));
        RandomAccessFile disk;
        try {
            disk = new RandomAccessFile(filename, "rw");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, String.format("File %s not found", filename));
            return;
        }
        try {
            for (int i = 0; i < blocks.length; i++) {
                byte[] bytes = new byte[BLOCK_SIZE];
                disk.read(bytes);
                blocks[i] = new LogicalBlock(bytes);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error occurred while loading file %s: %s", filename, e.getMessage()));
        }
    }

    public void dumpToFile(String filename) {
        LOGGER.log(Level.INFO, String.format("Dump to file: %s", filename));
        RandomAccessFile disk;
        try {
            disk = new RandomAccessFile(filename, "rw");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, String.format("File %s not found", filename));
            return;
        }
        try {
            for (LogicalBlock block : blocks) {
                disk.write(block.getBytes());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error occurred while loading file %s: %s", filename, e.getMessage()));
        }

    }

    public LogicalBlock readBlock(int index) {
        return blocks[index].getCopy();
    }

    public void writeBlock(int index, LogicalBlock block) {
        blocks[index] = block.getCopy();
    }
}
