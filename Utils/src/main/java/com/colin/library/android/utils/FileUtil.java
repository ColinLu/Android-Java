package com.colin.library.android.utils;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Locale;

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

    public static boolean createOrExistsDir(@Nullable final File dir) {
        return dir != null && (dir.exists() ? dir.isDirectory() : dir.mkdirs());
    }

    /*判断是否文件存在 或者 是否文件创建成功*/
    public static boolean createOrExistsFile(@Nullable final File file) {
        //文件不存在
        if (file == null) return false;
        //文件存在是否是文件
        if (file.exists()) return file.isFile();
        //判断是否存在文件夹 或者是否创建文件夹是否成功
        if (!createOrExistsDir(file.getParentFile())) return false;
        //创建新文件
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建文件夹
     *
     * @param folder   目录（当前文件的文件夹）
     * @param fileName 文件名
     * @return 不为空的文件
     */
    @NonNull
    public static File createFile(@NonNull final File folder, @NonNull final String fileName, boolean deleteOld) throws Exception {
        final boolean existsDir = createOrExistsDir(folder);
        if (!existsDir) throw new IOException("create dir fail");
        final File file = new File(folder, fileName);
        boolean flag = true;
        if (file.exists() && deleteOld) flag = file.delete();
        if (!flag) throw new IOException("file is exists delete fail");
        return file;
    }

    @Nullable
    public static String getMimeTypeByUrl(@Nullable final String url) {
        return getMimeType(HttpUtil.getFileName(url));
    }

    /*文件名 xxx.mp3*/
    @Nullable
    public static String getMimeType(@Nullable final String fileName) {
        if (TextUtils.isEmpty(fileName)) return null;
        return URLConnection.getFileNameMap().getContentTypeFor(fileName.toLowerCase(Locale.US));
    }

    /*获取后缀 mp3 png mp4 不含 . */
    @Nullable
    public static String getExtension(String path) {
        return TextUtils.isEmpty(path) ? null : MimeTypeMap.getFileExtensionFromUrl(path.toLowerCase());
    }

    /*获取后缀名(小写)  .png .mp4*/
    @Nullable
    public static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) return null;
        final String fileName = file.getName();
        if (TextUtils.isEmpty(fileName) || fileName.endsWith(".")) return null;
        int index = fileName.lastIndexOf(".");
        return index == -1 ? null : fileName.substring(index + 1).toLowerCase(Locale.US);
    }

    /*文件大小 或者 文件夹总大小*/
    public static long getFileSize(@Nullable final File file) {
        if (null == file || !file.exists()) return 0L;
        //单个文件
        if (file.isFile()) return file.length();
        final File[] files = file.listFiles();
        if (files == null || files.length == 0) return 0L;
        long len = 0;
        for (File child : files) {
            if (child.isFile()) len += child.length();
            else len += getFileSize(child);
        }
        return len;
    }

    /*格式化文件大小*/
    @NonNull
    public static String formatSize(final long size) {
        final DecimalFormat format = new DecimalFormat("#.00");
        String result;
        if (size == 0) return "0B";
        if (size < 1024) result = format.format(size * 1D) + "B";
        else if (size < 1048576) result = format.format(size * 1D / 1024) + "KB";
        else if (size < 1073741824) result = format.format(size * 1D / 1048576) + "MB";
        else result = format.format(size * 1D / 1073741824) + "GB";
        return result;
    }
}
