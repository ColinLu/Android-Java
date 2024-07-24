package com.colin.library.android.helper;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.Utils;
import com.colin.library.android.annotation.LogLevel;
import com.colin.library.android.utils.data.UtilConfig;

/**
 * 作者： ColinLu
 * 时间： 2021-12-18 17:47
 * <p>
 * 描述： 工具类的辅助类，配置信息等
 */
public final class UtilHelper {
    private UtilConfig mUtilConfig;
    private static volatile UtilHelper sHelper;

    private UtilHelper() {
    }

    public static UtilHelper getInstance() {
        if (sHelper == null) {
            synchronized (UtilHelper.class) {
                if (sHelper == null) sHelper = new UtilHelper();
            }
        }
        return sHelper;
    }

    public void init(@NonNull Application app) {
        init(new UtilConfig.Builder(app, true).build());
    }

    public void init(@NonNull UtilConfig config) {
        this.mUtilConfig = config;
        ActivityHelper.getInstance().register(config.getApplication());
    }

    @NonNull
    public UtilConfig getUtilConfig() {
        return Utils.notNull(mUtilConfig, "UtilHelper init first !");
    }

    public boolean showLog(@LogLevel final int level) {
        return level >= getLogLevel() && getUtilConfig().isShowLog();
    }

    public boolean isShowLogThread() {
        return getUtilConfig().isShowLogThread();
    }

    @LogLevel
    public int getLogLevel() {
        return getUtilConfig().getLogLevel();
    }

    @Nullable
    public String getLogTag() {
        return getUtilConfig().getLogTag();
    }

    public int getLogMethodOffset() {
        return getUtilConfig().getLogMethodOffset();
    }

    public int getLogMethodCount() {
        return getUtilConfig().getLogMethodCount();
    }


}
