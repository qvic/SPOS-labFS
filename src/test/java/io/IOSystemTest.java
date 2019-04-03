package io;

import org.junit.jupiter.api.Test;
import util.Config;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class IOSystemTest {

    @Test
    void updateFromFile() {
        IOSystem ioSystem = new IOSystem();

        String tmpDirectoryPath = System.getProperty("java.io.tmpdir");
        String tmpFileName = "IOSystemTest";
        String fullFilename = Paths.get(tmpDirectoryPath, tmpFileName).toString();

        for (int i = 0; i < Config.BLOCKS; i++) {
            LogicalBlock block = new LogicalBlock();
            for (int j = 0; j < Config.BLOCK_SIZE; j++) {
                block.setByte(j, (byte) (i + j));
            }
            ioSystem.writeBlock(i, block);
        }

        ioSystem.dumpToFile(fullFilename);

        // checking

        ioSystem = new IOSystem();
        ioSystem.updateFromFile(fullFilename);

        for (int i = 0; i < Config.BLOCKS; i++) {
            LogicalBlock block = ioSystem.readBlock(i);
            for (int j = 0; j < Config.BLOCK_SIZE; j++) {
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

        ioSystem.writeBlock(0, block);

        assertEquals(ioSystem.readBlock(0).getByte(0), (byte) 1);
    }
}