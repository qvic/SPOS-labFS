package fs;

import exceptions.DescriptorIsFullException;
import exceptions.DiskIsFullException;
import exceptions.NoFreeDescriptorsException;
import io.IOSystem;
import io.LogicalBlock;
import util.Config;

public class FileDescriptorsArray {

    private final IOSystem ioSystem;
    private final FileDescriptor emptyDescriptor;

    public FileDescriptorsArray(IOSystem ioSystem) {
        this.ioSystem = ioSystem;

        BitMap bitMap = BitMap.fromBlock(Config.BLOCKS, ioSystem.readBlock(0));
        int descriptorsOffset = 1;
        for (int i = 0; i < Config.BLOCKS_FOR_DESCRIPTORS; i++) {
            bitMap.setOccupied(i + descriptorsOffset);
        }
        ioSystem.writeBlock(0, bitMap.asBlock());

        this.emptyDescriptor = new FileDescriptor(0);
        emptyDescriptor.add(0);
    }

    public void addDescriptor(int descriptorIndex) throws DiskIsFullException {
        FileDescriptor descriptor = new FileDescriptor(0);

        BitMap bitMap = BitMap.fromBlock(Config.BLOCKS, ioSystem.readBlock(0));
        int freeBlock = bitMap.findFreeBlock();
        descriptor.add(freeBlock);
        bitMap.setOccupied(freeBlock);

        insertDescriptor(descriptor, descriptorIndex);
        ioSystem.writeBlock(0, bitMap.asBlock());
    }

    public void removeDescriptor(int descriptorIndex) {
        BitMap bitMap = BitMap.fromBlock(Config.BLOCKS, ioSystem.readBlock(0));
        FileDescriptor descriptorToRemove = getDescriptor(descriptorIndex);
        for (int blockIndex : descriptorToRemove.getBlockIndexes()) {
            bitMap.setFree(blockIndex);
        }
        ioSystem.writeBlock(0, bitMap.asBlock());
        insertDescriptor(emptyDescriptor, descriptorIndex);
    }

    public void insertDescriptor(FileDescriptor descriptor, int position) {
        if (position < 0 || position > Config.BLOCKS_FOR_DESCRIPTORS * Config.DESCRIPTORS_IN_BLOCK) {
            throw new IllegalArgumentException("position is out of range");
        }

        int blockIndex = position / Config.DESCRIPTORS_IN_BLOCK + 1;
        LogicalBlock block = ioSystem.readBlock(blockIndex);
        insertDescriptorToBlock(descriptor, position % Config.DESCRIPTORS_IN_BLOCK, block);
        ioSystem.writeBlock(blockIndex, block);
    }

    private void insertDescriptorToBlock(FileDescriptor descriptor, int positionInBlock, LogicalBlock block) {
        int[] ints = descriptor.asInts();

        for (int i = 0; i < ints.length; i++) {
            block.setInt((1 + Config.BLOCK_INDICES_IN_DESCRIPTOR) * positionInBlock + i, ints[i]);
        }
    }

    public int findFreeDescriptorIndex() throws NoFreeDescriptorsException {
        for (int i = 1; i <= Config.BLOCKS_FOR_DESCRIPTORS; i++) {
            for (int j = 0; j < Config.DESCRIPTORS_IN_BLOCK; j++) {
                FileDescriptor descriptor = FileDescriptor.fromBlock(ioSystem.readBlock(i), j);
                if (descriptor == null) {
                    return ((i - 1) * Config.DESCRIPTORS_IN_BLOCK + j);
                }
            }
        }
        throw new NoFreeDescriptorsException();
    }

    public FileDescriptor getDescriptor(int descriptorIndex) {
        LogicalBlock descriptorBlock = ioSystem.readBlock(descriptorIndex / Config.DESCRIPTORS_IN_BLOCK + 1);
        return FileDescriptor.fromBlock(descriptorBlock, descriptorIndex % Config.DESCRIPTORS_IN_BLOCK);
    }

    public void allocateNewBlock(int descriptorIndex) throws DiskIsFullException, DescriptorIsFullException {
        FileDescriptor descriptor = getDescriptor(descriptorIndex);
        if (descriptor.isFull()) {
            throw new DescriptorIsFullException();
        }

        BitMap bitMap = BitMap.fromBlock(Config.BLOCKS, ioSystem.readBlock(0));
        int freeBlock = bitMap.findFreeBlock();
        bitMap.setOccupied(freeBlock);
        descriptor.add(freeBlock);
        // allocation of new block means that file is full
        descriptor.setFileLength((descriptor.getBlockIndexes().size() - 1) * Config.BLOCK_SIZE);
        insertDescriptor(descriptor, descriptorIndex);
        ioSystem.writeBlock(0, bitMap.asBlock());
    }

    public int getFileLength(int descriptorIndex) {
        FileDescriptor descriptor = getDescriptor(descriptorIndex);
        return descriptor.getFileLength();
    }

    public void updateFileLength(int descriptorIndex, int fileLength) {
        FileDescriptor descriptor = getDescriptor(descriptorIndex);
        descriptor.setFileLength(fileLength);
        insertDescriptor(descriptor, descriptorIndex);
    }
}
