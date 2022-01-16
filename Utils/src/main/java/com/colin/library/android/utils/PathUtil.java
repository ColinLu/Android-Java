package com.colin.library.android.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.utils.data.UtilHelper;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 22:42
 * <p>
 * 描述： Android 路径辅助类
 */
public final class PathUtil {
    private PathUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*判断是否存在外部存储卡*/
    public static boolean storageState() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /*外部存储是否有写权限*/
    public static boolean canWrite() {
        return storageState() && Environment.getExternalStorageDirectory().canWrite();
    }

    /*/data/data/package*/
    @NonNull
    public static File getAppData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return new File(UtilHelper.getInstance().getContext().getApplicationInfo().dataDir);
        }
        return UtilHelper.getInstance().getContext().getDataDir();
    }

    /*/data/data/package*/
    @NonNull
    public static String getAppDataPath() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return UtilHelper.getInstance().getContext().getApplicationInfo().dataDir;
        }
        return UtilHelper.getInstance().getContext().getDataDir().getAbsolutePath();
    }

    /*/data/data/package/shared_prefs*/
    public static String getSpPath() {
        return getAppDataPath() + Constants.FILE_SEP + "shared_prefs";
    }

    /*/data/data/package/code_cache*/
    public static String getCodeCache() {
        return getAppDataPath() + Constants.FILE_SEP + "code_cache";
    }

    /*清除缓存 操作 /data/data/package/cache */
    @NonNull
    public static File getInternalCache() {
        return UtilHelper.getInstance().getContext().getCacheDir();
    }


}
