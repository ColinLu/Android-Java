package com.colin.android.demo.java.utils.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.android.demo.java.BuildConfig;

/**
 * 作者： ColinLu
 * 时间： 2021-12-18 17:10
 * <p>
 * 描述： Util 配置
 */
public final class UtilConfig {
    private final Application mApplication;
    private final boolean mDebug;
    private final boolean mShowLog;
    private final int mLogLevel;

    private UtilConfig(@NonNull Builder builder) {
        this.mApplication = builder.mApplication;
        this.mDebug = builder.mDebug;
        this.mShowLog = builder.mShowLog;
        this.mLogLevel = builder.mLogLevel;
    }


    @NonNull
    public Context getApplication() {
        return mApplication;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public boolean isShowLog() {
        return mShowLog;
    }

    public int getLogLevel() {
        return mLogLevel;
    }

    public static class Builder {
        ///////////////////////////////////////////////////////////////////////////
        // 全局
        ///////////////////////////////////////////////////////////////////////////
        private final Application mApplication;
        private final boolean mDebug;
        ///////////////////////////////////////////////////////////////////////////
        // Log
        ///////////////////////////////////////////////////////////////////////////
        private boolean mShowLog;
        private int mLogLevel;

        public Builder(@NonNull Application application) {
            this(application, BuildConfig.DEBUG);
        }

        public Builder(@NonNull Application application, boolean debug) {
            this.mApplication = application;
            this.mDebug = debug;
            this.mShowLog = debug;
        }

        public Builder setShowLog(boolean showLog) {
            this.mShowLog = showLog;
            return this;
        }

        public Builder setLogLevel(int logLevel) {
            this.mLogLevel = logLevel;
            return this;
        }

        public UtilConfig build() {
            return new UtilConfig(this);
        }
    }


}
