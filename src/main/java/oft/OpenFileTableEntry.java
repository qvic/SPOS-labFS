package oft;


import exceptions.*;
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

    public OpenFileTableEntry(FileDescriptorsArray descriptors, IOSystem ioSystem, int descriptorIndex) {
        this.descriptors = descriptors;
        this.ioSystem = ioSystem;
        this.descriptorIndex = descriptorIndex;
        this.currentPositionInFile = 0;
        this.currentPositionInBuffer = 0;
        this.currentBlockIndex = 0;
        this.fileLength = descriptors.getFileLength(descriptorIndex);
        // read ahead
        this.buffer = ioSystem.readBlock(descriptors.getDescriptor(descriptorIndex).getBlockIndexes().get(0)).getBytes();
    }

    public int getFileLength() {
        return fileLength;
    }

    private void seekBuffer(int position, boolean dump) throws SeekOutOfFileException {
        if (position > fileLength) {
            throw new SeekOutOfFileException();
        }

        int newBlockIndex = position / Config.BLOCK_SIZE;
        if (newBlockIndex != currentBlockIndex) {
            if (dump) {
                dumpBuffer();
            }

            if (descriptors.getDescriptor(descriptorIndex).getBlockIndexes().size() > newBlockIndex) {
                currentBlockIndex = newBlockIndex;
                updateBuffer();
            } else {
                throw new SeekOutOfFileException();
            }
        }

        currentPositionInBuffer = position % Config.BLOCK_SIZE;
        currentPositionInFile = position;
    }

    public void seekBuffer(int position) throws SeekOutOfFileException {
        seekBuffer(position, true);
    }

    public void writeToBuffer(byte data) throws FullDiskException, FullDescriptorException {
        try {
            seekBuffer(currentPositionInFile, true);
        } catch (SeekOutOfFileException e) {
            descriptors.allocateNewBlock(descriptorIndex);

            try {
                seekBuffer(currentPositionInFile, false);
            } catch (SeekOutOfFileException ex) {
                throw new IllegalStateException("Seek index is out of file after allocating new block");
            }
        }
        currentPositionInFile++;
        if (fileLength < currentPositionInFile) {
            fileLength++;
        }
        buffer[currentPositionInBuffer] = data;
    }

    public byte readBuffer() throws ReadOutOfFileException {
        if (currentPositionInFile == fileLength) throw new ReadOutOfFileException();
        try {
            seekBuffer(currentPositionInFile);
        } catch (SeekOutOfFileException e) {
            throw new ReadOutOfFileException();
        }
        currentPositionInFile++;
        return buffer[currentPositionInBuffer];
    }

    private void setBuffer(byte[] bytes) {
        if (bytes.length != Config.BLOCK_SIZE) {
            throw new IllegalArgumentException("Bytes array must be same size as block");
        }
        buffer = bytes;
        currentPositionInBuffer = 0;
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
