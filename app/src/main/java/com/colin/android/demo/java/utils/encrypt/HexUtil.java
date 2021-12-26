package com.colin.android.demo.java.utils.encrypt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.utils.StringUtil;
import com.colin.android.demo.java.utils.data.Constants;

/**
 * 作者： ColinLu
 * 时间： 2021-08-28 08:22
 * <p>
 * 描述： 十六进制 工具类
 */
public final class HexUtil {

    /*普通字节数组-> 16进制字符串*/
    @Nullable
    public static String getString(@Nullable final byte[] bytes) {
        return getString(bytes, Constants.DIGITS_BYTE_LOWER);
    }

    /*普通字节数组-> 16进制字符串*/
    @Nullable
    public static String getString(@Nullable final byte[] bytes, final boolean upper) {
        return getString(bytes, upper ? Constants.DIGITS_BYTE_UPPER : Constants.DIGITS_BYTE_LOWER);
    }

    /*普通字节数组-> 16进制字符串*/
    @Nullable
    public static String getString(@Nullable final byte[] bytes, @NonNull final byte[] digits) {
        return StringUtil.getString(getBytes(bytes, digits));
    }

    /*普通字节数组->16进制数组*/
    @Nullable
    public static byte[] getBytes(@Nullable final byte[] bytes, @NonNull final byte[] digits) {
        final int len = null == bytes ? 0 : bytes.length;
        if (len == 0) return null;
        final byte[] values = new byte[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            values[j++] = digits[bytes[i] >> 4 & 0X0F];
            values[j++] = digits[bytes[i] & 0X0F];
        }
        return values;
    }
}
