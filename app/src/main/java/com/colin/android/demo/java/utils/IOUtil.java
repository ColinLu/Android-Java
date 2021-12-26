package com.colin.android.demo.java.utils;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 20:07
 * <p>
 * 描述： IO Uitl
 */
public final class IOUtil {
    private IOUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /**
     * 关闭数据流
     *
     * @param closeables closeables
     */
    public static void close(@Nullable final Closeable... closeables) {
        if (closeables == null || closeables.length == 0) return;
        for (Closeable closeable : closeables) {
            if (null == closeable) continue;
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * flush() 是清空，而不是刷新啊。
     * 一般主要用在IO中，即清空缓冲区数据，就是说你用读写流的时候，其实数据是先被读到了内存中，
     * 然后用数据写到文件中，当你数据读完的时候不代表你的数据已经写完了，因为还有一部分有可能会留在内存这个缓冲区中。
     * 这时候如果你调用了 close()方法关闭了读写流，那么这部分数据就会丢失，所以应该在关闭读写流之前先flush()，先清空数据。
     * <p>
     * 需要将FileOutputStream作为BufferedOutputStream构造函数的参数传入，
     * 然后对BufferedOutputStream进行写入操作，才能利用缓冲及flush()。
     *
     * @param flushables
     */
    public static void flush(final Flushable... flushables) {
        if (flushables == null || flushables.length == 0) return;
        for (Flushable flush : flushables) {
            if (null == flush) continue;
            try {
                flush.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
