package oft;


import exceptions.*;
import fs.BitMap;
import fs.FileDescriptor;
import fs.FileDescriptorsArray;
import io.IOSystem;
import io.LogicalBlock;
import util.Config;

public class OpenFileTableEntry {

    private int currentPositionInBuffer;
    private int currentPositionInFile;
    private int currentBlockIndex;
    private int fileLength;

    private byte[] buffer;
    private int descriptorIndex;

    private FileDescriptorsArray descriptors;
    private IOSystem ioSystem;

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public int getCurrentPositionInFile() {
        return currentPositionInFile;
    }

    public OpenFileTableEntry(FileDescriptorsArray descriptors, IOSystem ioSystem, int descriptorIndex) {
        this.descriptors = descriptors;
        this.ioSystem = ioSystem;
        this.descriptorIndex = descriptorIndex;
        this.currentPositionInFile = 0;
        this.currentPositionInBuffer = 0;
        this.currentBlockIndex = 0;
        this.fileLength = 0;
        this.buffer = ioSystem.readBlock(descriptors.getDescriptor(descriptorIndex).getBlockIndexes().get(0)).getBytes();
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void seekBuffer(int position) throws SeekOutOfFileException {
        if (position > fileLength) {
            throw new SeekOutOfFileException();
        }

        // if buffer just switched to new
        if (position / Config.BLOCK_SIZE != currentBlockIndex) {
            dumpBuffer();
            currentBlockIndex = position / Config.BLOCK_SIZE;
            updateBuffer();
        }

        currentPositionInBuffer = position % Config.BLOCK_SIZE;
        currentPositionInFile = position;
    }

    public void writeToBuffer(byte data) throws DiskIsFullException {
        buffer[currentPositionInBuffer] = data;
        int nextPositionInFile = currentPositionInFile + 1;
        try {
            seekBuffer(nextPositionInFile);
        } catch (SeekOutOfFileException e) {
            fileLength++;

            if (currentPositionInBuffer + 1 == Config.BLOCK_SIZE) {
                descriptors.allocateNewBlock(descriptorIndex);
            }

            try {
                seekBuffer(nextPositionInFile);
            } catch (SeekOutOfFileException ex) {
                throw new IllegalStateException("Seek index is out of file after allocating new block");
            }
        }
    }

    public byte readBuffer() throws ReadOutOfFileException {
        if (currentPositionInFile == fileLength) throw new ReadOutOfFileException();

        byte value = buffer[currentPositionInBuffer];
        try {
            seekBuffer(currentPositionInFile + 1);
        } catch (SeekOutOfFileException e) {
            throw new ReadOutOfFileException();
        }
        return value;
    }

    private void setBuffer(byte[] bytes) {
        if (bytes.length != Config.BLOCK_SIZE) {
            throw new IllegalArgumentException("Bytes array must be same size as block");
        }
        buffer = bytes;
        currentPositionInBuffer = 0;
    }

    private void dumpBuffer() {
        int absoluteBlockIndex = descriptors.getDescriptor(descriptorIndex).getBlockIndexes().get(currentBlockIndex);
        ioSystem.writeBlock(absoluteBlockIndex, new LogicalBlock(buffer));
    }

    private void updateBuffer() {
        int absoluteBlockIndex = descriptors.getDescriptor(descriptorIndex).getBlockIndexes().get(currentBlockIndex);
        setBuffer(ioSystem.readBlock(absoluteBlockIndex).getBytes());
    }
}
