package com.colin.library.android.utils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.WorkerThread;

import com.colin.library.android.utils.data.Constants;

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
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-16 19:40
 * <p>
 * 描述： 文件流操作
 */
public final class FileIOUtil {
    private FileIOUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    private static int sBufferSize = Constants.FILE_BUFFER_SIZE;

    /**
     * 设置缓冲区尺寸
     *
     * @param bufferSize 缓冲区大小
     */
    public static void setBufferSize(final int bufferSize) {
        sBufferSize = bufferSize;
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final String filePath, @Nullable final InputStream is) {
        return toFile(FileUtil.getFile(filePath), is, false);
    }

    /**
     * 将输入流写入文件
     *
     * @param filePath 路径
     * @param is       输入流
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final String filePath, @Nullable final InputStream is, final boolean append) {
        return toFile(FileUtil.getFile(filePath), is, append);
    }

    /**
     * 将输入流写入文件
     *
     * @param file 文件
     * @param is   输入流
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final File file, @Nullable final InputStream is, final boolean append) {
        if (null == is || !FileUtil.createOrExistsFile(file)) return false;
        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append));
            byte[] data = new byte[sBufferSize];
            int len;
            while ((len = is.read(data, 0, sBufferSize)) != Constants.INVALID) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.flush(os);
            IOUtil.close(is, os);
        }
    }

    /**
     * 将字节数组写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节数组
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final String filePath, @Nullable final byte[] bytes) {
        return toFile(FileUtil.getFile(filePath), bytes, false);
    }

    /**
     * 将字节数组写入文件
     *
     * @param filePath 文件路径
     * @param bytes    字节数组
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final String filePath, @Nullable final byte[] bytes, final boolean append) {
        return toFile(FileUtil.getFile(filePath), bytes, append);
    }

    /**
     * 将字节数组写入文件
     *
     * @param file  文件
     * @param bytes 字节数组
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final File file, @Nullable final byte[] bytes, final boolean append) {
        if (ArrayUtil.isEmpty(bytes) || FileUtil.createOrExistsFile(file)) return false;
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file, append));
            bos.write(bytes);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.flush(bos);
            IOUtil.close(bos);
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final String filePath, @Nullable final String content) {
        return toFile(FileUtil.getFile(filePath), content, false);
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath 文件路径
     * @param content  写入内容
     * @param append   是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final String filePath, @Nullable final String content, final boolean append) {
        return toFile(FileUtil.getFile(filePath), content, append);
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final File file, @Nullable final String content) {
        return toFile(file, content, false);
    }

    /**
     * 将字符串写入文件
     *
     * @param file    文件
     * @param content 写入内容
     * @param append  是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFile(@Nullable final File file, @Nullable final String content, final boolean append) {
        if (StringUtil.isEmpty(content) || !FileUtil.createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.flush(bw);
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static boolean toFileChannelMap(@Nullable final String filePath, @Nullable final byte[] bytes, final boolean append, final boolean isForce) {
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                if (start <= curLine && curLine <= end) list.add(line);
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
     * @param filePath 文件路径
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final String filePath) {
        return toString(FileUtil.getFile(filePath), null);
    }

    /**
     * 读取文件到字符串中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final String filePath, @Nullable final String charsetName) {
        return toString(FileUtil.getFile(filePath), charsetName);
    }

    /**
     * 读取文件到字符串中
     *
     * @param file 文件
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final File file) {
        return toString(file, null);
    }

    /**
     * 读取文件到字符串中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 字符串
     */
    @Nullable
    public static String toString(@Nullable final File file, @Nullable final String charsetName) {
        if (!FileUtil.isFile(file)) return null;
        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (null == charsetName || charsetName.length() == 0) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
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
     * @param filePath 文件路径
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytes(@Nullable final String filePath) {
        return toBytes(FileUtil.getFile(filePath));
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
     * @param filePath 文件路径
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytesByChannel(@Nullable final String filePath) {
        return toBytesByChannel(FileUtil.getFile(filePath));
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
     * @param filePath 文件路径
     * @return 字符数组
     */
    @Nullable
    public static byte[] toBytesByChannelMap(final String filePath) {
        return toBytesByChannelMap(FileUtil.getFile(filePath));
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

    @Nullable
    @WorkerThread
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static String save(@Nullable final Context context, @Nullable final Bitmap bitmap) {
        return save(context, bitmap, true);
    }


    @Nullable
    @WorkerThread
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static String save(@Nullable final Context context, @Nullable final Bitmap bitmap, final boolean recycle) {
        if (null == context || BitmapUtil.isEmpty(bitmap)) return null;
        String path = null;
        try {
            File file = FileUtil.createImageFile(".png");
            path = file.getAbsolutePath();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            if (recycle) BitmapUtil.recycle(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
