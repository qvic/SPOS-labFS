package io;

import util.Config;
import util.Util;

import java.util.Arrays;

public class LogicalBlock {

    public static final int BYTES_IN_INT = 4;
    private byte[] bytes;

    public LogicalBlock() {
        this.bytes = new byte[Config.BLOCK_SIZE];
    }

    public LogicalBlock(byte[] bytes) {
        if (bytes.length != Config.BLOCK_SIZE) {
            throw new IllegalArgumentException("Bytes array must be same size as block");
        }
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public int getBlockSize() {
        return Config.BLOCK_SIZE;
    }

    public void setByte(int index, byte data) {
        bytes[index] = data;
    }

    public byte getByte(int index) {
        return bytes[index];
    }

    public byte[] getBytes() {
        return Arrays.copyOf(bytes, Config.BLOCK_SIZE);
    }

    public int getInt(int index) {
        int byteIndex = BYTES_IN_INT * index;
        return Util.getIntByBytes(bytes[byteIndex], bytes[byteIndex + 1], bytes[byteIndex + 2], bytes[byteIndex + 3]);
    }

    public void setInt(int index, int data) {
        byte[] result = Util.getBytesByInt(data);
        int byteIndex = BYTES_IN_INT * index;
        bytes[byteIndex] = result[0];
        bytes[byteIndex + 1] = result[1];
        bytes[byteIndex + 2] = result[2];
        bytes[byteIndex + 3] = result[3];
    }

    LogicalBlock getCopy() {
        LogicalBlock copy = new LogicalBlock();
        copy.bytes = getBytes();
        return copy;
    }

    @Override
    public String toString() {
        return "LogicalBlock{" +
                "bytes='" + new String(bytes) +
                "'}";
    }
}
