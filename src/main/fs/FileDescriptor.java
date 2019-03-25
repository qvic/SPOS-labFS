package main.fs;

import main.io.LogicalBlock;
import main.util.Config;
import main.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FileDescriptor {

    private static int MAX_BLOCK_INDICES = Integer.parseInt(Config.INSTANCE.getProperty("blockIndicesInDescriptor"));
    private static int BLOCK_SIZE = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));

    public void setLength(int length) {
        this.length = length;
    }

    private int length;
    private List<Integer> blockIndexes;

    public FileDescriptor(int length) {
        this.length = length;
        this.blockIndexes = new ArrayList<>();
    }

    public void add(int... indices) {
        if (blockIndexes.size() == MAX_BLOCK_INDICES)
            throw new IllegalStateException("Can't add more indices to block");

        for (int index : indices) {
            blockIndexes.add(index);
        }
    }

    public List<Integer> getBlockIndexes() {
        return blockIndexes;
    }

    public int getLength() {
        return length;
    }

    public int[] asInts() {
        int blockIndicesInDescriptor = length / BLOCK_SIZE + 1;

        if (blockIndexes.size() > 1 && blockIndicesInDescriptor != blockIndexes.size()) {
            throw new IllegalStateException("Length is not correct");
        }

        int totalSizeInInts = 1 + blockIndicesInDescriptor;
        int[] ints = new int[totalSizeInInts];

        ints[0] = length;

        for (int i = 0; i < blockIndexes.size(); i++) {
            ints[i + 1] = blockIndexes.get(i);
        }

        return ints;
    }

    public static FileDescriptor fromBlock(LogicalBlock block, int positionInBlock) {
        int intPositionInBlock = (1 + MAX_BLOCK_INDICES) * positionInBlock;
        int length = block.getInt(intPositionInBlock);
        int blockIndicesInDescriptor = length / BLOCK_SIZE + 1;

        FileDescriptor descriptor = new FileDescriptor(length);
        for (int i = 0; i < blockIndicesInDescriptor; i++) {
            descriptor.add(block.getInt(intPositionInBlock + i + 1));
        }
        return descriptor;
    }

    public void insertToBlock(LogicalBlock block, int positionInBlock) {
        int[] ints = asInts();

        for (int i = 0; i < ints.length; i++) {
            block.setInt(positionInBlock + i, ints[i]);
        }
    }

    @Override
    public String toString() {
        return "FileDescriptor{" +
                "length=" + length +
                ", blockIndexes=" + blockIndexes +
                '}';
    }
}
