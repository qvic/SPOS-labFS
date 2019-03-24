package main.fs;

import main.exceptions.FileIsFullException;
import main.io.IOSystem;
import main.io.LogicalBlock;
import main.oft.OpenFileTable;
import main.util.Config;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystem {

    private static final Logger LOGGER = Logger.getLogger(FileSystem.class.getName());

    private static final int BLOCKS_FOR_DESCRIPTORS = Integer.parseInt(Config.INSTANCE.getProperty("blocksForDescriptors"));

    private final OpenFileTable oft;
    private final IOSystem ioSystem;

    public FileSystem() {
        ioSystem = new IOSystem();
        oft = new OpenFileTable(ioSystem);

        // filling some data for tests
        int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
        BitMap bitMap = BitMap.fromBlock(numberOfBlocks, ioSystem.readBlock(0));
        bitMap.setOccupied(63);
        ioSystem.writeBlock(0, bitMap.asBlock());

        bitMap = BitMap.fromBlock(numberOfBlocks, ioSystem.readBlock(0));
        System.out.println(bitMap);

        FileDescriptor newDescriptor = new FileDescriptor(1);

        LogicalBlock newBlock = new LogicalBlock();

        byte[] bytes = "Hello world".getBytes();
        for (int i = 0; i < bytes.length; i++) {
            newBlock.setByte(i, bytes[i]);
        }

        ioSystem.writeBlock(7, newBlock);
        newDescriptor.add(7);

        LogicalBlock block = ioSystem.readBlock(1);
        newDescriptor.insertToBlock(block, 0);
        ioSystem.writeBlock(1, block);

        FileDescriptor directoryDescriptor = FileDescriptor.fromBlock(ioSystem.readBlock(1), 0);
//        Directory directory = new Directory(directoryDescriptor);

        List<Integer> blockIndexes = directoryDescriptor.getBlockIndexes();
        for (Integer blockIndex : blockIndexes) {
            System.out.println(ioSystem.readBlock(blockIndex));
        }

        //


    }

    public void create(String name) {
        LOGGER.log(Level.INFO, String.format("Create: name=%s", name));
    }

    public void destroy(String name) {
        LOGGER.log(Level.INFO, String.format("Destroy: name=%s", name));
    }

    public void open(String name) {
        LOGGER.log(Level.INFO, String.format("Open: name=%s", name));
    }

    public void close(int index) {
        LOGGER.log(Level.INFO, String.format("Close: index=%d", index));
    }

    public void read(int index, int count) {
        LOGGER.log(Level.INFO, String.format("Read: index=%d, count=%d", index, count));
    }

    public void write(int index, byte data, int count) {
        LOGGER.log(Level.INFO, String.format("Write: index=%d, data=%c, count=%d", index, data, count));
    }

    public void seek(int index, int position) {
        LOGGER.log(Level.INFO, String.format("Seek: index=%d, position=%d", index, position));
    }

    public void directory() {
        LOGGER.log(Level.INFO, "Directory");
    }

    public void input(String name) {
        LOGGER.log(Level.INFO, String.format("Input: name=%s", name));
    }

    public void save(String name) {
        LOGGER.log(Level.INFO, String.format("Save: name=%s", name));
    }

    private void write(FileDescriptor descriptor, byte[] bytes) throws FileIsFullException {
        int length = descriptor.getLength();
        int blockSize = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));
        int blockIndicesInDescriptor = Integer.parseInt(Config.INSTANCE.getProperty("blockIndicesInDescriptor"));

        List<Integer> blockIndexes = descriptor.getBlockIndexes();

        int blockToWrite = length / blockSize;

        if (blockIndexes.size() <= blockToWrite) {

            if (blockIndexes.size() < blockIndicesInDescriptor) {
                int newBlockIndex = findFreeBlock();
                blockIndexes.add(newBlockIndex);
            } else {
                throw new FileIsFullException();
            }
        }
    }

    private int findFreeBlock() {
        return 0;
    }
}
