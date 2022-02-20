package com.colin.library.android.utils.data;

import android.app.Application;
import android.content.Context;

import com.colin.library.android.utils.ActivityUtil;
import com.colin.library.android.utils.BuildConfig;

/**
 * 作者： ColinLu
 * 时间： 2021-12-18 17:47
 * <p>
 * 描述： 工具类的辅助类，配置信息等
 */
public final class UtilHelper {
    private static UtilConfig sConfig;

    private UtilHelper() {
    }

    private static final class Holder {
        private static final UtilHelper instance = new UtilHelper();
    }

    public static UtilHelper getInstance() {
        return Holder.instance;
    }

    public static void init(Application app) {
        init(new UtilConfig.Builder(app, BuildConfig.DEBUG).build());
    }

    public static void init(UtilConfig config) {
        sConfig = config;
        ActivityUtil.getInstance().register((Application) config.getApplication());
    }


    public Context getContext() {
        if (sConfig == null) throw new NullPointerException("UtilHelper init first !");
        return sConfig.getApplication();
    }

    public boolean showLog(int type) {
        if (sConfig == null) throw new NullPointerException("UtilHelper init first !");
        return sConfig.isShowLog() && type >= sConfig.getLogLevel();
    }

}
