package main.fs;

import main.io.LogicalBlock;

import java.util.Arrays;
import java.util.BitSet;

public class BitMap {

    private BitSet occupied;

    public BitMap(int numberOfBlocks) {
        int intsNeeded = numberOfBlocks / 32;

        this.occupied = new BitSet(numberOfBlocks);
    }

    public boolean isFreeBlock(int index) {
        return occupied.get(index);
    }

    public static BitMap fromBlock(int numberOfBlocks, LogicalBlock block) {
        BitMap bitMap = new BitMap(numberOfBlocks);

        for (int i = 0; i < 8; i++) {
            byte b = block.getByte(i);
            bitMap.occupied.set(i * 8 + 7, (b & 0b10000000) != 0);
            bitMap.occupied.set(i * 8 + 6, (b & 0b01000000) != 0);
            bitMap.occupied.set(i * 8 + 5, (b & 0b00100000) != 0);
            bitMap.occupied.set(i * 8 + 4, (b & 0b00010000) != 0);
            bitMap.occupied.set(i * 8 + 3, (b & 0b00001000) != 0);
            bitMap.occupied.set(i * 8 + 2, (b & 0b00000100) != 0);
            bitMap.occupied.set(i * 8 + 1, (b & 0b00000010) != 0);
            bitMap.occupied.set(i * 8 + 0, (b & 0b00000001) != 0);
        }

        System.out.println(bitMap.occupied.length());
        System.out.println(bitMap.occupied);
        return bitMap;
    }

    public void setOccupied(int index) {
        occupied.set(index, true);
    }

    public void setFree(int index) {
        occupied.set(index, false);
    }

    public LogicalBlock asBlock() {
        LogicalBlock block = new LogicalBlock();
        byte[] bytes = new byte[8];
        byte[] src = occupied.toByteArray();
        System.arraycopy(src, 0, bytes, 0, src.length);
        for (int i = 0; i < 8; i++) {
            block.setByte(i, bytes[i]);
        }
        return block;
    }

    @Override
    public String toString() {
        return "BitMap{" +
                "occupied=" + occupied +
                '}';
    }
}
