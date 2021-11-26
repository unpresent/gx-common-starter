package ru.gx.core.utils;

import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class BytesUtils {
    private static ByteBuffer bufferLong = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        bufferLong.putLong(0, x);
        return bufferLong.array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static byte[] intToBytes(int x) {
        bufferLong.putInt(0, x);
        return bufferLong.array();
    }

    public static long bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}