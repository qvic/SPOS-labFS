package fs;

import io.LogicalBlock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitMapTest {

    @Test
    void isFreeBlock() {
        int numberOfBlocks = 64;
        BitMap bitMap = new BitMap(numberOfBlocks);

        for (int i = 0; i < numberOfBlocks; i++) {
            if (i % 2 == 0) {
                bitMap.setOccupied(i);
            }
        }

        // testing is setOccupied and isFree working correctly
        // by default bitmap should free for all indexes
        for (int i = 0; i < numberOfBlocks; i++) {
            if (i % 2 == 0) {
                assertFalse(bitMap.isFreeBlock(i));
            } else {
                assertTrue(bitMap.isFreeBlock(i));
            }
        }

        // testing that setFree works correctly
        // and is calling setFree on free index leave it free
        for (int i = 0; i < numberOfBlocks; i++) {
            if (i % 3 == 0) {
                bitMap.setFree(i);
            }
        }

        for (int i = 0; i < numberOfBlocks; i++) {
            if (i % 3 != 0 && i % 2 == 0) {
                assertFalse(bitMap.isFreeBlock(i));
            } else {
                assertTrue(bitMap.isFreeBlock(i));
            }
        }

        // testing bounds
        assertThrows(IllegalArgumentException.class, () -> bitMap.setFree(-1));
        assertThrows(IllegalArgumentException.class, () -> bitMap.setFree(64));
        assertThrows(IllegalArgumentException.class, () -> bitMap.setOccupied(-1));
        assertThrows(IllegalArgumentException.class, () -> bitMap.setOccupied(64));
        assertThrows(IllegalArgumentException.class, () -> bitMap.isFreeBlock(-1));
        assertThrows(IllegalArgumentException.class, () -> bitMap.isFreeBlock(64));
    }

    @Test
    void fromBlock() {
        LogicalBlock block = new LogicalBlock();
        block.setInt(0, 0b10010010010010010010010010010010);
        block.setInt(1, 0b01001001001001001001001001001001);

        int numberOfBlocks = 64;
        BitMap bitMap = BitMap.fromBlock(numberOfBlocks, block);

        for (int i = 0; i < numberOfBlocks; i++) {
            if (i % 3 == 0) {
                assertFalse(bitMap.isFreeBlock(i));
            } else {
                assertTrue(bitMap.isFreeBlock(i));
            }
        }
    }

    @Test
    void asBlock() {
        int numberOfBlocks = 64;
        BitMap bitMap = new BitMap(numberOfBlocks);

        for (int i = 0; i < numberOfBlocks; i++) {
            if (i % 3 == 0) {
                bitMap.setOccupied(i);
            }
        }

        LogicalBlock block = bitMap.asBlock();
        assertEquals(block.getInt(0), 0b10010010010010010010010010010010);
        assertEquals(block.getInt(1), 0b01001001001001001001001001001001);
    }
}