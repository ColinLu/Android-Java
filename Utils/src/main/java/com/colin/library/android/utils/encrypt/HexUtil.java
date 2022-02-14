package com.colin.library.android.utils.encrypt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.data.Constants;


/**
 * 作者： ColinLu
 * 时间： 2021-08-28 08:22
 * <p>
 * 描述： 十六进制 工具类
 */
public final class HexUtil {


    /*普通字节数组-> 16进制字符串*/
    @Nullable
    public static String getString(@Nullable final String text) {
        return getString(Constants.DIGITS_BYTE_LOWER, StringUtil.getBytes(text));
    }

    /*普通字节数组-> 16进制字符串*/
    @Nullable
    public static String getString(@Nullable final byte[] bytes) {
        return getString(Constants.DIGITS_BYTE_LOWER, bytes);
    }

    /*普通字节数组-> 16进制字符串*/
    @Nullable
    public static String getString(final boolean upper, @Nullable final byte[] bytes) {
        return getString(upper ? Constants.DIGITS_BYTE_UPPER : Constants.DIGITS_BYTE_LOWER, bytes);
    }

    /*普通字节数组-> 16进制字符串*/
    @Nullable
    public static String getString(@NonNull final byte[] digits, @Nullable final byte[] bytes) {
        return StringUtil.getString(getBytes(digits, bytes));
    }

    /*普通字节数组->16进制数组*/
    @Nullable
    public static byte[] getBytes(@NonNull final byte[] digits, @Nullable final byte[] bytes) {
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
