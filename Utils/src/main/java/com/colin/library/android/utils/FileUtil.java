package com.colin.library.android.utils;

import android.text.TextUtils;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.annotation.Encode;
import com.colin.library.android.utils.data.UtilHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    /*路径转文件*/
    @Nullable
    public static File getFile(@Nullable final String path) {
        return StringUtil.isSpace(path) ? null : new File(path);
    }

    /*路径转文件*/
    @Nullable
    public static File getFile(@Nullable final String parent, @Nullable String fileName) {
        return StringUtil.isSpace(parent) || StringUtil.isSpace(fileName) ? null : new File(parent, fileName);
    }

    /*路径转文件*/
    @Nullable
    public static File getFile(@Nullable final File parent, @Nullable String fileName) {
        return parent == null || StringUtil.isSpace(fileName) ? null : new File(parent, fileName);
    }

    /*判断文件是否存在*/
    public static boolean isFile(@Nullable final String path) {
        return isFile(getFile(path));
    }

    /*判断文件/文件夹(目录) 是否存在*/
    public static boolean isExists(@Nullable final String path) {
        return isExists(getFile(path));
    }

    /*判断文件/文件夹(目录) 是否存在*/
    public static boolean isExists(@Nullable final File file) {
        return file != null && file.exists();
    }

    /*判断是否文件*/
    public static boolean isFile(@Nullable final File file) {
        return isExists(file) && file.isFile();
    }

    /*判断文件夹（目录）是否存在*/
    public static boolean isDir(@Nullable final String path) {
        return isDir(getFile(path));
    }


    /*判断文件夹（目录）是否存在*/
    public static boolean isDir(@Nullable final File dir) {
        return isExists(dir) && dir.isDirectory();
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file   文件
     * @param delete 判断文件是否存在，存在则在创建之前删除
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFile(@Nullable final File file, boolean delete) {
        //文件不存在
        if (file == null) return false;
        //删除旧文件， 文件存在并且删除失败返回 false
        if (delete && file.exists() && !file.delete()) return false;
        //不需要删除旧文件，判断是否存在  并且是文件
        if (!delete && file.exists()) return file.isFile();
        // 创建目录失败返回 false
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*创建文件  不存在 创建，存在不创建*/
    public static boolean createOrExistsFile(@Nullable final String path) {
        return createOrExistsFile(getFile(path));
    }

    /*创建文件  不存在 创建，存在不创建*/
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

    /*创建文件夹  不存在 创建，存在不创建*/
    public static boolean createOrExistsDir(@Nullable final String path) {
        return createOrExistsDir(getFile(path));
    }

    /*创建文件夹  不存在 创建，存在不创建*/
    public static boolean createOrExistsDir(@Nullable final File dir) {
        return dir != null && (dir.exists() ? dir.isDirectory() : dir.mkdirs());
    }

    /**
     * 获取文件后缀名 ( 无 "." 单独后缀 )
     *
     * @param file 文件
     * @return 文件后缀名 ( 无 "." 单独后缀 )
     */
    public static String getFileSuffix(@Nullable final File file) {
        return getFileSuffix(file == null ? null : file.getAbsoluteFile());
    }

    /**
     * 获取文件后缀名 ( 无 "." 单独后缀 )
     *
     * @param path 文件路径或文件名
     * @return 文件后缀名 ( 无 "." 单独后缀 )
     */
    public static String getFileSuffix(final String path) {
        // 获取最后的索引
        int index;
        // 判断是否存在
        if (path != null && (index = path.lastIndexOf('.')) != -1) {
            final String result = path.substring(index);
            if (result.startsWith(".")) return result.substring(1);
            return result;
        }
        return null;
    }

    /**
     * 获取路径中的文件扩展名
     *
     * @param file 文件
     * @return 文件扩展名
     */
    public static String getFileExtension(@Nullable final File file) {
        return file == null ? null : getFileExtension(file.getPath());
    }

    /**
     * 获取路径中的文件扩展名
     *
     * @param path 文件路径
     * @return 文件扩展名
     */
    @Nullable
    public static String getFileExtension(@Nullable final String path) {
        if (StringUtil.isSpace(path)) return null;
        final int lastPoi = path.lastIndexOf('.');
        final int lastSep = path.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return null;
        return path.substring(lastPoi + 1);
    }

    /**
     * 获取路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    @Nullable
    public static String getFileName(final File file) {
        return file == null ? null : getFileName(file.getPath());
    }

    /**
     * 获取路径中的文件名
     *
     * @param path 文件路径
     * @return 文件名
     */
    @Nullable
    public static String getFileName(final String path) {
        if (StringUtil.isSpace(path)) return null;
        int lastSep = path.lastIndexOf(File.separator);
        return lastSep == -1 ? path : path.substring(lastSep + 1);
    }

    /*解析URL地址 获取文件名字 带后缀 eg:  xxx.txt*/
    @Nullable
    public static String getFileNameByUrl(@Nullable final String url) {
        if (TextUtils.isEmpty(url)) return null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    return string.substring(0, endIndex);
                }
            }
        }
        if (strings.length > 0) return strings[strings.length - 1];
        return null;
    }

    @NonNull
    public static String getDefaultName(@NonNull String Suffix) {
        return TimeUtil.getTimeString() + Suffix;
    }

    /**
     * 获取路径中的最长目录地址
     *
     * @param file 文件
     * @return 最长目录地址
     */
    public static String getDirName(@Nullable final File file) {
        return file == null ? null : getDirName(file.getPath());
    }

    /**
     * 获取全路径中的最长目录地址
     *
     * @param path 文件路径
     * @return 最长目录地址
     */
    public static String getDirName(@Nullable final String path) {
        if (StringUtil.isSpace(path)) return null;
        int lastSep = path.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : path.substring(0, lastSep + 1);
    }

    /**
     * 获取文件编码格式
     *
     * @param path 文件路径
     * @return 文件编码格式
     */
    public static String getCharset(final String path) {
        return getCharset(getFile(path));
    }

    /**
     * 获取文件编码格式
     *
     * @param file 文件
     * @return 文件编码格式
     */
    public static String getCharset(final File file) {

        if (!isExists(file)) return null;
        int pos = 0;
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            pos = (is.read() << 8) + is.read();
        } catch (IOException e) {
            LogUtil.log(e);
        } finally {
            IOUtil.close(is);
        }
        switch (pos) {
            case 0xefbb:
                return Encode.UTF_8;
            case 0xfffe:
                return Encode.UNICODE;
            case 0xfeff:
                return Encode.UTF_16BE;
            default:
                return Encode.GBK;
        }
    }

    /**
     * Rename the file.
     *
     * @param file The file.
     * @param name The new name of file or dir.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean rename(@Nullable final File file, @Nullable final String name) {
        // file is null or file doesn't exist then return false
        if (!isExists(file)) return false;
        // the new name is space then return false
        if (StringUtil.isSpace(name)) return false;
        // the new name equals old name then return true
        if (name.equals(file.getName())) return true;
        final File newFile = new File(file.getParent() + File.separator + name);
        // the new name of file exists then return false
        return !newFile.exists() && file.renameTo(newFile);
    }

    /**
     * Delete the file.
     *
     * @param path The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    private static boolean deleteFile(@Nullable final String path) {
        return deleteFile(getFile(path));
    }

    /**
     * Delete the file.
     *
     * @param file The file.
     * @return {@code true}: success<br>{@code false}: fail
     */
    private static boolean deleteFile(@Nullable final File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }


    @Nullable
    public static String getMimeTypeByUrl(@Nullable final String url) {
        return getMimeType(getFileNameByUrl(url));
    }

    /*文件名 xxx.mp3*/
    @Nullable
    public static String getMimeType(@Nullable final String fileName) {
        if (StringUtil.isSpace(fileName)) return null;
        return URLConnection.getFileNameMap().getContentTypeFor(fileName.toLowerCase(Locale.US));
    }


    /*获取后缀 mp3 png mp4 不含 . */
    @Nullable
    public static String getExtension(String path) {
        return StringUtil.isSpace(path) ? null : MimeTypeMap.getFileExtensionFromUrl(path.toLowerCase());
    }

    /*获取后缀名(小写)  .png .mp4*/
    @Nullable
    public static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) return null;
        final String fileName = file.getName();
        if (StringUtil.isSpace(fileName) || fileName.endsWith(".")) return null;
        int index = fileName.lastIndexOf(".");
        return index == -1 ? null : fileName.substring(index + 1).toLowerCase(Locale.US);
    }

    /**
     * 获取文件大小 或者 文件夹总体积
     *
     * @param file 文件
     * @return 大小
     */
    public static long getFileSize(@Nullable final File file) {
        if (!isExists(file)) return 0L;
        //单个文件
        if (file.isFile()) return file.length();
        File[] files = file.listFiles();
        if (null == files || files.length == 0) return 0;
        long len = 0;
        for (File childFile : files) {
            if (isDir(childFile)) len += getFileSize(childFile);
            else len += childFile.length();
        }
        return len;
    }

    public static String formatFileSize(@Nullable final File file) {
        return formatFileSize(getFileSize(file));
    }

    /**
     * 格式化文件大小
     *
     * @param fileSizeByte byte
     * @return
     */
    public static String formatFileSize(long fileSizeByte) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String fileSizeString;
        if (fileSizeByte == 0) return "0B";
        if (fileSizeByte < 1024) {
            fileSizeString = decimalFormat.format((double) fileSizeByte) + "B";
        } else if (fileSizeByte < 1048576) {
            fileSizeString = decimalFormat.format((double) fileSizeByte / 1024) + "KB";
        } else if (fileSizeByte < 1073741824) {
            fileSizeString = decimalFormat.format((double) fileSizeByte / 1048576) + "MB";
        } else {
            fileSizeString = decimalFormat.format((double) fileSizeByte / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    public static String formatSize(long sizeBytes) {
        return Formatter.formatFileSize(UtilHelper.getInstance().getContext(), sizeBytes);
    }

    /**
     * 字节数转合适内存大小
     * <p>保留 3 位小数</p>
     *
     * @param fileSizeByte 字节数
     * @return 合适内存大小
     */
    public static String byte2FitMemorySize(final long fileSizeByte) {
        if (fileSizeByte < 0) {
            return "shouldn't be less than zero!";
        } else if (fileSizeByte < 1024) {
            return String.format(Locale.CHINESE, "%.3fB", (double) fileSizeByte);
        } else if (fileSizeByte < 1048576) {
            return String.format(Locale.CHINESE, "%.3fKB", (double) fileSizeByte / 1024);
        } else if (fileSizeByte < 1073741824) {
            return String.format(Locale.CHINESE, "%.3fMB", (double) fileSizeByte / 1048576);
        } else {
            return String.format(Locale.CHINESE, "%.3fGB", (double) fileSizeByte / 1073741824);
        }
    }


    public interface OnFileListener {
        boolean action(File src, File dest);
    }
}
