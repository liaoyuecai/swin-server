package com.swin.utils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

public class CoderUtils {

    public static final String STR_CODE = "utf-8";

    public static Integer getInt(ByteBuf buf) {
        return (int) (buf.readInt() & 0xFFFFFFFFL);
    }

    public static Integer getShort(ByteBuf buf) {
        return buf.readShort() & 0xFFFF;
    }

    public static String getString(ByteBuf buf, Integer len) {
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, Charset.forName(STR_CODE));
    }

    public static byte[] getBytes(ByteBuf buf, Integer len) {
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return bytes;
    }

    public static Integer writeStrAndLen16(ByteBuf buf, String str) {
        if (str != null && str.length() > 0) {
            byte[] bytes = str.getBytes(Charset.forName(STR_CODE));
            buf.writeShort(bytes.length);
            buf.writeBytes(bytes);
            return bytes.length;
        } else {
            buf.writeShort(0);
            return 0;
        }
    }

    public static byte[] getBytes(String str) {
        if (str != null && str.length() > 0) {
            return str.getBytes(Charset.forName(STR_CODE));
        } else {
            return null;
        }
    }

    public static Integer writeBytesAndLen32(ByteBuf buf, byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            return bytes.length;
        } else {
            buf.writeInt(0);
            return 0;
        }
    }

}
