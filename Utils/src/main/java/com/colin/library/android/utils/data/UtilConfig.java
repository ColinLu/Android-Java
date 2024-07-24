package com.colin.library.android.utils.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.LogLevel;


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
    private final boolean mShowLog;
    private final boolean mShowLogThread;
    @LogLevel
    private final int mLogLevel;
    /*show 指定tag log*/
    @Nullable
    private final String mLogTag;
    private final int mLogMethodOffset;
    private final int mLogMethodCount;

    public static UtilConfig.Builder newBuilder(@NonNull Application application) {
        return new Builder(application);
    }

    private UtilConfig(@NonNull Builder builder) {
        this.mApplication = builder.mApplication;
        this.mDebug = builder.mDebug;
        this.mShowLog = builder.mShowLog;
        this.mShowLogThread = builder.mShowLogThread;
        this.mLogLevel = builder.mLogLevel;
        this.mLogTag = builder.mLogTag;
        this.mLogMethodCount = builder.mLogMethodCount;
        this.mLogMethodOffset = builder.mLogMethodOffset;
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

    public boolean isShowLogThread() {
        return mShowLogThread;
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

    @Nullable
    public String getLogTag() {
        return mLogTag;
    }

    public static class Builder {
        /*全局上下文*/
        private final Application mApplication;
        /*环境*/
        private final boolean mDebug;
        /*Log*/
        private boolean mShowLog = true;
        private boolean mShowLogThread = true;
        @LogLevel
        private int mLogLevel = LogLevel.I;
        @Nullable
        private String mLogTag;
        private int mLogMethodOffset = 0;
        private int mLogMethodCount = 3;

        public Builder(@NonNull Application application) {
            this(application, true);
        }

        public Builder(@NonNull Application application, boolean debug) {
            this.mApplication = application;
            this.mDebug = debug;
            this.mShowLog = debug;
        }

        @NonNull
        public Builder setShowLog(boolean show) {
            this.mShowLog = show;
            return this;
        }

        @NonNull
        public Builder setShowLogThread(boolean show) {
            this.mShowLogThread = show;
            return this;
        }

        @NonNull
        public Builder setLogLevel(@LogLevel int level) {
            this.mLogLevel = level;
            return this;
        }

        @NonNull
        public Builder setLogTag(@Nullable String tag) {
            this.mLogTag = tag;
            return this;
        }

        @NonNull
        public Builder setLogMethodOffset(int offset) {
            this.mLogMethodOffset = offset;
            return this;
        }

        @NonNull
        public Builder setLogMethodCount(int count) {
            this.mLogMethodCount = count;
            return this;
        }


        @NonNull
        public UtilConfig build() {
            return new UtilConfig(this);
        }
    }


}
