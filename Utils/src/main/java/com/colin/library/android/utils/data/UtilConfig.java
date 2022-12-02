package com.colin.library.android.utils.data;

import android.app.Application;

import androidx.annotation.NonNull;

import com.colin.library.android.annotation.LogLevel;
import com.colin.library.android.utils.BuildConfig;


/**
 * 作者： ColinLu
 * 时间： 2021-12-18 17:10
 * <p>
 * 描述： Util 配置
 */
public final class UtilConfig {
    /*全局上下文*/
    private final Application mApplication;
    /*环境*/
    private final boolean mDebug;
    /*Log*/
    private final boolean mShowLog;
    @LogLevel
    private final int mLogLevel;

    private UtilConfig(@NonNull Builder builder) {
        this.mApplication = builder.mApplication;
        this.mDebug = builder.mDebug;
        this.mShowLog = builder.mShowLog;
        this.mLogLevel = builder.mLogLevel;
    }


    @NonNull
    public Application getApplication() {
        return mApplication;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public boolean isShowLog() {
        return mShowLog;
    }

    @LogLevel
    public int getLogLevel() {
        return mLogLevel;
    }

    public static class Builder {
        /*全局上下文*/
        private final Application mApplication;
        /*环境*/
        private final boolean mDebug;
        /*Log*/
        private boolean mShowLog;
        @LogLevel
        private int mLogLevel;

        public Builder(@NonNull Application application) {
            this(application, BuildConfig.DEBUG);
        }

        public Builder(@NonNull Application application, boolean debug) {
            this.mApplication = application;
            this.mDebug = debug;
            this.mShowLog = debug;
        }

        @NonNull
        public Builder setShowLog(boolean showLog) {
            this.mShowLog = showLog;
            return this;
        }

        @NonNull
        public Builder setLogLevel(@LogLevel int logLevel) {
            this.mLogLevel = logLevel;
            return this;
        }

        @NonNull
        public UtilConfig build() {
            return new UtilConfig(this);
        }
    }


}
