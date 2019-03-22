package main.io;

import main.fs.BitMap;
import main.fs.FileDescriptor;
import main.util.Config;

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

    public List<FileDescriptor> loadFileDescriptorsFromBlock(int index) {
        int intsInFileLength = Integer.parseInt(Config.INSTANCE.getProperty("intsInFileLength"));
        int blockIndicesInDescriptor = Integer.parseInt(Config.INSTANCE.getProperty("blockIndicesInDescriptor"));
        int descriptorsInBlock = Integer.parseInt(Config.INSTANCE.getProperty("descriptorsInBlock"));


        LogicalBlock block = readBlock(index);
        for (int i = 0; i < intsInFileLength; i++) {

        }
        block.getInt(intsInFileLength);
        block.getInt(intsInFileLength);
    }

    public LogicalBlock readBlock(int index) {
        return blocks[index].getCopy();
    }

    public void writeBlock(int index, LogicalBlock block) {
        blocks[index] = block;
    }
}
