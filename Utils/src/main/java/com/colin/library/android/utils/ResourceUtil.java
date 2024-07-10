package com.colin.library.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.colin.library.android.helper.UtilHelper;
import com.colin.library.android.utils.data.Constants;


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

    @NonNull
    public static Resources getResources() {
        return getResources(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    @NonNull
    public static Resources getResources(@NonNull Context context) {
        return context.getResources();
    }

    @NonNull
    public static DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }

    @NonNull
    public static DisplayMetrics getDisplayMetrics(@NonNull Context context) {
        return getResources(context).getDisplayMetrics();
    }

    public static float getDensity() {
        return getDisplayMetrics().density;
    }

    public static float getDensity(@NonNull Context context) {
        return getDisplayMetrics(context).density;
    }


    public static float getScaled() {
        return getDisplayMetrics().scaledDensity;
    }

    public static float getScaled(@NonNull Context context) {
        return getDisplayMetrics(context).scaledDensity;
    }

    @Nullable
    public static Uri getUri(@DrawableRes int id) {
        return getUri(getResources(), id);
    }

    @Nullable
    public static Uri getUri(@Nullable Resources resources, @DrawableRes int id) {
        if (null == resources || id == Resources.ID_NULL) return null;
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(id) + "/" + resources.getResourceTypeName(
                id) + "/" + resources.getResourceEntryName(id));
    }

    /**
     * 状态栏高度
     *
     * @return
     */
    @Px
    public static int getStatusBarHeight() {
        @DimenRes int dimenResId = getDimenResId(Constants.HEIGHT_STATUS_BAR);
        return dimenResId == Resources.ID_NULL ? 0 : ResourceUtil.getResources().getDimensionPixelSize(dimenResId);
    }

    /**
     * 底部导航栏高度
     *
     * @return
     */
    @Px
    public static int getNavBarHeight() {
        @DimenRes int dimenResId = ResourceUtil.getDimenResId(Constants.HEIGHT_NAVIGATION_BAR);
        return dimenResId == Resources.ID_NULL ? 0 : ResourceUtil.getResources().getDimensionPixelSize(dimenResId);
    }


    /**
     * 获取长度资源文件ID
     *
     * @param name
     * @return
     */
    @DimenRes
    public static int getDimenResId(@NonNull String name) {
        return getIdentifier(name, "dimen", "android");
    }

    /**
     * 获取资源文件ID
     *
     * @param name       eg:status_bar_height
     * @param defType    eg:dimen/drawable
     * @param defPackage eg:android
     * @return eg:ResId
     */
    public static int getIdentifier(@NonNull String name, @NonNull String defType, @NonNull String defPackage) {
        return getResources().getIdentifier(name, defType, defPackage);
    }


    @Nullable
    public static Drawable getDrawable(@DrawableRes final int res) {
        return getDrawable(UtilHelper.getInstance().getUtilConfig().getApplication(), res);
    }

    @Nullable
    public static Drawable getDrawable(@Nullable Context context, @DrawableRes final int res) {
        if (context == null || res == Resources.ID_NULL) return null;
        return ContextCompat.getDrawable(context, res);
    }

    @Nullable
    public static Drawable getVectorDrawable(@NonNull final Context context, @DrawableRes final int res) {
        return AppCompatResources.getDrawable(context, res);
    }

    @NonNull
    public static String getString(@StringRes final int res) {
        return getResources().getString(res);
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes final int res) {
        return getResources(context).getString(res);
    }

    @NonNull
    public static String getString(@StringRes final int res, Object... args) {
        return getString(UtilHelper.getInstance().getUtilConfig().getApplication(), res, args);
    }

    @NonNull
    public static String getString(@NonNull Context context, @StringRes final int res, Object... args) {
        return getResources(context).getString(res, args);
    }

    @ColorInt
    public static int getColor(@ColorRes final int res) {
        return getColor(UtilHelper.getInstance().getUtilConfig().getApplication(), res);
    }

    @ColorInt
    public static int getColor(@Nullable Context context, @ColorRes final int res) {
        if (context == null || res == Resources.ID_NULL) return Color.TRANSPARENT;
        return ContextCompat.getColor(context, res);
    }


    public static int setColorAlpha(@ColorInt int color, @FloatRange(from = 0, to = 1) final float alpha) {
        return setColorAlpha(color, (int) (alpha * 255.0f + 0.5f));
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

    /**
     * Value of dp to value of px.
     *
     * @param dp The value of dp.
     * @return value of px
     */
    public static int dp2px(final float dp) {
        return dp2px(UtilHelper.getInstance().getUtilConfig().getApplication(), dp);
    }

    public static int dp2px(@NonNull Context context, final float dp) {
        final float density = getDensity(context);
        return (int) (dp * density + 0.5f);
    }

    /**
     * Value of px to value of dp.
     *
     * @param px The value of px.
     * @return value of dp
     */
    public static int px2dp(final int px) {
        return px2dp(UtilHelper.getInstance().getUtilConfig().getApplication(), px);
    }

    public static int px2dp(@NonNull Context context, final int px) {
        final float density = getDensity(context);
        return (int) (px / density + 0.5f);
    }

    public static float px2dp(final float px) {
        return px2dp(UtilHelper.getInstance().getUtilConfig().getApplication(), px);
    }

    public static float px2dp(@NonNull Context context, final float px) {
        final float density = getDensity(context);
        return (px / density + 0.5f);
    }

    /**
     * Value of sp to value of px.
     *
     * @param sp The value of sp.
     * @return value of px
     */
    public static int sp2px(final int sp) {
        return sp2px(UtilHelper.getInstance().getUtilConfig().getApplication(), sp);
    }

    public static int sp2px(@NonNull Context context, final int sp) {
        final float scaled = getScaled(context);
        return (int) (sp * scaled + 0.5f);
    }

    public static float sp2px(final float sp) {
        return sp2px(UtilHelper.getInstance().getUtilConfig().getApplication(), sp);

    }

    public static float sp2px(@NonNull Context context, final float sp) {
        final float scaled = getScaled(context);
        return (sp * scaled + 0.5f);
    }

    /**
     * Value of px to value of sp.
     *
     * @param px The value of px.
     * @return value of sp
     */
    public static int px2sp(final int px) {
        return px2sp(UtilHelper.getInstance().getUtilConfig().getApplication(), px);
    }

    public static int px2sp(@NonNull Context context, final int px) {
        final float scaled = getScaled(context);
        return (int) (px / scaled + 0.5f);
    }

    public static float px2sp(final float px) {
        return px2sp(UtilHelper.getInstance().getUtilConfig().getApplication(), px);
    }

    public static float px2sp(@NonNull Context context, final float px) {
        final float scaled = getScaled(context);
        return (px / scaled + 0.5f);
    }

    /**
     * Converts an unpacked complex data value holding a dimension to its final floating
     * point value. The two parameters <var>unit</var> and <var>value</var>
     * are as in {@link TypedValue#TYPE_DIMENSION}.
     *
     * @param value The value to apply the unit to.
     * @param unit  The unit to convert from.
     * @return The complex floating point value multiplied by the appropriate
     * metrics depending on its unit.
     */
    public static float applyDimension(final float value, final int unit) {
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f / 72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f / 25.4f);
        }
        return 0;
    }

}
