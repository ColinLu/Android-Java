package com.colin.library.android.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
    public static boolean isSpace(@Nullable final CharSequence text) {
        if (null == text || text.length() == 0) return true;
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) return false;
        }
        return true;
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


    /*根据Unicode编码判断中文汉字和符号*/
    public static boolean isChinese(final char c) {
        final Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }


}
