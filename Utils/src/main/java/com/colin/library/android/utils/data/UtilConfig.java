package com.colin.library.android.utils.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.LogLevel;
import com.colin.library.android.utils.BuildConfig;
import com.colin.library.android.utils.StringUtil;


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
    private final boolean mLogShowThread;
    @LogLevel
    private final int mLogLevel;
    /*show 指定tag log*/
    @Nullable
    private String mLogShowTag;
    private int mLogMethodOffset;
    private int mLogMethodCount;

    private UtilConfig(@NonNull Builder builder) {
        this.mApplication = builder.mApplication;
        this.mDebug = builder.mDebug;
        this.mShowLog = builder.mShowLog;
        this.mLogLevel = builder.mLogLevel;
        this.mLogShowTag = builder.mLogShowTag;
        this.mLogShowThread = builder.mLogShowThread;
        this.mLogMethodOffset = builder.mLogMethodOffset;
        this.mLogMethodCount = builder.mLogMethodCount;
    }


    @NonNull
    public Application getApplication() {
        return mApplication;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public boolean isShowLog(@Nullable String tag) {
        if (!mShowLog) return false;
        return StringUtil.isEmpty(mLogShowTag) || StringUtil.equals(mLogShowTag, tag);
    }

    public boolean isLogShowThread() {
        return mLogShowThread;
    }


    public int getLogMethodOffset() {
        return mLogMethodOffset;
    }

    public int getLogMethodCount() {
        return mLogMethodCount;
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
        private int mLogLevel = LogLevel.D;
        @Nullable
        private String mLogShowTag;
        private boolean mLogShowThread = true;
        private int mLogMethodOffset = 0;
        private int mLogMethodCount = 1;

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

        public Builder setLogShowTag(@Nullable String showTag) {
            this.mLogShowTag = showTag;
            return this;
        }

        public Builder setLogMethodOffset(int logMethodOffset) {
            this.mLogMethodOffset = logMethodOffset;
            return this;
        }

        public Builder setLogMethodCount(int logMethodCount) {
            this.mLogMethodCount = logMethodCount;
            return this;
        }

        public Builder setLogShowThread(boolean logShowThread) {
            this.mLogShowThread = logShowThread;
            return this;
        }

        @NonNull
        public UtilConfig build() {
            return new UtilConfig(this);
        }
    }


}
