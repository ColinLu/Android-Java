package com.colin.library.android.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.utils.data.UtilHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Formatter;

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
    private static final String PLACEHOLDER = " ";
    private static final int INDENT_SPACES = 4;
    private static final String NULL = "null";
    private static final String MSG = "Msg";
    private static final String BORDER_TOP = "|———————————————————————————————————————————————————————————————————————————————————————————|";
    private static final String BORDER_BOTTOM = "|___________________________________________________________________________________________|";

    private LogUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static void v(Object... args) {
        print(Log.VERBOSE, null, format(args));
    }

    public static void vTag(@NonNull String tag, @Nullable Object... args) {
        print(Log.VERBOSE, tag, format(args));
    }

    public static void d(@Nullable Object... args) {
        print(Log.DEBUG, null, format(args));
    }

    public static void dTag(@NonNull String tag, Object... args) {
        print(Log.DEBUG, tag, format(args));
    }

    @SafeVarargs
    public static void i(@Nullable Object... args) {
        print(Log.INFO, null, format(args));
    }

    public static void iTag(@NonNull String tag, @Nullable Object... args) {
        print(Log.INFO, tag, format(args));
    }

    public static void w(@Nullable Object... args) {
        print(Log.WARN, null, format(args));
    }

    public static void wTag(@NonNull String tag, @Nullable Object... args) {
        print(Log.WARN, tag, format(args));
    }

    public static void e(@Nullable Object... args) {
        print(Log.ERROR, null, format(args));
    }

    public static void eTag(@NonNull String tag, @Nullable Object... args) {
        print(Log.ERROR, tag, format(args));
    }

    public static void a(@Nullable Object... args) {
        print(Log.ASSERT, null, format(args));
    }

    public static void aTag(@NonNull String tag, @Nullable Object... args) {
        print(Log.ASSERT, tag, format(args));
    }

    public static void log(@NonNull Throwable e) {
        print(Log.ERROR, null, format(e));
    }

    public static void logTag(@NonNull String tag, @NonNull Throwable e) {
        print(Log.ERROR, tag, format(e));
    }

    private static void print(int priority, @Nullable String tag, @Nullable String msg) {
        if (!UtilHelper.getInstance().showLog(priority)) return;
        final StackTraceElement traceElement = getStackTrace(3);
        final String fileName = getFileName(traceElement);
        print(priority, tag == null ? fileName : tag, getHead(fileName, traceElement), msg);
    }

    private static void print(int priority, @NonNull String tag, @NonNull String head, @Nullable String msg) {
        final StringBuilder sb = new StringBuilder(PLACEHOLDER);
        sb.append(Constants.LINE_SEP).append(BORDER_TOP).append(Constants.LINE_SEP)
                .append(head).append(Constants.LINE_SEP)
                .append(msg).append(Constants.LINE_SEP)
                .append(BORDER_BOTTOM);
        Log.println(priority, tag, sb.toString());
    }

    @NonNull
    private static StackTraceElement getStackTrace(int index) {
        final StackTraceElement[] traceElements = new Throwable().getStackTrace();
        return traceElements[index];
    }

    @NonNull
    private static String getFileName(@NonNull final StackTraceElement targetElement) {
        String fileName = targetElement.getFileName();
        if (fileName != null) return fileName;
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) className = classNameInfo[classNameInfo.length - 1];
        int index = className.indexOf('$');
        if (index != -1) className = className.substring(0, index);
        return className + ".java";
    }

    @NonNull
    private static String getHead(@NonNull String fileName, @NonNull final StackTraceElement element) {
        return new Formatter().format("%s.%s(%s:%d)", element.getClassName(), element.getMethodName(), fileName, element.getLineNumber()).toString();
    }

    private static String format(@Nullable Object... args) {
        final int len = args == null ? 0 : args.length;
        if (len == 0) return NULL;
        if (len == 1) return format(args[0]);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final Object obj = args[i];
            sb.append(MSG).append('[').append(i).append(']').append(" = ").append(format(obj)).append(Constants.LINE_SEP);
        }
        return sb.toString().trim();
    }

    private static String format(@Nullable final Object obj) {
        final String msg = obj == null ? null : obj.toString();
        if (obj == null) return NULL;
        if ((msg.startsWith("[") && msg.endsWith("]")) || (msg.startsWith("{") && msg.endsWith("}")))
            return formatJson(msg);
        else if (msg.startsWith("<?xml")) return formatXml(msg);
        return msg;
    }

    public static String format(@NonNull final Throwable error) {
        final StringBuilder sb = new StringBuilder();
        sb.append(Constants.LINE_SEP).append("reason:");
        if (error.getMessage() != null) sb.append(error.getMessage()).append('\n');
        if (error.getCause() != null) sb.append(error.getCause().toString()).append('\n');
        StackTraceElement[] traceElements = error.getStackTrace();
        for (StackTraceElement traceElement : traceElements) {
            sb.append("at ").append(traceElement.toString()).append('\n');
        }
        return sb.toString().trim();
    }

    public static String formatJson(@NonNull final String json) {
        try {
            for (int i = 0, len = json.length(); i < len; i++) {
                char c = json.charAt(i);
                if (c == '{') {
                    return new JSONObject(json).toString(INDENT_SPACES);
                } else if (c == '[') {
                    return new JSONArray(json).toString(INDENT_SPACES);
                } else if (!Character.isWhitespace(c)) {
                    return json;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private static String formatXml(@NonNull final String xml) {
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">" + Constants.LINE_SEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }
}
