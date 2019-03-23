package main.fs;

import main.io.LogicalBlock;

public class BitMap {

    private int size;
    private int[] occupied;

    public BitMap(int numberOfBlocks) {
        this.size = numberOfBlocks;

        int intsNeeded = numberOfBlocks / 32;
        this.occupied = new int[intsNeeded];
    }

    public boolean isFreeBlock(int index) {
        return false;
    }

    public static BitMap fromBlock(int numberOfBlocks, LogicalBlock block) {
        BitMap bitMap = new BitMap(numberOfBlocks);
        for (int i = 0; i < bitMap.occupied.length; i++) {
            bitMap.occupied[i] = block.getInt(i);
        }
        return bitMap;
    }

    public void setBit(int index) {
        occupied[index] = index;
    }

    public LogicalBlock asBlock(int numberOfBlocks) {
        LogicalBlock block = new LogicalBlock(numberOfBlocks);
        for (int i = 0; i < occupied.length; i++) {
            block.setInt(i, occupied[i]);
        }
        return block;
    }


}
