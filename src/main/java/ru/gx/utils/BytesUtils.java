package ru.gx.utils;

import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class BytesUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
        /*
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
        */
    }

    public static byte[] intToBytes(int x) {
        buffer.putInt(0, x);
        return buffer.array();
    }

    public static long bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
        /*
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getInt();
        //*/
    }
}