package com.colin.android.demo.java.utils.encrypt;

import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 17:49
 * <p>
 * 描述： TODO
 */
public final class MD5Util {
    private MD5Util() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /**
     * 加密内容 (32 位小写 MD5)
     *
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    @Nullable
    public static byte[] getByte(@Nullable final byte[] data) {
        if (null == data || data.length == 0) return null;
        return EncryptUtil.digest(data, Constants.ALGORITHM_MD_5);
    }

}
