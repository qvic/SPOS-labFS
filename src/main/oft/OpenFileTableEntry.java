package main.oft;

import main.exceptions.BlockIsFullException;
import main.util.Config;

public class OpenFileTableEntry {

    private static final int BLOCK_SIZE = Integer.parseInt(Config.INSTANCE.getProperty("blockSize"));

    private int currentPositionInBuffer;
    private byte[] buffer;
    private int fileLength;
    private int descriptorIndex;
    private int currentPositionInFile;

    public int getFileLength() {
        return fileLength;
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public int getCurrentPositionInFile() {
        return currentPositionInFile;
    }

    public OpenFileTableEntry(int descriptorIndex, int fileLength) {
        this.fileLength = fileLength;
        this.buffer = new byte[BLOCK_SIZE];
        this.descriptorIndex = descriptorIndex;
        this.currentPositionInFile = 0;
        this.currentPositionInBuffer = 0;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void writeToBuffer(byte data) throws BlockIsFullException {

        if (currentPositionInBuffer == BLOCK_SIZE) {
            throw new BlockIsFullException();
        }

        buffer[currentPositionInBuffer] = data;
        currentPositionInFile++;
        currentPositionInBuffer++;

        if (currentPositionInFile > fileLength) {
            fileLength = currentPositionInFile;
        }
    }

    public void setBuffer(byte[] bytes) {
        if (bytes.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Bytes array must be same size as block");
        }
        buffer = bytes;
        currentPositionInBuffer = 0;
    }
}
