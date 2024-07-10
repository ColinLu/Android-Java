package com.colin.library.android.utils;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 22:26
 * <p>
 * 描述： Util for number
 */
public final class NumberUtil {
    private NumberUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static int constrain(int amount, int low, int high) {
        return amount < low ? low : Math.min(amount, high);
    }

    public static float constrain(float amount, float low, float high) {
        return amount < low ? low : Math.min(amount, high);
    }

}
