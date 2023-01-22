package com.colin.library.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.helper.UtilHelper;

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

    /*  /system */
    public static File getRootSystem() {
        return Environment.getRootDirectory();
    }

    /*  /storage*/
    public static File getRootStorage() {
        return Environment.getStorageDirectory();
    }


    /*  /data */
    public static File getRootData() {
        return Environment.getDataDirectory();
    }

    /*  /data/cache*/
    public static File getDownloadCache() {
        return Environment.getDownloadCacheDirectory();
    }


    /*/data/user/0/package/code_cache*/
    public static File getUserData() {
        return getUserData(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    /*/data/user/0/package*/
    public static File getUserData(@NonNull Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? context.getDataDir() : new File(context.getApplicationInfo().dataDir);
    }

    public static File getUserCache() {
        return getUserCache(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    /*/data/user/0/package/cache*/
    public static File getUserCache(@NonNull Context context) {
        return context.getCacheDir();
    }


    public static File getUserCodeCache() {
        return getUserCodeCache(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    /*/data/user/0/package/code_cache*/
    public static File getUserCodeCache(@NonNull Context context) {
        return context.getCodeCacheDir();
    }

    public static File getDatabase() {
        return getDatabase(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    /*/data/user/0/package/databases*/
    public static File getDatabase(@NonNull Context context) {
        return new File(context.getApplicationInfo().dataDir + File.separator + "databases");
    }

    public static File getSp() {
        return getSp(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    /*/data/user/0/package/shared_prefs*/
    public static File getSp(@NonNull Context context) {
        return new File(context.getApplicationInfo().dataDir + File.separator + "shared_prefs");
    }

    /*/data/user/0/package/files*/
    public static File getUserFiles() {
        return getUserFiles(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    /*/data/user/0/package/files*/
    public static File getUserFiles(@NonNull Context context) {
        return context.getFilesDir();
    }

    /*  storage/emulated/0*/
    public static File getExternalStorage() {
        return Environment.getExternalStoragePublicDirectory(null);
    }

    /*  storage/emulated/0/type */
    public static File getExternalFile(@NonNull String type) {
        return Environment.getExternalStoragePublicDirectory(type);
    }

    /**
     * /storage/emulated/0/Android/data/package/files    type == null
     * /storage/emulated/0/Android/data/package/files/type
     *
     * @param context
     * @param type
     * @return
     */
    public static File getExternalFile(@NonNull Context context, @Nullable String type) {
        return context.getExternalFilesDir(type);
    }

    /*/storage/emulated/0/Android/data/package/cache*/
    public static File getExternalCache() {
        return getExternalCache(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    /*/storage/emulated/0/Android/data/package/cache*/
    public static File getExternalCache(@NonNull Context context) {
        return context.getExternalCacheDir();
    }

    @NonNull
    public static String getInternalAppPath() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return UtilHelper.getInstance().getUtilConfig().getApplication().getApplicationInfo().dataDir;
        }
        return UtilHelper.getInstance().getUtilConfig().getApplication().getDataDir().getAbsolutePath();
    }

    @NonNull
    public static String getInternalAppPath(@NonNull Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? context.getDataDir().getAbsolutePath() : context.getApplicationInfo().dataDir;
    }

    /*清除缓存 操作 /data/data/package/cache */
    @NonNull
    public static File getInternalCache() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getCacheDir();
    }

    /*/data/data/package/shared_prefs*/
    public static String getSpPath() {
        return getInternalAppPath() + File.separator + "shared_prefs";
    }

    /*/data/data/package/code_cache*/
    public static String getCodeCachePath() {
        return getInternalAppPath() + File.separator + "code_cache";
    }

    @Nullable
    public static String getPath(@Nullable final File file) {
        return file == null ? null : file.getPath();
    }

    @Nullable
    public static String getAbsolutePath(@Nullable final File file) {
        return file == null ? null : file.getAbsolutePath();
    }
}
