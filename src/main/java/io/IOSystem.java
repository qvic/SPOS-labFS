package io;

import util.Config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IOSystem {
    private static final Logger LOGGER = Logger.getLogger(IOSystem.class.getName());

    private LogicalBlock[] blocks;

    public IOSystem() {
        blocks = new LogicalBlock[Config.BLOCKS];

        for (int i = 0; i < Config.BLOCKS; i++) {
            blocks[i] = new LogicalBlock();
        }
    }

    public void updateFromFile(String filename) {
        LOGGER.log(Level.INFO, String.format("Update from file: %s", filename));
        RandomAccessFile disk;
        try {
            disk = new RandomAccessFile(filename, "r");
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, String.format("File %s not found", filename));
            return;
        }
        try {
            for (int i = 0; i < blocks.length; i++) {
                byte[] bytes = new byte[Config.BLOCK_SIZE];
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
