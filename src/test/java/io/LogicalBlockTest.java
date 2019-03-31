package io;

import org.junit.jupiter.api.Test;
import util.Config;

import static org.junit.jupiter.api.Assertions.*;

class LogicalBlockTest {

    @Test
    void getInt() {
        LogicalBlock block = new LogicalBlock();
        int blockSize = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));
        int blockSizeInInts = blockSize / 4;

        for (int i = 0; i < blockSizeInInts; i++) {
            block.setInt(i, i + 123);
        }

        for (int i = 0; i < blockSizeInInts; i++) {
            assertEquals(i + 123, block.getInt(i));
        }
    }
}