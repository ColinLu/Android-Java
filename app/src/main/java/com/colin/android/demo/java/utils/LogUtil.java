package com.colin.android.demo.java.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.utils.data.Constants;
import com.colin.android.demo.java.utils.data.UtilHelper;

import java.util.Formatter;
import java.util.Locale;

/**
 * 作者： ColinLu
 * 时间： 2021-12-17 22:58
 * <p>
 * 描述： 日志工具类
 */
public final class LogUtil {
    private static final String PLACEHOLDER = " ";

    private LogUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static void v(@Nullable String msg) {
        print(Log.VERBOSE, null, msg);
    }

    public static void v(String format, Object... args) {
        print(Log.VERBOSE, null, String.format(Locale.getDefault(), format, args));
    }

    public static void v(String tag, String format, Object... args) {
        print(Log.VERBOSE, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void d(@Nullable String msg) {
        print(Log.DEBUG, null, msg);
    }

    public static void d(String format, Object... args) {
        print(Log.DEBUG, null, String.format(Locale.getDefault(), format, args));
    }

    public static void d(String tag, String format, Object... args) {
        print(Log.DEBUG, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void i(@Nullable String msg) {
        print(Log.INFO, null, msg);
    }

    public static void i(String format, Object... args) {
        print(Log.INFO, null, String.format(Locale.getDefault(), format, args));
    }

    public static void i(String tag, String format, Object... args) {
        print(Log.INFO, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void w(@Nullable String msg) {
        print(Log.WARN, null, msg);
    }

    public static void w(String format, Object... args) {
        print(Log.WARN, null, String.format(Locale.getDefault(), format, args));
    }

    public static void w(String tag, String format, Object... args) {
        print(Log.WARN, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void e(@Nullable String msg) {
        print(Log.ERROR, null, msg);
    }

    public static void e(String format, Object... args) {
        print(Log.ERROR, null, String.format(Locale.getDefault(), format, args));
    }

    public static void e(String tag, String format, Object... args) {
        print(Log.ERROR, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void a(@Nullable String msg) {
        print(Log.ASSERT, null, msg);
    }

    public static void a(String format, Object... args) {
        print(Log.ASSERT, null, String.format(Locale.getDefault(), format, args));
    }

    public static void a(String tag, String format, Object... args) {
        print(Log.ASSERT, tag, String.format(Locale.getDefault(), format, args));
    }

    public static void log(Exception e) {
        print(Log.ERROR, null, format(e));
    }


    private static void print(int priority, @Nullable String tag, @Nullable String msg) {
        if (!UtilHelper.getInstance().showLog(priority)) return;
        final StackTraceElement traceElement = getStackTrace(3);
        final String fileName = getFileName(traceElement);
        print(priority, tag == null ? fileName : tag, getHead(fileName, traceElement), msg);
    }

    private static void print(int priority, @NonNull String tag, @NonNull String head, @Nullable String msg) {
        final StringBuilder sb = new StringBuilder(PLACEHOLDER);
        sb.append(Constants.LINE_SEP).append(head).append(Constants.LINE_SEP).append(msg);
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
        // If name of file is null, should add
        // "-keepattributes SourceFile,LineNumberTable" in proguard file.
        String className = targetElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) className = classNameInfo[classNameInfo.length - 1];
        int index = className.indexOf('$');
        if (index != -1) className = className.substring(0, index);
        return className + ".java";
    }

    private static String format(Throwable e) {
        return null;
    }

    @NonNull
    private static String getHead(@NonNull String fileName, @NonNull final StackTraceElement element) {
        return new Formatter().format("%s.%s(%s:%d)", element.getClassName(),
                element.getMethodName(), fileName, element.getLineNumber()).toString();
    }


}
