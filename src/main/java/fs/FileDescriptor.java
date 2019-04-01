package fs;

import io.LogicalBlock;
import util.Config;

import java.util.ArrayList;
import java.util.List;

public class FileDescriptor {

    private int length;
    private List<Integer> blockIndexes;

    public FileDescriptor(int length) {
        this.length = length;
        this.blockIndexes = new ArrayList<>();
    }

    public void setFileLength(int length) {
        if (length >= Config.BLOCK_SIZE * blockIndexes.size()) {
            throw new IllegalArgumentException("Can't set length greater than maximum length for added blocks");
        }
        int blockIndicesInDescriptor = length / Config.BLOCK_SIZE + 1;
        if (blockIndicesInDescriptor != blockIndexes.size()) {
            throw new IllegalStateException("Number of indexes in descriptor is not correct");
        }
        this.length = length;
    }

    public void add(int... indices) {
        if (blockIndexes.size() == Config.BLOCK_INDICES_IN_DESCRIPTOR)
            throw new IllegalStateException("Can't add more indices to block");

        for (int index : indices) {
            blockIndexes.add(index);
        }
    }

    public List<Integer> getBlockIndexes() {
        return blockIndexes;
    }

    public int getFileLength() {
        return length;
    }

    public int[] asInts() {
        int blockIndicesInDescriptor = length / Config.BLOCK_SIZE + 1;

        if (blockIndicesInDescriptor != blockIndexes.size()) {
            throw new IllegalStateException("Number of indexes in descriptor is not correct");
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
        int positionInBlockInInts = (1 + Config.BLOCK_INDICES_IN_DESCRIPTOR) * positionInBlock;
        int length = block.getInt(positionInBlockInInts);
        int blockIndicesInDescriptor = length / Config.BLOCK_SIZE + 1;

        if (blockIndicesInDescriptor > Config.BLOCK_INDICES_IN_DESCRIPTOR) {
            throw new IllegalStateException("Length of file is too big");
        }

        FileDescriptor descriptor = new FileDescriptor(length);
        for (int i = 0; i < blockIndicesInDescriptor; i++) {
            int blockIndex = block.getInt(positionInBlockInInts + i + 1);
            if (blockIndex == 0) {
                throw new IllegalStateException("Number of indexes in descriptor is not correct");
            }
            descriptor.add(blockIndex);
        }
        return descriptor;
    }

    @Override
    public String toString() {
        return "FileDescriptor{" +
                "length=" + length +
                ", blockIndexes=" + blockIndexes +
                '}';
    }
}
