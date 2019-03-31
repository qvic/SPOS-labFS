package io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Config;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class IOSystemTest {

    private static final int BLOCK_SIZE = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));
    private static final int NUMBER_OF_BLOCKS = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));

    @Test
    void updateFromFile() {
        IOSystem ioSystem = new IOSystem();

        String tmpDirectoryPath = System.getProperty("java.io.tmpdir");
        String tmpFileName = "IOSystemTest";
        String fullFilename = Paths.get(tmpDirectoryPath, tmpFileName).toString();

        for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
            LogicalBlock block = new LogicalBlock();
            for (int j = 0; j < BLOCK_SIZE; j++) {
                block.setByte(j, (byte) (i + j));
            }
            ioSystem.writeBlock(i, block);
        }

        ioSystem.dumpToFile(fullFilename);

        // checking

        ioSystem = new IOSystem();
        ioSystem.updateFromFile(fullFilename);

        for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
            LogicalBlock block = ioSystem.readBlock(i);
            for (int j = 0; j < BLOCK_SIZE; j++) {
                assertEquals((byte) (i + j), block.getByte(j));
            }
        }
    }

    @Test
    void readBlock() {
        IOSystem ioSystem = new IOSystem();

        LogicalBlock block = ioSystem.readBlock(0);
        block.setByte(0, (byte) 1);

        assertNotEquals(ioSystem.readBlock(0).getByte(0), (byte) 1);
    }
}