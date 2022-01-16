package com.colin.library.android.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

    /**
     * Write file from string.
     *
     * @param file    The file.
     * @param content The string of content.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean toFile(final File file, final String content) {
        return toFile(file, content, false);
    }

    /**
     * Write file from string.
     *
     * @param file    The file.
     * @param content The string of content.
     * @param append  True to append, false otherwise.
     * @return {@code true}: success<br>{@code false}: fail
     */
    public static boolean toFile(final File file, final String content, final boolean append) {
        if (file == null || content == null) return false;
        if (!FileUtil.createOrExistsFile(file)) {
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtil.close(bw);
            IOUtil.flush(bw);
        }
    }

}
