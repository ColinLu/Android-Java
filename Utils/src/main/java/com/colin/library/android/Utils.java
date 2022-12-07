package com.colin.library.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

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
    public static <T> T notNull(@Nullable T reference, @NonNull Object errorMessage) {
        if (reference == null) throw new NullPointerException(String.valueOf(errorMessage));
        return reference;
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
