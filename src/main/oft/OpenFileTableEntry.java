package main.oft;

import main.fs.FileDescriptor;
import main.io.IOSystem;
import main.io.LogicalBlock;
import main.util.Config;

public class OpenFileTableEntry {

    private static final int BLOCK_SIZE = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));

    private byte[] buffer;
    private int descriptorIndex;
    private int currentPosition;
    private IOSystem ioSystem;

    public OpenFileTableEntry(int descriptorIndex, IOSystem ioSystem) {
        this.ioSystem = ioSystem;
        this.buffer = new byte[BLOCK_SIZE];
        this.descriptorIndex = descriptorIndex;
        this.currentPosition = 0;
    }

    public void writeToBuffer(byte data) {

        if (currentPosition == BLOCK_SIZE) {
            int descriptorsInBlock = Integer.parseInt(Config.INSTANCE.getProperty("descriptorsInBlock"));
            LogicalBlock descriptorBlock = ioSystem.readBlock(descriptorIndex / descriptorsInBlock + 1);
            int nextBlockIndexInDescriptor = currentPosition / BLOCK_SIZE;

            int previousBlockIndex = FileDescriptor
                    .fromBlock(descriptorBlock, descriptorIndex % descriptorsInBlock)
                    .getBlockIndexes()
                    .get(nextBlockIndexInDescriptor - 1);

            int nextBlockIndex = FileDescriptor
                    .fromBlock(descriptorBlock, descriptorIndex % descriptorsInBlock)
                    .getBlockIndexes()
                    .get(nextBlockIndexInDescriptor);

            ioSystem.writeBlock(previousBlockIndex, new LogicalBlock(buffer));
            buffer = ioSystem.readBlock(nextBlockIndex).getBytes();
        }
        int positionInBuffer = currentPosition % BLOCK_SIZE;
        buffer[positionInBuffer] = data;
        currentPosition++;
    }
}
