package fs;


import exceptions.DiskIsFullException;
import io.LogicalBlock;
import util.Config;

public class BitMap {

    private int[] bitMap;
    private int[] mask;
    private int[] mask2;
    private int numberOfBlocks;

    public BitMap(int numberOfBlocks) {
        this.numberOfBlocks = numberOfBlocks;
        int intsNeeded = numberOfBlocks / 32;
        if (numberOfBlocks % 32 > 0) intsNeeded++;
        this.bitMap = new int[intsNeeded];
        mask = new int[32];
        mask2 = new int[32];
        mask[31] = 1;
        mask2[31] = ~mask[31];
        for (int i = 30; i >= 0; i--) {
            mask[i] = mask[i + 1] << 1;
            mask2[i] = ~mask[i];
        }
    }

    public boolean isFreeBlock(int index) {
        if (index < numberOfBlocks && index >= 0) {
            return ((bitMap[index / 32] & mask[index % 32]) == 0);
        } else throw new IllegalArgumentException("Incorrect block number");
    }

    public static BitMap fromBlock(int numberOfBlocks, LogicalBlock block) {
        BitMap bitMap = new BitMap(numberOfBlocks);
        int intsCount = numberOfBlocks / 32;
        if (numberOfBlocks % 32 > 0) intsCount++;
        for (int i = 0; i < intsCount; i++) {
            bitMap.bitMap[i] = block.getInt(i);
        }
        return bitMap;
    }

    public void setOccupied(int index) {
        if (index < numberOfBlocks && index >= 0)
            bitMap[index / 32] = bitMap[index / 32] | mask[index % 32];
        else throw new IllegalArgumentException("Incorrect block number");
    }

    public void setFree(int index) {
        if (index < numberOfBlocks && index >= 0)
            bitMap[index / 32] = bitMap[index / 32] & mask2[index % 32];
        else throw new IllegalArgumentException("Incorrect block number");
    }

    public int findFreeBlock() throws DiskIsFullException {
        for (int i = 0; i < numberOfBlocks; i++) {
            if (isFreeBlock(i)) {
                return i;
            }
        }
        throw new DiskIsFullException();
    }

    public LogicalBlock asBlock() {
        LogicalBlock block = new LogicalBlock();
        for (int i = 0; i < bitMap.length; i++) {
            block.setInt(i, bitMap[i]);
        }
        return block;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("BitMap{occupied=");
        for (int i = 0; i < bitMap.length; i++) {
            result.append(String.format("%32s", Integer.toBinaryString(bitMap[i])).replace(' ', '0'));
        }
        result.append('}');
        return result.toString();
    }
}
