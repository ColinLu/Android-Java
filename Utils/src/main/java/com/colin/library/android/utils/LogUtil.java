package com.colin.library.android.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.LogLevel;
import com.colin.library.android.helper.UtilHelper;
import com.colin.library.android.utils.data.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 作者： ColinLu
 * 时间： 2021-12-17 22:58
 * <p>
 * 描述： 日志工具类
 */
public final class LogUtil {
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;
    private static final String TAB_SPACE = "    ";
    private static final String POINT = ".";
    private static final String MSG = "value";
    private static final String LOG_EMPTY = "null";
    private static final String XML_PROPERTY_NAME = "{http://xml.apache.org/xslt}indent-amount";
    private static final int INDENT_SPACES = 4;
    private static final int LOG_COUNT = 4000;

    private LogUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外公开api
    ///////////////////////////////////////////////////////////////////////////
    public static void v(@Nullable String msg) {
        print(LogLevel.V, UtilHelper.getInstance().getLogTag(), msg == null ? LOG_EMPTY : msg);
    }

    public static void v(@NonNull String tag, @Nullable String msg) {
        print(LogLevel.V, tag, msg == null ? LOG_EMPTY : msg);
    }

    public static void v(@NonNull String format, Object... args) {
        print(LogLevel.V, UtilHelper.getInstance().getLogTag(), String.format(format, args));
    }

    public static void v(@NonNull String tag, @NonNull String format, Object... args) {
        print(LogLevel.V, tag, String.format(format, args));
    }

    public static void d(@Nullable String msg) {
        print(LogLevel.D, UtilHelper.getInstance().getLogTag(), msg == null ? LOG_EMPTY : msg);
    }

    public static void d(@NonNull String tag, @Nullable String msg) {
        print(LogLevel.D, tag, msg == null ? LOG_EMPTY : msg);
    }

    public static void d(@NonNull String format, Object... args) {
        print(LogLevel.D, UtilHelper.getInstance().getLogTag(), String.format(format, args));
    }

    public static void d(@NonNull String tag, @NonNull String format, Object... args) {
        print(LogLevel.D, tag, String.format(format, args));
    }

    public static void i(@Nullable String msg) {
        print(LogLevel.I, UtilHelper.getInstance().getLogTag(), msg == null ? LOG_EMPTY : msg);
    }

    public static void i(@NonNull String tag, @Nullable String msg) {
        print(LogLevel.I, tag, msg == null ? LOG_EMPTY : msg);
    }

    public static void i(@NonNull String format, Object... args) {
        print(LogLevel.I, UtilHelper.getInstance().getLogTag(), String.format(Locale.getDefault(), format, args));
    }

    public static void i(@NonNull String tag, @NonNull String format, Object... args) {
        print(LogLevel.I, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void w(@Nullable String msg) {
        print(LogLevel.W, UtilHelper.getInstance().getLogTag(), msg == null ? LOG_EMPTY : msg);
    }

    public static void w(@NonNull String tag, @Nullable String msg) {
        print(LogLevel.W, tag, msg == null ? LOG_EMPTY : msg);
    }

    public static void w(@NonNull String format, Object... args) {
        print(LogLevel.W, UtilHelper.getInstance().getLogTag(), String.format(Locale.getDefault(), format, args));
    }

    public static void w(@NonNull String tag, @NonNull String format, Object... args) {
        print(LogLevel.W, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void e(@Nullable String msg) {
        print(LogLevel.E, UtilHelper.getInstance().getLogTag(), msg == null ? LOG_EMPTY : msg);
    }

    public static void e(@NonNull String tag, @Nullable String msg) {
        print(LogLevel.E, tag, msg == null ? LOG_EMPTY : msg);
    }

    public static void e(@NonNull String format, Object... args) {
        print(LogLevel.E, UtilHelper.getInstance().getLogTag(), String.format(Locale.getDefault(), format, args));
    }

    public static void e(@NonNull String tag, @NonNull String format, Object... args) {
        print(LogLevel.E, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void a(@Nullable String msg) {
        print(LogLevel.A, UtilHelper.getInstance().getLogTag(), msg == null ? LOG_EMPTY : msg);
    }

    public static void a(@NonNull String tag, @Nullable String msg) {
        print(LogLevel.A, tag, msg == null ? LOG_EMPTY : msg);
    }

    public static void a(@NonNull String format, Object... args) {
        print(LogLevel.A, UtilHelper.getInstance().getLogTag(), String.format(Locale.getDefault(), format, args));
    }

    public static void a(@NonNull String tag, @NonNull String format, Object... args) {
        print(LogLevel.A, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void log(@NonNull Throwable error) {
        print(LogLevel.E, UtilHelper.getInstance().getLogTag(), format(error));
    }

    public static void log(@NonNull String tag, @NonNull Throwable error) {
        print(LogLevel.E, tag, format(error));
    }

    public static void log(@LogLevel int level, @NonNull String tag, @NonNull Throwable error) {
        print(level, tag, format(error));
    }

    public static void log(@Nullable String msg) {
        print(UtilHelper.getInstance().getLogLevel(), UtilHelper.getInstance().getLogTag(), msg);
    }

    public static void log(@NonNull String format, @NonNull Object... args) {
        print(UtilHelper.getInstance().getLogLevel(), UtilHelper.getInstance().getLogTag(), String.format(Locale.getDefault(), format, args));
    }

    public static void json(@Nullable Object obj) {
        print(UtilHelper.getInstance().getLogLevel(), UtilHelper.getInstance().getLogTag(), formatJson(obj));
    }

    public static void json(@NonNull String tag, @Nullable Object obj) {
        print(UtilHelper.getInstance().getLogLevel(), tag, formatJson(obj));
    }

    public static void json(@LogLevel int level, @Nullable Object obj) {
        print(level, UtilHelper.getInstance().getLogTag(), formatJson(obj));
    }

    public static void json(@LogLevel int level, @NonNull String tag, @Nullable Object obj) {
        print(level, tag, formatJson(obj));
    }

    public static void xml(@Nullable String xml) {
        print(UtilHelper.getInstance().getLogLevel(), UtilHelper.getInstance().getLogTag(), formatXml(xml));
    }

    public static void xml(@NonNull String tag, @Nullable String xml) {
        print(UtilHelper.getInstance().getLogLevel(), tag, formatXml(xml));
    }

    public static void xml(@LogLevel int level, @Nullable String xml) {
        print(level, UtilHelper.getInstance().getLogTag(), formatXml(xml));
    }

    public static void xml(@LogLevel int level, @NonNull String tag, @Nullable String xml) {
        print(level, tag, formatXml(xml));
    }

    @NonNull
    public static String format(@Nullable Object... args) {
        final int len = args == null ? Constants.ZERO : args.length;
        if (len == Constants.ZERO) return LOG_EMPTY;
        if (len == 1) return StringUtil.toString(args[Constants.ZERO]);
        final StringBuilder sb = new StringBuilder();
        for (int i = Constants.ZERO; i < len; i++) {
            sb.append(MSG).append('[').append(i).append(']').append(" = ").append(StringUtil.toString(args[i])).append(Constants.LINE_SEP);
        }
        return sb.toString().trim();
    }

    @NonNull
    public static String format(@NonNull final Throwable error) {
        Throwable t = error;
        while (t != null) {
            if (t instanceof UnknownHostException) return "UnknownHostException";
            t = t.getCause();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    @NonNull
    public static String formatJson(@Nullable final Object obj) {
        if (obj == null) return "json is null";
        try {
            if (obj instanceof JSONObject) return ((JSONObject) obj).toString(INDENT_SPACES);
            if (obj instanceof JSONArray) return ((JSONArray) obj).toString(INDENT_SPACES);
            final String json = obj.toString();
            for (int i = Constants.ZERO, len = json.length(); i < len; i++) {
                final char c = json.charAt(i);
                if (c == '{') return new JSONObject(json).toString(INDENT_SPACES);
                else if (c == '[') return new JSONArray(json).toString(INDENT_SPACES);
                else if (!Character.isWhitespace(c)) return json;
            }
        } catch (Exception e) {
            return format(e);
        }
        return StringUtil.toString(obj);
    }

    @NonNull
    public static String formatXml(@Nullable final String xml) {
        if (StringUtil.isEmpty(xml)) return "xml is null";
        try {
            final Source xmlInput = new StreamSource(new StringReader(xml));
            final StreamResult xmlOutput = new StreamResult(new StringWriter());
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(XML_PROPERTY_NAME, "4");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">" + Constants.LINE_SEP);
        } catch (Exception e) {
            return format(e);
        }
    }

    private static synchronized void print(@LogLevel int level, @Nullable String tag, @NonNull String msg) {
        //判断是否输出
        if (!UtilHelper.getInstance().showLog(level)) return;
        //Java栈信息
        final StackTraceElement[] traces = Thread.currentThread().getStackTrace();
        //print tag
        tag = StringUtil.isEmpty(tag) ? getFileName(traces[4]) : tag;
        //Top Border
        Log.println(level, tag, TOP_BORDER);
        //Head
        printlnHead(level, tag, traces);
        //Message
        final byte[] bytes = msg.getBytes();
        final int length = bytes.length;
        //single
        if (length <= LOG_COUNT) {
            final String[] lines = msg.split(Constants.LINE_SEP);
            for (String line : lines) Log.println(level, tag, HORIZONTAL_LINE + line);
            Log.println(level, tag, BOTTOM_BORDER);
            return;
        }
        //
        for (int i = Constants.ZERO; i < length; i += LOG_COUNT) {
            final int count = Math.min(length - i, LOG_COUNT);
            final String[] lines = new String(bytes, i, count).split(Constants.LINE_SEP);
            for (String line : lines) Log.println(level, tag, HORIZONTAL_LINE + line);
        }
        //Bottom Border
        Log.println(level, tag, BOTTOM_BORDER);
    }

    private static void printlnHead(int level, @NonNull String tag, @NonNull StackTraceElement[] traces) {
        //thread
        if (UtilHelper.getInstance().isShowLogThread()) {
            Log.println(level, tag, HORIZONTAL_LINE + "thread:" + Thread.currentThread().getName());
            Log.println(level, tag, MIDDLE_BORDER);
        }
        //method
        final int count = UtilHelper.getInstance().getLogMethodCount();
        if (count == Constants.ZERO) return;
        final int offset = getStackOffset(traces) + UtilHelper.getInstance().getLogMethodOffset();
        final StringBuilder space = new StringBuilder();
        Formatter formatter;
        for (int i = count; i > Constants.ZERO; i--) {
            final int stackIndex = i + offset;
            if (stackIndex >= traces.length) continue;
            final StackTraceElement trace = traces[stackIndex];
            formatter = new Formatter().format("%s%s.%s(%s:%d)", HORIZONTAL_LINE + space.toString(), trace.getClassName(), trace.getMethodName(), trace.getFileName(), trace.getLineNumber());
            Log.println(level, tag, formatter.toString());
            space.append(TAB_SPACE);
        }
        if (count > Constants.ZERO) Log.println(level, tag, MIDDLE_BORDER);
    }


    private static int getStackOffset(@NonNull final StackTraceElement[] trace) {
        for (int i = 4; i < trace.length; i++) {
            final StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LogUtil.class.getName())) return --i;
        }
        return -1;
    }

    /*for tag*/
    @NonNull
    private static String getFileName(@NonNull final StackTraceElement element) {
        final String fileName = element.getFileName();
        if (fileName.contains(POINT)) return fileName.substring(0, fileName.lastIndexOf(POINT));
        return element.getMethodName();
    }

}
