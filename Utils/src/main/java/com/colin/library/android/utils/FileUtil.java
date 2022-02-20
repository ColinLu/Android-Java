package com.colin.library.android.utils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.utils.data.UtilHelper;
import com.colin.library.android.utils.encrypt.MD5Util;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.UUID;

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

    /*判断文件/文件夹(目录) 是否存在*/
    public static boolean isExists(@Nullable final String filePath) {
        return isExists(getFile(filePath));
    }

    /*判断文件/文件夹(目录) 是否存在*/
    public static boolean isExists(@Nullable final File file) {
        return file != null && file.exists();
    }

    /*判断文件是否存在*/
    public static boolean isFile(@Nullable final String filePath) {
        return isFile(getFile(filePath));
    }

    /*判断文件是否存在*/
    public static boolean isFile(@Nullable final File file) {
        return isExists(file) && file.isFile();
    }

    /*判断文件夹（目录）是否存在*/
    public static boolean isFolder(@Nullable final String folderPath) {
        return isFolder(getFile(folderPath));
    }

    /*判断文件夹（目录）是否存在*/
    public static boolean isFolder(@Nullable final File folder) {
        return isExists(folder) && folder.isDirectory();
    }

    /*文件或文件夹（目录）路径 -->> 文件或文件夹 */
    @Nullable
    public static File getFile(@Nullable final String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createImageFile() {
        return createFile(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_PICTURES, createFileName(Constants.DEFAULT_IMAGE_SUFFIX));
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createImageFile(@NonNull String suffix) {
        return createFile(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_PICTURES, createFileName(suffix));
    }


    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createVideoFile() {
        return createFile(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_MOVIES, createFileName(Constants.DEFAULT_VIDEO_SUFFIX));
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createVideoFile(@NonNull String suffix) {
        return createFile(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_MOVIES, createFileName(suffix));
    }


    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createAudioFile() {
        return createFile(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_MUSIC, createFileName(Constants.DEFAULT_MUSIC_SUFFIX));
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createAudioFile(@NonNull String suffix) {
        return createFile(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_MUSIC, createFileName(suffix));
    }


    /**
     * 下载/保存 文件生成文件  存在需要删除
     *
     * @param context  上下文
     * @param type     文件路径
     * @param fileName 文件名
     * @return
     */
    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createFile(@NonNull final Context context, @Nullable final String type, @NonNull final String fileName) {
        return createFile(context, type, fileName, true);
    }


    /**
     * 下载/保存 文件生成文件  存在需要删除
     *
     * @param context   上下文
     * @param type      文件路径
     * @param fileName  文件名
     * @param deleteOld 是否需要删除旧文件
     * @return
     */
    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createFile(@NonNull final Context context, @Nullable final String type,
                                  @NonNull final String fileName, final boolean deleteOld) {
        return createFile(PathUtil.getExternalFile(context, type), fileName, deleteOld);
    }

    /**
     * 下载/保存 文件生成文件  存在需要删除
     *
     * @param folder    文件夹
     * @param fileName  文件名
     * @param deleteOld 是否需要删除旧文件
     * @return
     */
    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createFile(@NonNull File folder, @NonNull final String fileName, final boolean deleteOld) {
        final boolean existsFolder = createOrExistsFolder(folder);
        if (!existsFolder) throw new NullPointerException("create folder fail");
        final File file = new File(folder, fileName);
        boolean flag = true;
        if (file.exists() && deleteOld) flag = file.delete();
        if (!flag) throw new NullPointerException("file is exists delete fail");
        return file;
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createImageFolder() {
        return createFolder(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_PICTURES);
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createVideoFolder() {
        return createFolder(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_MOVIES);
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createAudioFolder() {
        return createFolder(UtilHelper.getInstance().getContext(), Environment.DIRECTORY_MUSIC);
    }

    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    private static File createFolder(@NonNull final Context context, @NonNull String type) {
        final File folder = PathUtil.getExternalFile(context, type);
        final boolean existsFolder = createOrExistsFolder(folder);
        if (!existsFolder) throw new NullPointerException("create folder fail");
        return folder;
    }

    /*解析URL地址 获取文件名字 带后缀 eg:  xxx.txt*/
    @Nullable
    public static String getFileName(@Nullable final String url) {
        if (null == url || url.length() == 0) return null;
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

    public static String createFileName(@NonNull String suffix) {
        return createFileName(System.currentTimeMillis(), suffix);
    }

    public static String createFileName(long time, @NonNull String suffix) {
        final String pre = MD5Util.getString(UUID.randomUUID().toString());
        final String date = TimeUtil.getTimeString("yyyyMMdd_HHmmssSSS", time);
        return pre + "_" + date + suffix;
    }

    /**
     * 创建文件夹  不存在 创建，存在不创建
     * <p>
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param folder 文件夹
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    @RequiresPermission(WRITE_EXTERNAL_STORAGE)
    public static boolean createOrExistsFolder(@Nullable final File folder) {
        return folder != null && (folder.exists() ? folder.isDirectory() : folder.mkdirs());
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean createOrExistsFolder(@Nullable final String folderPath) {
        if (TextUtils.isEmpty(folderPath)) return false;
        return createOrExistsFolder(new File(folderPath));
    }


    /**
     * 判断是否文件存在 或者 是否文件创建成功
     *
     * @param file
     * @return
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean createOrExistsFile(@Nullable final File file) {
        //文件不存在
        if (file == null) return false;
        //文件存在是否是文件
        if (file.exists()) return file.isFile();
        //判断是否存在文件夹 或者是否创建文件夹是否成功
        if (!createOrExistsFolder(file.getParentFile())) return false;
        //创建新文件
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file      文件
     * @param deleteOld 判断文件是否存在，存在则在创建之前删除
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    @RequiresPermission(WRITE_EXTERNAL_STORAGE)
    public static boolean createFile(final File file, boolean deleteOld) {
        //文件不存在
        if (file == null) return false;
        //删除旧文件， 文件存在并且删除失败返回 false
        if (deleteOld && file.exists() && !file.delete()) return false;
        //不需要删除旧文件，判断是否存在  并且是文件
        if (!deleteOld && file.exists()) return file.isFile();
        // 创建目录失败返回 false
        if (!createOrExistsFile(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static String getMimeTypeByUrl(@Nullable final String url) {
        return getMimeType(getFileName(url));
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

    /**
     * 获取文件大小 或者 文件夹总体积
     *
     * @param file 文件
     * @return 大小
     */
    public static long getFileSize(@Nullable final File file) {
        if (null == file || !file.exists()) return 0L;
        //单个文件
        if (file.isFile()) return file.length();
        File[] files = file.listFiles();
        if (null == files || files.length == 0) return 0;
        long len = 0;
        for (File childFile : files) {
            if (isFolder(childFile)) len += getFileSize(childFile);
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


}
