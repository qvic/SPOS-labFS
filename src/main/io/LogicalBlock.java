package main.io;

import main.util.Util;

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
        return Util.getIntByBytes(bytes[index], bytes[index + 1], bytes[index + 2], bytes[index + 3]);
    }

    public void setInt(int index, int data) {
        byte[] result = Util.getBytesByInt(data);
        bytes[index] = result[0];
        bytes[index + 1] = result[1];
        bytes[index + 2] = result[2];
        bytes[index + 3] = result[3];
    }

    LogicalBlock getCopy() {
        LogicalBlock copy = new LogicalBlock(numberOfBytes);
        copy.bytes = Arrays.copyOf(bytes, numberOfBytes);
        return copy;
    }
}
