package com.colin.library.android.utils.encrypt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.data.Constants;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * 作者： ColinLu
 * 时间： 2021-08-28 08:22
 * <p>
 * 描述： 十六进制 工具类
 */
public final class HexUtil {

    /**
     * 默认字符串转16进制字符串
     *
     * @param text 普通字符串
     * @return 16进制字符串：Colin->436f6c696e
     */
    @Nullable
    public static String getHex(@Nullable final String text) {
        return getHex(StringUtil.getBytes(text));
    }

    /**
     * 默认字节数组转16进制
     *
     * @param bytes 字节数组
     * @return 16进制字符串：Colin->436f6c696e
     */
    @Nullable
    public static String getHex(@Nullable final byte[] bytes) {
        return toHex(bytes);
    }

    /*normal to hex*/
    @Nullable
    public static String getHex(final boolean upper, @Nullable final byte[] bytes) {
        return getHex(upper ? Constants.DIGITS_BYTE_UPPER : Constants.DIGITS_BYTE_LOWER, bytes);
    }

    /*normal to hex*/
    @Nullable
    public static String getHex(@NonNull final byte[] digits, @Nullable final byte[] bytes) {
        return StringUtil.getString(toHex(digits, bytes));
    }

    /*normal to hex*/

    /**
     * Converts an array of bytes to a hexadecimal representation.
     *
     * @param digits The array of bytes representing hexadecimal digits.
     * @param bytes  The array of bytes to convert.
     * @return The hexadecimal representation of the input bytes, or null if either input array is null or the bytes array is empty.
     */
    @Nullable
    public static byte[] toHex(@NonNull final byte[] digits, @Nullable final byte[] bytes) {
        final int len = null == bytes ? 0 : bytes.length;
        if (len == 0) return null;
        final byte[] values = new byte[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            values[j++] = digits[bytes[i] >> 4 & 0X0F];
            values[j++] = digits[bytes[i] & 0X0F];
        }
        return values;
    }

    @Nullable
    public static String toHex(@Nullable final String text) {
        return toHex(StringUtil.getBytes(text));
    }

    @Nullable
    public static String toHex(@Nullable final byte[] bytes) {
        int length = bytes == null ? Constants.ZERO : bytes.length;
        if (length == Constants.ZERO) return null;
        return IntStream.range(0, length).mapToObj(i -> Integer.toHexString(bytes[i])).collect(Collectors.joining());
    }


    /**
     * 十六进制字符串转二进制数组
     *
     * @param hex string of hex-encoded values
     * @return decoded byte array
     * eg:436f6c696e->[67, 111, 108, 105, 110]
     */
    @Nullable
    public static byte[] getBytes(@Nullable final String hex) {
        final int len = hex == null ? Constants.ZERO : hex.length();
        if (len == Constants.ZERO) return null;
        final byte[] data = new byte[(len >> 1)];
        for (int i = 0; i < len; i += 2) {
            data[(i >> 1)] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 十六进制字符串转Java String
     *
     * @param hex string of hex-encoded values
     * @return java string
     * eg:436f6c696e->Colin
     */
    @Nullable
    public static String getString(@Nullable final String hex) {
        return getString(hex, StandardCharsets.UTF_8);
    }

    /**
     * 十六进制字符串转Java String
     *
     * @param hex     string of hex-encoded values
     * @param charset string of charset
     * @return java string
     * eg:436f6c696e->Colin
     */
    @Nullable
    public static String getString(@Nullable final String hex, @NonNull final Charset charset) {
        if (StringUtil.isEmpty(hex)) return hex;
        return new String(new BigInteger(hex, Constants.RADIX_HEX).toByteArray(), charset);
    }
//    public static String getString(@Nullable final String hex) {
//        if (StringUtil.isEmpty(hex)) return null;
//        return StringUtil.getString(getBytes(hex.replace(" ", "")), StandardCharsets.UTF_8);
//    }
}
