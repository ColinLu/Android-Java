package com.colin.library.android.utils.encrypt;

import android.text.TextUtils;

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


    /*normal to  hex*/
    @Nullable
    public static String getString(@Nullable final String text) {
        return getString(Constants.DIGITS_BYTE_LOWER, StringUtil.getBytes(text));
    }

    /*normal to hex*/
    @Nullable
    public static String getString(@Nullable final byte[] bytes) {
        return getString(Constants.DIGITS_BYTE_LOWER, bytes);
    }

    /*normal to hex*/
    @Nullable
    public static String getString(final boolean upper, @Nullable final byte[] bytes) {
        return getString(upper ? Constants.DIGITS_BYTE_UPPER : Constants.DIGITS_BYTE_LOWER, bytes);
    }

    /*normal to hex*/
    @Nullable
    public static String getString(@NonNull final byte[] digits, @Nullable final byte[] bytes) {
        return StringUtil.getString(getBytes(digits, bytes));
    }


    /*normal to hex*/
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

    /*hex to normal*/
    @Nullable
    public static byte[] getBytes(@Nullable final String hex) {
        return getBytes(Constants.DIGITS_BYTE_LOWER, StringUtil.getBytes(hex));
    }

    /*hex to normal*/
    @Nullable
    public static byte[] getBytes(final boolean upper, @Nullable final String hex) {
        return getBytes(upper ? Constants.DIGITS_BYTE_UPPER : Constants.DIGITS_BYTE_LOWER, StringUtil.getBytes(hex));
    }


}
