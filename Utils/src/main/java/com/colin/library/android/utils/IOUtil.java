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
 * 描述： IO Uitl
 */
public final class IOUtil {
    private IOUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    ///////////////////////////////////////////////////////////////////////////
    // 转 Byte[]
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    public static byte[] getBytes(@NonNull final CharSequence input) {
        return getBytes(input.toString(), Constants.UTF_8);
    }

    @NonNull
    public static byte[] getBytes(@NonNull final CharSequence input, @NonNull final String encode) {
        return getBytes(input.toString(), encode);
    }

    @NonNull
    public static byte[] getBytes(@NonNull final String input) {
        return getBytes(input, Constants.UTF_8);
    }

    @NonNull
    public static byte[] getBytes(@NonNull final String input, @NonNull final String encode) {
        return input.getBytes(Charset.forName(encode));
    }

    @NonNull
    public static byte[] getBytes(@NonNull final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output);
        output.close();
        return output.toByteArray();
    }

    @NonNull
    public static byte[] getBytes(@NonNull final InputStream input, int size) throws IOException {
        if (size < 0) throw new IllegalArgumentException("Size must than zero: " + size);
        if (size == 0) return new byte[0];
        final byte[] data = new byte[size];
        int offset = 0;
        int byteCount;
        while ((offset < size) && (byteCount = input.read(data, offset, size - offset)) != -1)
            offset += byteCount;

        if (offset != size) throw new IOException("current: " + offset + ", excepted: " + size);
        return data;
    }

    @NonNull
    public static byte[] getBytes(@NonNull final Reader input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output);
        output.close();
        return output.toByteArray();
    }

    @NonNull
    public static byte[] getBytes(@NonNull final Reader input, @NonNull final String encode) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(input, output, encode);
        output.close();
        return output.toByteArray();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 转 Char[]
    ///////////////////////////////////////////////////////////////////////////
    public static char[] getChars(@NonNull final CharSequence input) throws IOException {
        final CharArrayWriter output = new CharArrayWriter();
        write(input, output);
        return output.toCharArray();
    }

    public static char[] getChars(@NonNull final InputStream input) throws IOException {
        final CharArrayWriter output = new CharArrayWriter();
        write(input, output);
        return output.toCharArray();
    }

    public static char[] getChars(@NonNull final InputStream input, String encode) throws IOException {
        final CharArrayWriter output = new CharArrayWriter();
        write(input, output, encode);
        return output.toCharArray();
    }

    public static char[] getChars(@NonNull final Reader input) throws IOException {
        final CharArrayWriter output = new CharArrayWriter();
        write(input, output);
        return output.toCharArray();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 转 String
    ///////////////////////////////////////////////////////////////////////////
    public static String getString(@NonNull final InputStream input) throws IOException {
        return new String(getBytes(input));
    }

    public static String getString(@NonNull final InputStream input, @NonNull final String encode) throws IOException {
        return new String(getBytes(input), encode);
    }

    public static String getString(@NonNull final Reader input) throws IOException {
        return new String(getBytes(input));
    }

    public static String getString(@NonNull final Reader input, @NonNull final String encode) throws IOException {
        return new String(getBytes(input), encode);
    }

    public static String getString(@NonNull final byte[] bytes) {
        return new String(bytes);
    }

    public static String getString(@NonNull final byte[] bytes, @NonNull final String encode) {
        return new String(bytes, Charset.forName(encode));
    }

    public static List<String> readLines(@NonNull final InputStream input, @NonNull final String encode) throws IOException {
        return readLines(new InputStreamReader(input, encode));
    }

    public static List<String> readLines(@NonNull final InputStream input) throws IOException {
        return readLines(new InputStreamReader(input));
    }

    public static List<String> readLines(@NonNull final Reader input) throws IOException {
        final BufferedReader reader = toBufferedReader(input);
        final List<String> list = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            list.add(line);
            line = reader.readLine();
        }
        return list;
    }

    ///////////////////////////////////////////////////////////////////////////
    // IO/OS 转化
    ///////////////////////////////////////////////////////////////////////////
    public static BufferedInputStream toBufferedInputStream(@NonNull final InputStream is) {
        return is instanceof BufferedInputStream ? (BufferedInputStream) is : new BufferedInputStream(is);
    }

    public static BufferedOutputStream toBufferedOutputStream(@NonNull final OutputStream os) {
        return os instanceof BufferedOutputStream ? (BufferedOutputStream) os : new BufferedOutputStream(os);
    }

    public static BufferedReader toBufferedReader(@NonNull final Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static BufferedWriter toBufferedWriter(@NonNull final Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    public static InputStream toInputStream(@NonNull final CharSequence input) {
        return new ByteArrayInputStream(getBytes(input));
    }

    public static InputStream toInputStream(@NonNull final CharSequence input, @NonNull final String encode) {
        return new ByteArrayInputStream(getBytes(input, encode));
    }

    public static InputStream toInputStream(@NonNull final String input) {
        return new ByteArrayInputStream(getBytes(input));
    }

    public static InputStream toInputStream(@NonNull final String input, @NonNull final String encode) {
        return new ByteArrayInputStream(getBytes(input, encode));
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

    public static void write(@NonNull @Size(min = 0) byte[] data, Writer output, String encoding) throws IOException {
        output.write(new String(data, encoding));
    }

    public static void write(@NonNull @Size(min = 0) char[] data, Writer output) throws IOException {
        output.write(data);
    }

    public static void write(@NonNull @Size(min = 0) char[] data, OutputStream output) throws IOException {
        output.write(new String(data).getBytes());
    }

    public static void write(@NonNull @Size(min = 0) char[] data, OutputStream output, String encoding) throws IOException {
        output.write(new String(data).getBytes(encoding));
    }

    public static void write(@NonNull CharSequence data, Writer output) throws IOException {
        output.write(data.toString());
    }

    public static void write(@NonNull CharSequence data, OutputStream output) throws IOException {
        output.write(data.toString().getBytes());
    }

    public static void write(@NonNull CharSequence data, OutputStream output, String encoding) throws IOException {
        output.write(data.toString().getBytes(encoding));
    }

    public static void write(@NonNull final InputStream is, @NonNull final OutputStream os) throws IOException {
        final byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) != -1) os.write(buffer, 0, len);
    }

    public static void write(@NonNull final Reader input, @NonNull final OutputStream output) throws IOException {
        final Writer out = new OutputStreamWriter(output);
        write(input, out);
        out.flush();
    }

    public static void write(@NonNull final InputStream input, @NonNull final Writer output) throws IOException {
        write(new InputStreamReader(input), output);
    }

    public static void write(@NonNull final Reader input, @NonNull final OutputStream output, @NonNull final String encode) throws IOException {
        final Writer out = new OutputStreamWriter(output, encode);
        write(input, out);
        out.flush();
    }

    public static void write(@NonNull final InputStream input, @NonNull final OutputStream output, @NonNull final String encode) throws IOException {
        write(new InputStreamReader(input, encode), output);
    }

    public static void write(@NonNull final InputStream input, @NonNull final Writer output, @NonNull final String encode) throws IOException {
        write(new InputStreamReader(input, encode), output);
    }

    public static void write(@NonNull final Reader input, @NonNull final Writer output) throws IOException {
        final char[] buffer = new char[4096];
        int len;
        while (-1 != (len = input.read(buffer))) output.write(buffer, 0, len);
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
