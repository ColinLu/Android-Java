package com.colin.android.demo.java.utils;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 20:25
 * <p>
 * 描述： Bitmap Util
 */
public final class BitmapUtil {
    public static boolean isEmpty(@Nullable Bitmap bitmap) {
        return bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0;
    }
}
