package ru.gx.core.utils;

import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class BytesUtils {
    public static byte[] longToBytes(long x) {
        return ByteBuffer
                .allocate(Long.BYTES)
                .putLong(0, x)
                .array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    public static byte[] intToBytes(int x) {
        return ByteBuffer
                .allocate(Long.BYTES)
                .putInt(0, x)
                .array();
    }

    public static long bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}