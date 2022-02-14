package com.colin.library.android.utils.data;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2021-12-25 08:09
 * <p>
 * 描述： Util 常量
 */
public interface Constants {
    //文件分隔符  xx/xx
    String FILE_SEP = System.getProperty("file.separator");
    //行分割线
    String LINE_SEP = System.getProperty("line.separator");
    int INVALID = -1;
    //文件读写缓冲区大小
    int FILE_BUFFER_SIZE = 10 * 1024;
    @NonNull
    String FORMAT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";

    //加解密统一编码方式
    @NonNull
    String ENCODE_GBK = "GBK";
    @NonNull
    String ENCODE_GB_2312 = "GB2312";
    @NonNull
    String ENCODE_8859_1 = "8859_1";
    @NonNull
    String UTF_8 = "UTF-8";
    @NonNull
    String ENCODE_UNICODE = "Unicode";
    @NonNull
    String ENCODE_UTF_16 = "UTF-16";
    @NonNull
    String ENCODE_UTF_16BE = "UTF-16BE";
    @NonNull
    String ENCODE_UTF_32 = "UTF-32";
    @NonNull
    String ENCODE_ISO_8859_1 = "iso8859-1";
    /*常见加密*/
    @NonNull
    byte[] DIGITS_BYTE_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    @NonNull
    byte[] DIGITS_BYTE_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

}
