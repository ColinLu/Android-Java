package com.colin.library.android.annotation;

import android.util.Log;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-03-07 21:09
 * <p>
 * 描述： Log级别
 */
@IntDef({LogLevel.V, LogLevel.D, LogLevel.I, LogLevel.W, LogLevel.E, LogLevel.A})
@Retention(RetentionPolicy.SOURCE)
public @interface LogLevel {
    int V = Log.VERBOSE;
    int D = Log.DEBUG;
    int I = Log.INFO;
    int W = Log.WARN;
    int E = Log.ERROR;
    int A = Log.ASSERT;
}
