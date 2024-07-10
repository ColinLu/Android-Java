package com.colin.library.android.utils;

import android.text.format.Formatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.Encode;
import com.colin.library.android.helper.UtilHelper;
import com.colin.library.android.utils.data.Constants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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

    private static int sBufferSize = Constants.BUFFER_FILE_SIZE;

    /**
     * 设置缓冲区尺寸
     *
     * @param bufferSize 缓冲区大小
     */
    public static void setBufferSize(final int bufferSize) {
        sBufferSize = bufferSize;
    }

    /*判断是否是文件并且存在*/
    public static boolean isFile(@Nullable final String path) {
        return isFile(getFile(path));
    }

    /*判断是否是文件并且存在*/
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

    /*判断文件/文件夹(目录) 是否存在*/
    public static boolean isExists(@Nullable final String path) {
        return isExists(getFile(path));
    }

    /*判断文件/文件夹(目录) 是否存在*/
    public static boolean isExists(@Nullable final File file) {
        return file != null && file.exists();
    }

    /*路径转文件*/
    @Nullable
    public static File getFile(@Nullable final String path) {
        return StringUtil.isSpace(path) ? null : new File(path);
    }

    /*路径转文件*/
    @Nullable
    public static File getFile(@Nullable final String parent, @Nullable String fileName) {
        return StringUtil.isSpace(parent, fileName) ? null : new File(parent, fileName);
    }

    /*路径转文件*/
    @Nullable
    public static File getFile(@Nullable final File parent, @Nullable String fileName) {
        return !isDir(parent) || StringUtil.isSpace(fileName) ? null : new File(parent, fileName);
    }

    public static boolean createFile(@Nullable final File file) {
        return createFile(file, true);
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
    public static boolean createOrExistsFile(@Nullable final File parent, @Nullable String fileName) {
        return createOrExistsFile(getFile(parent, fileName));
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
            LogUtil.log(e);
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
     * 获取路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    @Nullable
    public static String getFileName(final File file) {
        return file == null ? null : file.getName();
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


    /*文件名 xxx.mp3*/
    @Nullable
    public static String getMimeType(@Nullable final String fileName) {
        if (StringUtil.isSpace(fileName)) return null;
        return URLConnection.getFileNameMap().getContentTypeFor(fileName.toLowerCase(Locale.US));
    }


    /**
     * 获取文件后缀名 (eg: mp3)
     *
     * @param file 文件
     * @return 文件后缀名
     */
    public static String getSuffix(@Nullable final File file) {
        return getSuffix(file == null ? null : file.getAbsoluteFile());
    }

    /**
     * 获取文件后缀名 (eg: mp3)
     *
     * @param path 文件
     * @return 文件后缀名
     */
    public static String getSuffix(final String path) {
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
    public static String getExtension(@Nullable final File file) {
        return file == null ? null : getExtension(file.getPath());
    }

    /**
     * 获取路径中的文件扩展名
     *
     * @param path 文件路径
     * @return 文件扩展名
     */
    @Nullable
    public static String getExtension(@Nullable final String path) {
        if (StringUtil.isSpace(path)) return null;
        final int lastPoi = path.lastIndexOf('.');
        final int lastSep = path.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return null;
        return path.substring(lastPoi + 1);
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
        return Formatter.formatFileSize(UtilHelper.getInstance().getUtilConfig().getApplication(), sizeBytes);
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

    ///////////////////////////////////////////////////////////////////////////
    // to File
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 将输入流写入文件
     *
     * @param path 文件
     * @param is   输入流
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final String path, @Nullable final InputStream is) {
        return toFile(FileUtil.getFile(path), is, false);
    }

    /**
     * 将输入流写入文件
     *
     * @param path   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final String path, @Nullable final InputStream is, final boolean append) {
        return toFile(FileUtil.getFile(path), is, append);
    }

    /**
     * 将输入流写入文件
     *
     * @param file 文件
     * @param is   输入流
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final File file, @Nullable final InputStream is) {
        return toFile(file, is, false);
    }

    /**
     * 将输入流写入文件
     *
     * @param file   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final File file, @Nullable final InputStream is, final boolean append) {
        if (is == null || !FileUtil.createOrExistsFile(file)) return false;
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append));
            final byte[] data = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != Constants.INVALID) {
                os.write(data, 0, len);
            }
            IOUtil.flush(os);
            return true;
        } catch (IOException e) {
            LogUtil.e(e);
            return false;
        } finally {
            IOUtil.close(is, os);
        }
    }

    /**
     * 将字节数组写入文件
     *
     * @param bytes 字节
     * @param path  文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final String path, @Nullable final byte[] bytes) {
        return toFile(FileUtil.getFile(path), bytes, false);
    }

    /**
     * 将字节数组写入文件
     *
     * @param path   文件
     * @param bytes
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final String path, @Nullable final byte[] bytes, final boolean append) {
        return toFile(FileUtil.getFile(path), bytes, append);
    }

    /**
     * 将字节数组写入文件
     *
     * @param file  文件
     * @param bytes 字节数组
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final File file, @Nullable final byte[] bytes) {
        return toFile(file, bytes, false);
    }

    /**
     * 将字节数组写入文件
     *
     * @param file   文件
     * @param bytes  字节数组
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final File file, @Nullable final byte[] bytes, final boolean append) {
        if (ArrayUtil.isEmpty(bytes) || FileUtil.createOrExistsFile(file)) return false;
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file, append));
            bos.write(bytes);
            IOUtil.flush(bos);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.close(bos);
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param content 写入内容
     * @param path    文件路径
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final String path, @Nullable final CharSequence content) {
        return toFile(FileUtil.getFile(path), content, false);
    }

    /**
     * 将字符串写入文件
     *
     * @param content 写入内容
     * @param path    文件路径
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final String path, @Nullable final CharSequence content, final boolean append) {
        return toFile(FileUtil.getFile(path), content, append);
    }

    /**
     * 将字符串写入文件
     *
     * @param content 写入内容
     * @param file    文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final File file, @Nullable final CharSequence content) {
        return toFile(file, content, false);
    }

    /**
     * 将字符串写入文件
     *
     * @param content 写入内容
     * @param file    文件
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFile(@Nullable final File file, @Nullable final CharSequence content, final boolean append) {
        if (StringUtil.isEmpty(content) || !FileUtil.createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content.toString());
            IOUtil.flush(bw);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.close(bw);
        }
    }

    /**
     * 将字节数组写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节数组
     * @param isForce  是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannel(@Nullable final String filePath, @Nullable final byte[] bytes, final boolean isForce) {
        return toFileChannel(FileUtil.getFile(filePath), bytes, false, isForce);
    }

    /**
     * 将字节数组写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节数组
     * @param append   是否追加在文件末
     * @param isForce  是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannel(@Nullable final String filePath, @Nullable final byte[] bytes, final boolean append, final boolean isForce) {
        return toFileChannel(FileUtil.getFile(filePath), bytes, append, isForce);
    }

    /**
     * 将字节数组写入文件
     *
     * @param file    文件
     * @param bytes   字节数组
     * @param isForce 是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannel(@Nullable final File file, @Nullable final byte[] bytes, final boolean isForce) {
        return toFileChannel(file, bytes, false, isForce);
    }

    /**
     * 将字节数组写入文件
     *
     * @param file    文件
     * @param bytes   字节数组
     * @param append  是否追加在文件末
     * @param isForce 是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannel(@Nullable final File file, @Nullable final byte[] bytes, final boolean append, final boolean isForce) {
        if (ArrayUtil.isEmpty(bytes) || !FileUtil.createOrExistsFile(file)) return false;
        FileChannel fc = null;
        try {
            fc = new FileOutputStream(file, append).getChannel();
            fc.position(fc.size());
            fc.write(ByteBuffer.wrap(bytes));
            if (isForce) fc.force(true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.close(fc);
        }
    }

    /**
     * 将字节数组写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节数组
     * @param isForce  是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannelMap(@Nullable final String filePath, @Nullable final byte[] bytes, final boolean isForce) {
        return toFileChannelMap(FileUtil.getFile(filePath), bytes, false, isForce);
    }

    /**
     * 将字节数组写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节数组
     * @param append   是否追加在文件末
     * @param isForce  是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannelMap(@Nullable final String filePath, @Nullable final byte[] bytes, final boolean append,
            final boolean isForce) {
        return toFileChannelMap(FileUtil.getFile(filePath), bytes, append, isForce);
    }

    /**
     * 将字节数组写入文件
     *
     * @param file    文件
     * @param bytes   字节数组
     * @param isForce 是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannelMap(@Nullable final File file, @Nullable final byte[] bytes, final boolean isForce) {
        return toFileChannelMap(file, bytes, false, isForce);
    }

    /**
     * 将字节数组写入文件
     *
     * @param file    文件
     * @param bytes   字节数组
     * @param append  是否追加在文件末
     * @param isForce 是否写入文件
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean toFileChannelMap(@Nullable final File file, @Nullable final byte[] bytes, final boolean append, final boolean isForce) {
        if (!FileUtil.createOrExistsFile(file) || ArrayUtil.isEmpty(bytes)) return false;
        FileChannel fc = null;
        try {
            fc = new FileOutputStream(file, append).getChannel();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bytes.length);
            mbb.put(bytes);
            if (isForce) mbb.force();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.close(fc);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // the divide line of write and read
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 读取文件到字符串链表中
     *
     * @param filePath 文件路径
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final String filePath) {
        return toList(FileUtil.getFile(filePath), null);
    }

    /**
     * 读取文件到字符串链表中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final String filePath, @Nullable final String charsetName) {
        return toList(FileUtil.getFile(filePath), charsetName);
    }

    /**
     * 读取文件到字符串链表中
     *
     * @param file 文件
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final File file) {
        return toList(file, null, 0, 0x7FFFFFFF);
    }

    /**
     * 读取文件到字符串链表中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final File file, @Nullable final String charsetName) {
        return toList(file, charsetName, 0, 0x7FFFFFFF);
    }

    /**
     * 读取文件到字符串链表中
     *
     * @param filePath 文件路径
     * @param start    需要读取的开始行数
     * @param end      需要读取的结束行数
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final String filePath, final int start, final int end) {
        return toList(FileUtil.getFile(filePath), null, start, end);
    }

    /**
     * 读取文件到字符串链表中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @param start       需要读取的开始行数
     * @param end         需要读取的结束行数
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final String filePath, @Nullable final String charsetName, final int start, final int end) {
        return toList(FileUtil.getFile(filePath), charsetName, start, end);
    }

    /**
     * 读取文件到字符串链表中
     *
     * @param file  文件
     * @param start 需要读取的开始行数
     * @param end   需要读取的结束行数
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final File file, final int start, final int end) {
        return toList(file, null, start, end);
    }

    /**
     * 读取文件到字符串链表中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @param start       需要读取的开始行数
     * @param end         需要读取的结束行数
     * @return 字符串链表中
     */
    @Nullable
    public static List<String> toList(@Nullable final File file, @Nullable final String charsetName, final int start, final int end) {
        if (!FileUtil.isFile(file) || start > end) return null;
        BufferedReader reader = null;
        try {
            String line;
            int curLine = 1;
            List<String> list = new ArrayList<>();
            if (null == charsetName || charsetName.length() == 0) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            while ((line = reader.readLine()) != null) {
                if (curLine > end) break;
                if (start <= curLine) list.add(line);
                ++curLine;
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.close(reader);
        }
    }

    /**
     * 读取文件到字符串中
     *
     * @param path 文件路径
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final String path) {
        return toString(FileUtil.getFile(path), Encode.UTF_8);
    }

    /**
     * 读取文件到字符串中
     *
     * @param path    文件路径
     * @param charset 编码格式
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final String path, @NonNull final String charset) {
        return toString(FileUtil.getFile(path), charset);
    }

    /**
     * 读取文件到字符串中
     *
     * @param file 文件
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final File file) {
        return toString(file, Encode.UTF_8);
    }

    /**
     * 读取文件到字符串中
     *
     * @param file    文件
     * @param charset 编码格式
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final File file, @NonNull final String charset) {
        if (!FileUtil.isFile(file)) return null;
        BufferedReader reader = null;
        try {
            final StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));

            String line;
            if ((line = reader.readLine()) != null) {
                sb.append(line);
                while ((line = reader.readLine()) != null) {
                    sb.append(Constants.LINE_SEP).append(line);
                }
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.close(reader);
        }
    }

    /**
     * 读取文件到字节数组中
     *
     * @param path 文件路径
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytes(@Nullable final String path) {
        return toBytes(FileUtil.getFile(path));
    }

    /**
     * 读取文件到字节数组中
     *
     * @param file 文件
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytes(@Nullable final File file) {
        if (!FileUtil.isFile(file)) return null;
        FileInputStream fis = null;
        ByteArrayOutputStream os = null;
        try {
            fis = new FileInputStream(file);
            os = new ByteArrayOutputStream();
            byte[] b = new byte[sBufferSize];
            int len;
            while ((len = fis.read(b, 0, sBufferSize)) != Constants.INVALID) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.close(fis, os);
        }
    }

    /**
     * 读取文件到字节数组中
     *
     * @param path 文件路径
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytesByChannel(@Nullable final String path) {
        return toBytesByChannel(FileUtil.getFile(path));
    }

    /**
     * 读取文件到字节数组中
     *
     * @param file 文件
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytesByChannel(@Nullable final File file) {
        if (!FileUtil.isFile(file)) return null;
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
            while (true) {
                if (!((fc.read(byteBuffer)) > 0)) break;
            }
            return byteBuffer.array();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.close(fc);
        }
    }

    /**
     * 读取文件到字节数组中
     *
     * @param path 文件路径
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytesByChannelMap(final String path) {
        return toBytesByChannelMap(FileUtil.getFile(path));
    }

    /**
     * 读取文件到字节数组中
     *
     * @param file 文件
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytesByChannelMap(@Nullable final File file) {
        if (!FileUtil.isFile(file)) return null;
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            int size = (int) fc.size();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
            byte[] result = new byte[size];
            mbb.get(result, 0, size);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.close(fc);
        }
    }

    public static String createFileName(@NonNull String suffix) {
        return TimeUtil.getTimeString() + suffix;
    }

    public static String createImageFileName() {
        return createFileName(".png");
    }

    public static String createMusicFileName() {
        return createFileName(".mp3");
    }

    public static String createVideoFileName() {
        return createFileName(".mp4");
    }
}
