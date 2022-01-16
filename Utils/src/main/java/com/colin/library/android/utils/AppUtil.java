package com.colin.library.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.utils.data.UtilHelper;

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

    /**
     * 获取AndroidManifest.xml文件的信息
     *
     * @return
     */
    @Nullable
    public static PackageInfo getPackageInfo() {
        return getPackageInfo(UtilHelper.getInstance().getContext(), 0);
    }

    /**
     * 获取AndroidManifest.xml文件的信息
     *
     * @return
     */
    @Nullable
    public static PackageInfo getPackageInfo(@Nullable Context context, int flags) {
        return getPackageInfo(context, null == context ? null : context.getPackageName(), 0);
    }

    @Nullable
    public static PackageInfo getPackageInfo(@Nullable String packageName, int flags) {
        return getPackageInfo(UtilHelper.getInstance().getContext(), packageName, flags);
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
}
