package com.colin.library.android.utils;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 作者： ColinLu
 * 时间： 2021-08-21 00:27
 * <p>
 * 描述： String、CharSequence 工具类
 */
public final class StringUtil {
    private StringUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static boolean isEmpty(@Nullable final CharSequence... texts) {
        final int len = texts == null ? 0 : texts.length;
        if (len == 0) return true;
        for (int i = 0; i < len; i++) {
            final CharSequence text = texts[i];
            if (isEmpty(text)) return true;
        }
        return false;
    }

    public static boolean isEmpty(@Nullable final CharSequence text) {
        return text == null || text.length() == 0;
    }

    /*是空白*/
    public static boolean isSpace(@Nullable final CharSequence... texts) {
        final int len = texts == null ? 0 : texts.length;
        if (len == 0) return true;
        for (int i = 0; i < len; i++) {
            final CharSequence text = texts[i];
            if (isSpace(text)) return true;
        }
        return false;
    }

    /*是空白*/
    public static boolean isSpace(@Nullable final CharSequence text) {
        if (null == text || text.length() == 0) return true;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) return false;
        }
        return true;
    }


    /*是否相同*/
    public static boolean equals(@Nullable final CharSequence a, @Nullable final CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) if (a.charAt(i) != b.charAt(i)) return false;
                return true;
            }
        }
        return false;
    }


    /*普通字符串 转 字节数组*/
    @Nullable
    public static byte[] getBytes(@Nullable final CharSequence data) {
        return getBytes(data == null ? null : data.toString(), StandardCharsets.UTF_8);
    }

    /*普通字符串 转 字节数组*/
    @Nullable
    public static byte[] getBytes(@Nullable final String data) {
        return getBytes(data, StandardCharsets.UTF_8);
    }

    /*普通字符串 转 字节数组*/
    @Nullable
    public static byte[] getBytes(@Nullable final CharSequence data, @NonNull final Charset charset) {
        return getBytes(data == null ? null : data.toString(), charset);
    }

    /*普通字符串 转 字节数组*/
    @Nullable
    public static byte[] getBytes(@Nullable final String data, @NonNull final Charset charset) {
        return isEmpty(data) ? null : data.getBytes(charset);
    }


    /*char[] 转 byte[]*/
    @Nullable
    public static byte[] getBytes(@Nullable final char[] chars) {
        final int len = chars == null ? 0 : chars.length;
        if (len == 0) return null;
        final byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (chars[i]);
        }
        return bytes;
    }

    /*bytes[] 转 char[]*/
    @Nullable
    public static char[] getChars(@Nullable final byte[] bytes) {
        final int len = bytes == null ? 0 : bytes.length;
        if (len == 0) return null;
        final char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & 0xFF);
        }
        return chars;
    }

    /*普通字符串 转 字节数组*/
    @Nullable
    public static String getString(@Nullable final byte[] bytes) {
        return ArrayUtil.isEmpty(bytes) ? null : new String(bytes);
    }

    /*普通字符串 转 字节数组*/
    @Nullable
    public static String getString(@Nullable final byte[] bytes, @NonNull final Charset charset) {
        return ArrayUtil.isEmpty(bytes) ? null : new String(bytes, charset);
    }

    /*普通字符串 转 字节数组*/
    @Nullable
    public static String getString(@NonNull final AssetManager manager, @NonNull final String fileName) {
        InputStream in = null;
        BufferedReader reader = null;
        try {
            in = manager.open(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            final StringBuilder sb = new StringBuilder();
            do {
                line = reader.readLine();
                // 去除注释
                if (line != null && !line.matches("^\\s*\\/\\/.*")) sb.append(line);
            } while (line != null);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(in, reader);
        }
        return null;
    }


    /*根据Unicode编码判断中文汉字和符号*/
    public static boolean isChinese(final char c) {
        final Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }

    public static String toString(@Nullable Object object) {
        if (object == null) return "null";
        if (!object.getClass().isArray()) return object.toString();
        if (object instanceof boolean[]) return Arrays.toString((boolean[]) object);
        if (object instanceof byte[]) return Arrays.toString((byte[]) object);
        if (object instanceof char[]) return Arrays.toString((char[]) object);
        if (object instanceof short[]) return Arrays.toString((short[]) object);
        if (object instanceof int[]) return Arrays.toString((int[]) object);
        if (object instanceof long[]) return Arrays.toString((long[]) object);
        if (object instanceof float[]) return Arrays.toString((float[]) object);
        if (object instanceof double[]) return Arrays.toString((double[]) object);
        if (object instanceof Object[]) return Arrays.deepToString((Object[]) object);
        return "Couldn't find a correct type for the object";
    }
}
