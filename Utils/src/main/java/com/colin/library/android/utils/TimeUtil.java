package com.colin.library.android.utils;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;

import com.colin.library.android.utils.data.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 作者： ColinLu
 * 时间： 2018-12-05 16:50
 * <p>
 * 描述： 时间工具类
 * G 年代标志符
 * y 年
 * M 月
 * d 日
 * h 时 在上午或下午 (1~12)
 * H 时 在一天中 (0~23)
 * m 分
 * s 秒
 * S 毫秒
 * E 星期
 * D 一年中的第几天
 * F 一月中第几个星期几
 * w 一年中第几个星期
 * W 一月中第几个星期
 * a 上午 / 下午 标记符
 * k 时 在一天中 (1~24)
 * K 时 在上午或下午 (0~11)
 * z 时区
 * <p>
 * Java8的DateTimeFormatter是线程安全的，而SimpleDateFormat并不是线程安全。
 */
public final class TimeUtil {
    private TimeUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    private static final Object mLockObj = new Object();
    private static final LruCache<String, ThreadLocal<SimpleDateFormat>> SDF_THREAD_LOCAL = new LruCache<>(10);

    /*获取时间格式化*/
    public static SimpleDateFormat getDateFormat(@NonNull final String pattern) {
        ThreadLocal<SimpleDateFormat> dateFormatterThreadLocal = SDF_THREAD_LOCAL.get(pattern);
        if (null == dateFormatterThreadLocal) {
            synchronized (mLockObj) {//双重检验  多线程并发
                dateFormatterThreadLocal = SDF_THREAD_LOCAL.get(pattern);
                if (null == dateFormatterThreadLocal) {
                    dateFormatterThreadLocal = getLocal(pattern);
                    SDF_THREAD_LOCAL.put(pattern, dateFormatterThreadLocal);
                }
            }
        }
        return dateFormatterThreadLocal.get();
    }

    private static ThreadLocal<SimpleDateFormat> getLocal(@NonNull final String pattern) {
        return new ThreadLocal<SimpleDateFormat>() {
            @NonNull
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(pattern, Locale.getDefault());
            }
        };
    }

    /**
     * 获取当前时间戳格式化字符串
     * yyyy-MM-dd HH:mm:ss:SSS
     **/
    @NonNull
    public static String getTimeString() {
        return getTimeString(System.currentTimeMillis());
    }

    /**
     * 时间戳字符串格式化
     * yyyy-MM-dd HH:mm:ss:SSS
     *
     * @param time 时间戳
     * @return 时间字符串
     */
    @NonNull
    public static String getTimeString(final long time) {
        return getTimeString(Constants.FORMAT_TIME_PATTERN, time);
    }

    /**
     * 当前时间制定格式化成字符串
     *
     * @param pattern 时间格式
     * @return 时间字符串
     */
    @NonNull
    public static String getTimeString(@NonNull final String pattern) {
        return getTimeString(pattern, System.currentTimeMillis());
    }

    /**
     * 格式化时间戳
     *
     * @param pattern 时间格式
     * @param time    时间戳
     * @return 时间字符串
     */
    @NonNull
    public static String getTimeString(@NonNull final String pattern, final long time) {
        return getTimeString(getDateFormat(pattern), time);
    }

    /**
     * 格式化时间戳
     *
     * @param simpleDateFormat 时间格式
     * @return 时间字符串
     */
    @NonNull
    public static String getTimeString(@NonNull final SimpleDateFormat simpleDateFormat) {
        return getTimeString(simpleDateFormat, System.currentTimeMillis());
    }

    /**
     * 格式化时间戳
     *
     * @param simpleDateFormat 时间格式
     * @return 时间字符串
     */
    @NonNull
    public static String getTimeString(@NonNull final SimpleDateFormat simpleDateFormat, final long time) {
        return simpleDateFormat.format(new Date(time));
    }

    public static long getTime(@Nullable String pattern, @Nullable final String time) {
        if (StringUtil.isEmpty(pattern) || StringUtil.isEmpty(time)) return Constants.INVALID;
        try {
            SimpleDateFormat dateFormat = getDateFormat(pattern);
            Date date = dateFormat.parse(time);
            return null == date ? Constants.INVALID : date.getTime();
        } catch (Exception e) {
            LogUtil.log(e);
        }
        return Constants.INVALID;
    }

    public static String format(long second) {
        long days = second / 86400;             //转换天数
        second = second % 86400;                //剩余秒数
        long hours = second / 3600;             //转换小时
        second = second % 3600;                 //剩余秒数
        long minutes = second / 60;             //转换分钟
        second = second % 60;                   //剩余秒数
        if (days > 0) return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        else return hours + "小时" + minutes + "分" + second + "秒";

    }

    /**
     * 时间戳转换成时间格式
     *
     * @param duration 时间间隔
     * @return 时间
     */
    public static String formatDuration(long duration) {
        return String.format(Locale.getDefault(), "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }

    /**
     * 更具时间字符串 转化成日期格式
     *
     * @param timeString   时间字符串
     * @param formatString 时间格式
     * @return 日期
     */
    public static Date getDateByString(String timeString, String formatString) {
        if (StringUtil.isEmpty(timeString)) return new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString, Locale.CHINESE);
        try {
            return simpleDateFormat.parse(timeString);
        } catch (ParseException e) {
            LogUtil.log(e);
            return new Date();
        }
    }

    /**
     * 判断是否闰年
     *
     * @param year 年数
     * @return {@code true} yes, {@code false} no
     */
    public static boolean isLeapYear(int year) {
        if (year <= 0) return false;
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 根据年份、月份, 获取对应的天数 ( 完整天数, 无判断是否属于未来日期 )
     *
     * @param year  年数
     * @param month 月份
     * @return 指定年份所属的月份的天数
     */
    public static int getMonthDayNumberAll(final int year, final int month) {
        // 判断返回的标识数字
        switch (month) {
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
            default:
                return 31;
        }
    }

    public static long parseHttpTime(@Nullable String time) {
        if (TextUtils.isEmpty(time)) return 0;
        final SimpleDateFormat format = getDateFormat(Constants.FORMAT_TIME_HTTP);
        format.setTimeZone(Constants.TIME_ZONE_GMT);
        try {
            final Date date = format.parse(time);
            return null == date ? 0 : date.getTime();
        } catch (ParseException e) {
            LogUtil.log(e);
        }
        return 0;
    }
}
