package com.colin.library.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2022-11-28 19:10
 * <p>
 * 描述： 辅助类
 */
public final class Utils {

    @NonNull
    public static <T> T notNull(@Nullable T reference) {
        if (reference == null) throw new NullPointerException();
        return reference;
    }

    @NonNull
    public static <T> T notNull(@Nullable T reference, @NonNull String msg) {
        if (reference == null) throw new NullPointerException(msg);
        return reference;
    }

    @NonNull
    public static <T> T notNull(@Nullable T reference, @NonNull Throwable t) {
        if (reference == null) throw new NullPointerException(t.toString());
        return reference;
    }

}
