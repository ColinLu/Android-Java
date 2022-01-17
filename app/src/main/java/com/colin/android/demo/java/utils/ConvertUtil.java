package com.colin.android.demo.java.utils;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 17:37
 * <p>
 * 描述： 转化  10-16
 */
public final class ConvertUtil {
    private ConvertUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /**
     * 长度 必须为2
     *
     * @param b byte
     * @return char
     */
    public static char getChar(byte[] b) {
        return (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
    }


}
