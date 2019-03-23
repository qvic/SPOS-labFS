package main.io;

import main.fs.BitMap;
import main.fs.Directory;
import main.fs.FileDescriptor;
import main.util.Config;

import java.util.ArrayList;
import java.util.List;

public class IOSystem {

    private LogicalBlock[] blocks;

    public IOSystem() {
        int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
        int blockSize = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));
        blocks = new LogicalBlock[numberOfBlocks];

        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new LogicalBlock(blockSize);
        }
    }

    public void updateFromFile(String filename) {

    }

    public void dumpToFile(String filename) {

    }

    public BitMap loadBitmapFromBlock(int index) {
        return BitMap.fromBlock(blocks.length, readBlock(index));
    }

    public void saveBitmapToBlock(int index, BitMap bitMap) {
        writeBlock(index, bitMap.asBlock(blocks.length));
    }

    public List<FileDescriptor> loadFileDescriptorsFromBlock(int index) {

        int blockIndicesInDescriptor = Integer.parseInt(Config.INSTANCE.getProperty("blockIndicesInDescriptor"));
        int descriptorsInBlock = Integer.parseInt(Config.INSTANCE.getProperty("descriptorsInBlock"));

        List<FileDescriptor> descriptors = new ArrayList<>();
        LogicalBlock block = readBlock(index);

        for (int i = 0; i < descriptorsInBlock; i++) {
            int fileLength = block.getInt(0);
            FileDescriptor descriptor = new FileDescriptor(fileLength);
            for (int j = 1; j < blockIndicesInDescriptor + 1; j++) {
                descriptor.add(block.getInt(j));
            }
        }

        return descriptors;
    }

    public LogicalBlock readBlock(int index) {
        return blocks[index].getCopy();
    }

    public void writeBlock(int index, LogicalBlock block) {
        blocks[index] = block;
    }

    public byte[] loadDataByDescriptor(FileDescriptor descriptor) {
        byte[] bytes = new byte[descriptor.getLength()];
        int length = descriptor.getLength();
        List<Integer> indexes = descriptor.getBlockIndexes();

        int total = 0;
        for (int i = 0; i < indexes.size(); i++) {
            LogicalBlock block = readBlock(i);

            for (int j = 0; j < block.getBlockSize(); j++) {
                bytes[total] = block.getByte(j);

                if (total == length - 1) {
                    break;
                }

                total++;
            }
        }

        return bytes;
    }
}
