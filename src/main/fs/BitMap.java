package main.fs;

import main.io.LogicalBlock;
import main.util.Util;

import java.util.Arrays;

public class BitMap {

    private boolean[] occupied;

    public BitMap(int numberOfBlocks) {
        int intsNeeded = numberOfBlocks / 32;

        this.occupied = new boolean[numberOfBlocks];
    }

    public boolean isFreeBlock(int index) {
        return !occupied[index];
    }

    public static BitMap fromBlock(int numberOfBlocks, LogicalBlock block) {
        BitMap bitMap = new BitMap(numberOfBlocks);

        for (int i = 0; i < 8; i++) {
            byte b = block.getByte(i);
            bitMap.occupied[i * 8 + 7] = ((b & 0b10000000) != 0);
            bitMap.occupied[i * 8 + 6] = ((b & 0b01000000) != 0);
            bitMap.occupied[i * 8 + 5] = ((b & 0b00100000) != 0);
            bitMap.occupied[i * 8 + 4] = ((b & 0b00010000) != 0);
            bitMap.occupied[i * 8 + 3] = ((b & 0b00001000) != 0);
            bitMap.occupied[i * 8 + 2] = ((b & 0b00000100) != 0);
            bitMap.occupied[i * 8 + 1] = ((b & 0b00000010) != 0);
            bitMap.occupied[i * 8 + 0] = ((b & 0b00000001) != 0);
        }

        System.out.println(Arrays.toString(bitMap.occupied));
        return bitMap;
    }

    public void setOccupied(int index) {
        occupied[index] = true;
    }

    public void setFree(int index) {
        occupied[index] = false;
    }

    public LogicalBlock asBlock() {
        LogicalBlock block = new LogicalBlock();
        for (int i = 0; i < 8; i++) {
            block.setByte(i, (byte) (
                    (occupied[i * 8 + 7] ? 1 << 7 : 0) + (occupied[i * 8 + 6] ? 1 << 6 : 0) +
                            (occupied[i * 8 + 5] ? 1 << 5 : 0) + (occupied[i * 8 + 4] ? 1 << 4 : 0) +
                            (occupied[i * 8 + 3] ? 1 << 3 : 0) + (occupied[i * 8 + 2] ? 1 << 2 : 0) +
                            (occupied[i * 8 + 1] ? 1 << 1 : 0) + (occupied[i * 8 + 0] ? 1 : 0)));
        }
        return block;
    }

    @Override
    public String toString() {
        return "BitMap{" +
                "occupied=" + Util.toBinaryString(occupied) +
                '}';
    }
}
