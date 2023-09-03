package com.colin.library.android.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.colin.library.android.utils.data.Constants;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 20:07
 * <p>
 * 描述： IO Util
 */
public final class IOUtil {
    private IOUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    ///////////////////////////////////////////////////////////////////////////
    // 转 Byte[]
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getBytes(@Nullable final CharSequence input) {
        return StringUtil.isEmpty(input) ? null : getBytes(input.toString(), Charset.defaultCharset());
    }

    @Nullable
    public static byte[] getBytes(@Nullable final CharSequence input, @NonNull final Charset charset) {
        return StringUtil.isEmpty(input) ? null : getBytes(input.toString(), charset);
    }

    @Nullable
    public static byte[] getBytes(@Nullable final String input) {
        return StringUtil.isEmpty(input) ? null : getBytes(input, Charset.defaultCharset());
    }

    @Nullable
    public static byte[] getBytes(@Nullable final String input, @NonNull final Charset charset) {
        return StringUtil.isEmpty(input) ? null : input.getBytes(charset);
    }

    @Nullable
    public static byte[] getBytes(@Nullable final InputStream input) {
        if (input == null) return null;
        ByteArrayOutputStream output = null;
        byte[] bytes = null;
        try {
            output = new ByteArrayOutputStream();
            write(input, output);
            bytes = output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(input, output);
        }
        return bytes;
    }


    @Nullable
    public static byte[] getBytes(@Nullable final Reader input) {
        if (input == null) return null;
        ByteArrayOutputStream output = null;
        byte[] bytes = null;
        try {
            output = new ByteArrayOutputStream();
            write(input, output);
            bytes = output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(input, output);
        }
        return bytes;
    }

    @Nullable
    public static byte[] getBytes(@Nullable final Reader input, @NonNull final Charset charset) {
        if (input == null) return null;
        ByteArrayOutputStream output = null;
        byte[] bytes = null;
        try {
            output = new ByteArrayOutputStream();
            write(input, output, charset);
            bytes = output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(input, output);
        }
        return bytes;
    }

    @Nullable
    public static byte[] getBytes(@NonNull final InputStream input, final int size) {
        if (size <= 0) return null;
        final byte[] data = new byte[size];
        int offset = 0;
        int byteCount;
        try {
            while ((offset < size) && (byteCount = input.read(data, offset, size - offset)) != -1)
                offset += byteCount;
            if (offset != size) return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 转 Char[]
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static char[] getChars(@Nullable final CharSequence input) {
        if (StringUtil.isEmpty(input)) return null;
        CharArrayWriter output = null;
        char[] chars = null;
        try {
            output = new CharArrayWriter();
            write(input, output);
            chars = output.toCharArray();
        } catch (IOException e) {
            LogUtil.log(e);
        } finally {
            close(output);
        }
        return chars;
    }

    @Nullable
    public static char[] getChars(@NonNull final InputStream input) {
        CharArrayWriter output = null;
        char[] chars = null;
        try {
            output = new CharArrayWriter();
            write(input, output);
            chars = output.toCharArray();
        } catch (IOException e) {
            LogUtil.log(e);
        } finally {
            close(input, output);
        }
        return chars;
    }

    @Nullable
    public static char[] getChars(@Nullable final InputStream input, @NonNull final Charset charset) {
        if (input == null) return null;
        char[] chars = null;
        CharArrayWriter output = null;
        try {
            output = new CharArrayWriter();
            write(input, output, charset);
            chars = output.toCharArray();
        } catch (IOException e) {
            LogUtil.log(e);
        } finally {
            close(input, output);
        }
        return chars;
    }

    @Nullable
    public static char[] getChars(@Nullable final Reader input) {
        if (input == null) return null;
        char[] chars = null;
        CharArrayWriter output = null;
        try {
            output = new CharArrayWriter();
            write(input, output);
            chars = output.toCharArray();
        } catch (IOException e) {
            LogUtil.log(e);
        } finally {
            close(input, output);
        }
        return chars;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 转 String
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static String getString(@Nullable final byte[] bytes) {
        return ArrayUtil.isEmpty(bytes) ? null : new String(bytes);
    }

    @Nullable
    public static String getString(@Nullable final byte[] bytes, @NonNull final Charset charset) {
        return ArrayUtil.isEmpty(bytes) ? null : new String(bytes, charset);
    }

    @Nullable
    public static String getString(@Nullable final InputStream input) {
        return getString(getBytes(input));
    }

    @Nullable
    public static String getString(@Nullable final InputStream input, @NonNull final Charset charset) {
        return getString(getBytes(input), charset);
    }

    @Nullable
    public static String getString(@Nullable final Reader input) {
        return getString(getBytes(input));

    }

    @Nullable
    public static String getString(@Nullable final Reader input, @NonNull final Charset charset) {
        return getString(getBytes(input), charset);
    }


    @Nullable
    public static List<String> readLines(@Nullable final InputStream input) {
        return input == null ? null : readLines(new InputStreamReader(input));
    }

    @Nullable
    public static List<String> readLines(@Nullable final InputStream input, @NonNull final Charset charset) {
        return input == null ? null : readLines(new InputStreamReader(input, charset));
    }

    @Nullable
    public static List<String> readLines(@Nullable final Reader input) {
        if (input == null) return null;
        List<String> list = null;
        BufferedReader reader = null;
        try {
            list = new ArrayList<>();
            reader = toBufferedReader(input);
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            LogUtil.log(e);
        } finally {
            IOUtil.close(input, reader);
        }
        return list;
    }

    ///////////////////////////////////////////////////////////////////////////
    // IO/OS 转化
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    public static BufferedInputStream toBufferedInputStream(@NonNull final InputStream is) {
        return is instanceof BufferedInputStream ? (BufferedInputStream) is : new BufferedInputStream(is);
    }

    @NonNull
    public static BufferedOutputStream toBufferedOutputStream(@NonNull final OutputStream os) {
        return os instanceof BufferedOutputStream ? (BufferedOutputStream) os : new BufferedOutputStream(os);
    }

    @NonNull
    public static BufferedReader toBufferedReader(@NonNull final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    @NonNull
    public static BufferedWriter toBufferedWriter(@NonNull final Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    @NonNull
    public static InputStream toInputStream(@NonNull final CharSequence input) {
        return new ByteArrayInputStream(getBytes(input));
    }

    @NonNull
    public static InputStream toInputStream(@NonNull final CharSequence input, @NonNull final Charset charset) {
        return new ByteArrayInputStream(getBytes(input, charset));
    }

    @NonNull
    public static InputStream toInputStream(@NonNull final String input) {
        return new ByteArrayInputStream(getBytes(input));
    }

    @NonNull
    public static InputStream toInputStream(@NonNull final String input, @NonNull final Charset charset) {
        return new ByteArrayInputStream(getBytes(input, charset));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Write
    ///////////////////////////////////////////////////////////////////////////
    public static void write(@NonNull @Size(min = 0) final byte[] data, @NonNull OutputStream output) throws IOException {
        output.write(data);
    }

    public static void write(@NonNull @Size(min = 0) byte[] data, @NonNull final Writer output) throws IOException {
        output.write(new String(data));
    }

    public static void write(@NonNull @Size(min = 0) byte[] data, @NonNull Writer output, @NonNull final Charset charset) throws IOException {
        output.write(new String(data, charset));
    }

    public static void write(@NonNull @Size(min = 0) char[] data, @NonNull Writer output) throws IOException {
        output.write(data);
    }

    public static void write(@NonNull @Size(min = 0) char[] data, @NonNull OutputStream output) throws IOException {
        output.write(new String(data).getBytes());
    }

    public static void write(@NonNull @Size(min = 0) char[] data, @NonNull OutputStream output, @NonNull final Charset charset) throws IOException {
        output.write(new String(data).getBytes(charset));
    }

    public static void write(@NonNull CharSequence data, @NonNull Writer output) throws IOException {
        output.write(data.toString());
    }

    public static void write(@NonNull CharSequence data, @NonNull OutputStream output) throws IOException {
        output.write(data.toString().getBytes());
    }

    public static void write(@NonNull CharSequence data, @NonNull OutputStream output, @NonNull final Charset charset) throws IOException {
        output.write(data.toString().getBytes(charset));
    }


    public static void write(@NonNull final InputStream input, @NonNull final Writer output) throws IOException {
        write(new InputStreamReader(input), output);
    }


    public static void write(@NonNull final InputStream input, @NonNull final OutputStream output, @NonNull final Charset charset) throws IOException {
        write(new InputStreamReader(input, charset), output);
    }

    public static void write(@NonNull final InputStream input, @NonNull final Writer output, @NonNull final Charset charset) throws IOException {
        write(new InputStreamReader(input, charset), output);
    }

    public static void write(@NonNull final Reader input, @NonNull final Writer output) throws IOException {
        final char[] buffer = new char[Constants.BUFFER_STREAM_SIZE];
        int len;
        while (-1 != (len = input.read(buffer))) output.write(buffer, 0, len);
    }


    public static void write(@NonNull final InputStream is, @NonNull final OutputStream os) throws IOException {
        final byte[] buffer = new byte[Constants.BUFFER_STREAM_SIZE];
        int len;
        while ((len = is.read(buffer)) != -1) os.write(buffer, 0, len);
    }

    public static void write(@NonNull final Reader input, @NonNull final OutputStream output) throws IOException {
        final Writer out = new OutputStreamWriter(output);
        write(input, out);
        out.flush();
    }

    public static void write(@NonNull final Reader input, @NonNull final OutputStream output, @NonNull final Charset charset) throws IOException {
        final Writer out = new OutputStreamWriter(output, charset);
        write(input, out);
        out.flush();
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
