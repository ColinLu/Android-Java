package com.colin.android.demo.java.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.colin.android.demo.java.utils.data.UtilHelper;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 13:47
 * <p>
 * 描述： Res Util
 * 屏幕密度 	        范围(dpi) 	标准分辨率 	    dp与px 	    图标尺寸
 * ldpi(QVGA) 	    ~ 120 	2   40 * 320 	    1dp=0.75px 	36 * 36
 * mdpi(HVGA) 	    120 ~ 160 	320 * 480 	    1dp=1px 	48 * 48
 * hdpi(WVGA) 	    160 ~ 240 	480 * 800 	    1dp=1.5px 	72 * 72
 * xhdpi(720P) 	    240 ~ 320 	720 * 1280 	    1dp=2px 	96 * 96
 * xxhdpi(1080p) 	320 ~ 480 	1080 * 1920 	1dp=3px 	144 * 144
 * xxxhdpi(2K) 	    480 ~ 640 	1440 × 2560 	1dp=4px 	192 * 192
 */
public final class ResourceUtil {
    private ResourceUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static int setColorAlpha(@ColorInt int color, @FloatRange(from = 0, to = 1) final float alpha) {
        return (color & 0X00FFFFFF) | ((int) (alpha * 255.0f + 0.5f) << 24);
    }

    /**
     * Set the alpha component of {@code color} to be {@code alpha}.
     *
     * @param color The color.
     * @param alpha [0..255].
     * @return the {@code color} with {@code alpha} component
     */
    @ColorInt
    public static int setColorAlpha(@ColorInt final int color, @IntRange(from = 0x0, to = 0xFF) final int alpha) {
        return (color & 0X00FFFFFF) | (alpha << 24);
    }

    public static int getRandomColor(final boolean supportAlpha) {
        final int high = supportAlpha ? (int) (Math.random() * 0x100) << 24 : 0xFF000000;
        return high | (int) (Math.random() * 0x1000000);
    }


    @NonNull
    public static Resources getResources() {
        return UtilHelper.getInstance().getContext().getResources();
    }

    @NonNull
    public static Resources getResources(@Nullable Context context) {
        return context == null ? getResources() : context.getResources();
    }

    @Nullable
    public static String getString(@StringRes final int res) {
        return getString(UtilHelper.getInstance().getContext(), res);
    }

    @Nullable
    public static String getString(@Nullable Context context, @StringRes final int res) {
        if (context == null || res == Resources.ID_NULL) return null;
        return getResources(context).getString(res);
    }

    @ColorInt
    public static int getColor(@ColorRes final int res) {
        return getColor(UtilHelper.getInstance().getContext(), res);
    }

    @ColorInt
    public static int getColor(@Nullable Context context, @ColorRes final int res) {
        if (context == null || res == Resources.ID_NULL) return Color.TRANSPARENT;
        return ContextCompat.getColor(context, res);
    }

    @Nullable
    public static Drawable getDrawable(@DrawableRes final int res) {
        return getDrawable(UtilHelper.getInstance().getContext(), res);
    }

    @Nullable
    public static Drawable getDrawable(@Nullable Context context, @DrawableRes final int res) {
        if (context == null || res == Resources.ID_NULL) return null;
        return ContextCompat.getDrawable(context, res);
    }
}
