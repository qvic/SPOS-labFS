package oft;


import exceptions.*;
import fs.FileDescriptor;
import fs.FileDescriptorsArray;
import io.IOSystem;
import io.LogicalBlock;
import util.Config;

public class OpenFileTableEntry {

    private int currentPositionInFile;
    private int currentBlockIndex;
    private int fileLength;

    private byte[] buffer;
    private int descriptorIndex;

    private FileDescriptorsArray descriptors;
    private IOSystem ioSystem;

    public OpenFileTableEntry(FileDescriptorsArray descriptors, IOSystem ioSystem, int descriptorIndex) {
        this.descriptors = descriptors;
        this.ioSystem = ioSystem;
        this.descriptorIndex = descriptorIndex;
        this.currentPositionInFile = 0;
        this.currentBlockIndex = 0;
        this.fileLength = descriptors.getFileLength(descriptorIndex);
        // read ahead
        this.buffer = ioSystem.readBlock(descriptors.getDescriptor(descriptorIndex).getBlockIndexes().get(0)).getBytes();
    }

    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void seekBuffer(int position) throws SeekOutOfFileException {
        if (position > fileLength) {
            throw new SeekOutOfFileException();
        }

        currentPositionInFile = position;
    }

    public void writeToBuffer(byte data) throws FullDiskException, FullDescriptorException {
        int newBlockIndex = currentPositionInFile / Config.BLOCK_SIZE;
        if (newBlockIndex != currentBlockIndex) {
            dumpBuffer();
            if (fileLength <= currentPositionInFile) {
                descriptors.allocateNewBlock(descriptorIndex);
            }
            currentBlockIndex = newBlockIndex;
            updateBuffer();
        }
        buffer[currentPositionInFile % Config.BLOCK_SIZE] = data;
        currentPositionInFile++;
        if (currentPositionInFile > fileLength) {
            fileLength = currentPositionInFile;
        }
    }

    public byte readBuffer() throws ReadOutOfFileException {
        if (currentPositionInFile >= fileLength) throw new ReadOutOfFileException();

        int newBlockIndex = currentPositionInFile / Config.BLOCK_SIZE;
        if (newBlockIndex != currentBlockIndex) {
            dumpBuffer();
            currentBlockIndex = newBlockIndex;
            updateBuffer();
        }

        byte b = buffer[currentPositionInFile % Config.BLOCK_SIZE];

        currentPositionInFile++;

        return b;
    }

    private void setBuffer(byte[] bytes) {
        if (bytes.length != Config.BLOCK_SIZE) {
            throw new IllegalArgumentException("Bytes array must be same size as block");
        }
        buffer = bytes;
    }

    private void dumpBuffer() {
        FileDescriptor descriptor = descriptors.getDescriptor(descriptorIndex);
        int absoluteBlockIndex = descriptor.getBlockIndexes().get(currentBlockIndex);
        ioSystem.writeBlock(absoluteBlockIndex, new LogicalBlock(buffer));
    }

    private void updateBuffer() {
        int absoluteBlockIndex = descriptors.getDescriptor(descriptorIndex).getBlockIndexes().get(currentBlockIndex);
        setBuffer(ioSystem.readBlock(absoluteBlockIndex).getBytes());
    }

    public void closeEntry() {
        FileDescriptor descriptor = descriptors.getDescriptor(descriptorIndex);
        descriptor.setFileLength(fileLength);
        descriptors.insertDescriptor(descriptor, descriptorIndex);
        dumpBuffer();
    }
}
