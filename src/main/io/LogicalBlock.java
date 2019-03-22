package main.io;

import java.util.Arrays;

public class LogicalBlock {

    private int numberOfBytes;
    private byte[] bytes;

    public LogicalBlock(int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
        this.bytes = new byte[numberOfBytes];
    }

    public int getBlockSize() {
        return numberOfBytes;
    }

    public void setByte(int index, byte data) {
        bytes[index] = data;
    }

    public byte getByte(int index) {
        return bytes[index];
    }

    public int getInt(int index) {
        // todo bounds check
        return bytes[index] << 24 |
                (bytes[index + 1] & 0xFF) << 16 |
                (bytes[index + 2] & 0xFF) << 8 |
                (bytes[index + 3] & 0xFF);
    }

    LogicalBlock getCopy() {
        LogicalBlock copy = new LogicalBlock(numberOfBytes);
        copy.bytes = Arrays.copyOf(bytes, numberOfBytes);
        return copy;
    }
}
