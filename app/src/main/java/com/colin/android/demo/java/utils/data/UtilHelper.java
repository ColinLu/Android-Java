package com.colin.android.demo.java.utils.data;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * 作者： ColinLu
 * 时间： 2021-12-18 17:47
 * <p>
 * 描述： 工具类的辅助类，配置信息等
 */
public final class UtilHelper {
    private static UtilConfig sConfig;

    public static void init(UtilConfig config) {
        sConfig = config;
    }

    private static final class Holder {
        private static final UtilHelper instance = new UtilHelper();
    }

    public static UtilHelper getInstance() {
        return UtilHelper.Holder.instance;
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
