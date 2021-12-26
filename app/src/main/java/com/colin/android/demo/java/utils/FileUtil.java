package com.colin.android.demo.java.utils;

import androidx.annotation.Nullable;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 21:34
 * <p>
 * 描述： File Util
 */
public final class FileUtil {
    private FileUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static boolean isFile(@Nullable final File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean isDir(@Nullable final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

}
