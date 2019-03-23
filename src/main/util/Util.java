package main.util;

public class Util {

    public static int getIntByBytes(byte a, byte b, byte c, byte d) {
        // todo bounds check
        return a << 24 |
                (b & 0xFF) << 16 |
                (c & 0xFF) << 8 |
                (d & 0xFF);
    }

    public static byte[] getBytesByInt(int data) {
        byte[] result = new byte[4];
        result[0] = (byte) (data >> 24);
        result[1] = (byte) (data >> 16);
        result[2] = (byte) (data >> 8);
        result[3] = (byte) data;
        return result;
    }
}
