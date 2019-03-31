package oft;

import exceptions.BlockIsFullException;
import exceptions.DiskIsFullException;
import fs.BitMap;
import fs.FileDescriptor;
import io.IOSystem;
import io.LogicalBlock;
import util.Config;

import java.util.ArrayList;
import java.util.List;

public class OpenFileTable {

    private List<OpenFileTableEntry> table;
    private IOSystem ioSystem;

    public OpenFileTable(IOSystem ioSystem) {
        this.ioSystem = ioSystem;
        this.table = new ArrayList<>();

        int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
        int blocksForDescriptors = Integer.parseInt(Config.INSTANCE.getProperty("blocksForDescriptors"));
        int blockSize=Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));
        BitMap bitMap = BitMap.fromBlock(numberOfBlocks, ioSystem.readBlock(0));
        bitMap.setOccupied(0);
        LogicalBlock descriptorBlock=new LogicalBlock();
        for(int i=0;i<blockSize/16;i++){
            descriptorBlock.setInt(i*4,-1);
        }
        for (int i = 0; i < blocksForDescriptors; i++) {
            bitMap.setOccupied(i + 1);
            ioSystem.writeBlock(i+1,descriptorBlock);
        }

        FileDescriptor directoryDescriptor = new FileDescriptor(0);
        try {
            int freeBlock = findFreeBlock(bitMap);
            directoryDescriptor.add(freeBlock);
            bitMap.setOccupied(freeBlock);
        } catch (DiskIsFullException e) {
            throw new RuntimeException("Disk is full");
        }

        ioSystem.writeBlock(0, bitMap.asBlock());

        LogicalBlock block = ioSystem.readBlock(1);
        directoryDescriptor.insertToBlock(block, 0);
        ioSystem.writeBlock(1, block);

        // directory
        table.add(0, new OpenFileTableEntry(0, directoryDescriptor.getLength()));

        add("Hello sdhfhsfh sdhkf hjkj hsdf hjksh dhkhksgjd asgdg ghahs 32 234 23 23 324 23434 234 2 42 43 78345 78 834578 378483899hksdfhkh hsdfkj hj");
    }

    public void add(String name) {
        /*
        search directory to find index of file descriptor (i)
        allocate a free OpenFileTable entry (reuse deleted entries)
        fill in current position (0) and file descriptor index (i)
        read block 0 of file into the r/w buffer (read-ahead)
        return OpenFileTable index (j) (or return error)
        consider adding a file length field (to simplify checking)
        */

        byte[] bytes = name.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            try {
                writeByte(0, bytes[i]);
            } catch (DiskIsFullException e) {
                e.printStackTrace();
                return;
            }
        }
    }


    private void writeByte(int oftIndex, byte data) throws DiskIsFullException {
        OpenFileTableEntry entry = table.get(oftIndex);

        try {
            entry.writeToBuffer(data);

        } catch (BlockIsFullException e) {

            int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
            int descriptorsInBlock = Integer.parseInt(Config.INSTANCE.getProperty("descriptorsInBlock"));
            int blockSize = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));

            LogicalBlock descriptorBlock = ioSystem.readBlock(entry.getDescriptorIndex() / descriptorsInBlock + 1);
            int nextBlockIndexInDescriptor = entry.getCurrentPositionInFile() / blockSize;

            FileDescriptor descriptor = FileDescriptor.fromBlock(descriptorBlock, entry.getDescriptorIndex() % descriptorsInBlock);
            List<Integer> blockIndexes = descriptor.getBlockIndexes();

            ioSystem.writeBlock(blockIndexes.get(nextBlockIndexInDescriptor - 1), new LogicalBlock(entry.getBuffer()));

            if (nextBlockIndexInDescriptor >= blockIndexes.size()) {

                BitMap bitMap = BitMap.fromBlock(numberOfBlocks, ioSystem.readBlock(0));
                int freeBlock = findFreeBlock(bitMap);
                descriptor.add(freeBlock);
                bitMap.setOccupied(freeBlock);
                ioSystem.writeBlock(0, bitMap.asBlock());

                descriptor.setLength(entry.getFileLength());
                descriptor.insertToBlock(descriptorBlock, entry.getDescriptorIndex() % descriptorsInBlock);
                ioSystem.writeBlock(entry.getDescriptorIndex() / descriptorsInBlock + 1, descriptorBlock);
            }

            entry.setBuffer(ioSystem.readBlock(blockIndexes.get(nextBlockIndexInDescriptor)).getBytes());

            try {
                entry.writeToBuffer(data);
            } catch (BlockIsFullException e1) {
                throw new RuntimeException("Can't write after new block allocation");
            }
        }
    }

    private int findFreeBlock(BitMap bitMap) throws DiskIsFullException {
        int numberOfBlocks = Integer.parseInt(Config.INSTANCE.getProperty("blocks"));
        System.out.println(bitMap);
        for (int i = 0; i < numberOfBlocks; i++) {
            if (bitMap.isFreeBlock(i)) {
                return i;
            }
        }
        throw new DiskIsFullException();
    }
}
