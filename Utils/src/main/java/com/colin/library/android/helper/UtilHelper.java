package com.colin.library.android.helper;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.Utils;
import com.colin.library.android.annotation.LogLevel;
import com.colin.library.android.utils.ActivityUtil;
import com.colin.library.android.utils.BuildConfig;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.data.UtilConfig;

/**
 * 作者： ColinLu
 * 时间： 2021-12-18 17:47
 * <p>
 * 描述： 工具类的辅助类，配置信息等
 */
public final class UtilHelper {
    private static UtilConfig sConfig;
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

    public static void init(@NonNull Application app) {
        init(new UtilConfig.Builder(Utils.notNull(app), BuildConfig.DEBUG).build());
    }

    public static void init(@NonNull UtilConfig config) {
        sConfig = config;
        ActivityHelper.getInstance().register((Application) config.getApplication());
    }

    @NonNull
    public Application getContext() {
        Utils.notNull(sConfig, "UtilHelper init first !");
        return sConfig.getApplication();
    }

    public boolean showLog(@LogLevel final int level, @Nullable String showTag) {
        Utils.notNull(sConfig, "UtilHelper init first !");
        return level >= sConfig.getLogLevel() && sConfig.isShowLog(showTag);
    }

    @LogLevel
    public int getLogLevel() {
        Utils.notNull(sConfig, "UtilHelper init first !");
        return sConfig.getLogLevel();
    }

    public int getLogMethodOffset() {
        Utils.notNull(sConfig, "UtilHelper init first !");
        return sConfig.getLogMethodOffset();
    }

    public int getLogMethodCount() {
        Utils.notNull(sConfig, "UtilHelper init first !");
        return sConfig.getLogMethodCount();
    }

    public boolean isLogShowThread() {
        Utils.notNull(sConfig, "UtilHelper init first !");
        return sConfig.isLogShowThread();
    }
}
