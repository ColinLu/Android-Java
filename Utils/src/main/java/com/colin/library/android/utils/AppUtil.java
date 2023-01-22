package com.colin.library.android.utils;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.UserManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.helper.UtilHelper;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 22:10
 * <p>
 * 描述： App 相关工具类
 */
public final class AppUtil {
    private AppUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /**
     * 获取系统版本号
     *
     * @return -1 失败
     */
    public static int getVersionCode() {
        final PackageInfo packageInfo = getPackageInfo();
        return null == packageInfo ? Constants.INVALID : packageInfo.versionCode;
    }


    /**
     * 获取App版本
     *
     * @return 空值 失败
     */
    @NonNull
    public static String getVersionName() {
        final PackageInfo packageInfo = getPackageInfo();
        final String versionName = null == packageInfo ? null : packageInfo.versionName;
        return StringUtil.isEmpty(versionName) ? "" : versionName;
    }

    @Nullable
    public static Drawable getIcon() {
        return getIcon(UtilHelper.getInstance().getUtilConfig().getApplication().getPackageName());
    }

    /*App Icon*/
    @Nullable
    public static Drawable getIcon(@Nullable String packageName) {
        if (TextUtils.isEmpty(packageName)) return null;
        final Context context = UtilHelper.getInstance().getUtilConfig().getApplication();
        final PackageInfo info = getPackageInfo(context, packageName, 0);
        return info == null ? null : info.applicationInfo.loadIcon(context.getPackageManager());
    }

    @Nullable
    public static String getAppName() {
        return getAppName(UtilHelper.getInstance().getUtilConfig().getApplication().getPackageName());
    }

    /*App Name*/
    @Nullable
    public static String getAppName(@Nullable String packageName) {
        if (TextUtils.isEmpty(packageName)) return null;
        final Context context = UtilHelper.getInstance().getUtilConfig().getApplication();
        final PackageInfo info = getPackageInfo(context, packageName, 0);
        return info == null ? null : info.applicationInfo.loadLabel(context.getPackageManager()).toString();
    }

    @Nullable
    public static String getAppPath() {
        return getAppPath(UtilHelper.getInstance().getUtilConfig().getApplication().getPackageName());
    }

    /*App 安装路径*/
    @Nullable
    public static String getAppPath(@Nullable String packageName) {
        if (TextUtils.isEmpty(packageName)) return null;
        final Context context = UtilHelper.getInstance().getUtilConfig().getApplication();
        final PackageInfo info = getPackageInfo(context, packageName, 0);
        return info == null ? null : info.applicationInfo.sourceDir;
    }

    public static boolean isDebug() {
        return isDebug(UtilHelper.getInstance().getUtilConfig().getApplication().getPackageName());
    }

    public static boolean isDebug(@Nullable final String packageName) {
        if (TextUtils.isEmpty(packageName)) return false;
        final Context context = UtilHelper.getInstance().getUtilConfig().getApplication();
        final PackageInfo info = getPackageInfo(context, packageName, 0);
        final ApplicationInfo app = info == null ? null : info.applicationInfo;
        return app != null && (app.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    /**
     * 获取AndroidManifest.xml文件的信息
     *
     * @return
     */
    @Nullable
    public static PackageInfo getPackageInfo() {
        return getPackageInfo(UtilHelper.getInstance().getUtilConfig().getApplication(), 0);
    }

    /**
     * 获取AndroidManifest.xml文件的信息
     *
     * @return
     */
    @Nullable
    public static PackageInfo getPackageInfo(@Nullable Context context, int flags) {
        return getPackageInfo(context, null == context ? null : context.getPackageName(), flags);
    }

    @Nullable
    public static PackageInfo getPackageInfo(@Nullable String packageName, int flags) {
        return getPackageInfo(UtilHelper.getInstance().getUtilConfig().getApplication(), packageName, flags);
    }

    /**
     * 获取指定app AndroidManifest.xml文件的信息
     *
     * @param context     上下文
     * @param packageName 包名
     * @param flags       筛选匹配标记
     * @return 指定app
     */
    @Nullable
    public static PackageInfo getPackageInfo(@Nullable Context context, @Nullable String packageName, @IntRange(from = 0) int flags) {
        final PackageManager packageManager = null == context ? null : context.getPackageManager();
        if (null == packageManager || StringUtil.isEmpty(packageName)) return null;
        try {
            return packageManager.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.log(e);
        }
        return null;
    }

    public static ContentResolver getContentResolver() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getContentResolver();
    }

    public static ActivityManager getActivityManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), ActivityManager.class);
    }

    public static WindowManager getWindowManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), WindowManager.class);
    }

    public static LocationManager getLocationManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), LocationManager.class);
    }

    public static NotificationManager getNotificationManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), NotificationManager.class);
    }

    public static ConnectivityManager getConnectivityManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), ConnectivityManager.class);
    }

    public static TelephonyManager getTelephonyManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), TelephonyManager.class);
    }

    public static InputMethodManager getInputMethodManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), InputMethodManager.class);
    }

    public static DownloadManager getDownloadManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), DownloadManager.class);
    }

    public static ClipboardManager getClipboardManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), ClipboardManager.class);
    }

    public static UserManager getUserManager() {
        return ContextCompat.getSystemService(UtilHelper.getInstance().getUtilConfig().getApplication(), UserManager.class);
    }

    public static <T> T getService(@NonNull Context context, @NonNull Class<T> clazz) {
        return ContextCompat.getSystemService(context, clazz);
    }
}
