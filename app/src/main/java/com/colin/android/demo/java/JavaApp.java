package com.colin.android.demo.java;

import android.app.Application;

import com.colin.library.android.utils.CrashUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.data.UtilConfig;
import com.colin.library.android.utils.data.UtilHelper;


/**
 * 作者： ColinLu
 * 时间： 2021-12-18 17:14
 * <p>
 * 描述： TODO
 */
public final class JavaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UtilHelper.init(new UtilConfig.Builder(this, true).build());
        CrashUtil.init((crashInfo, e) -> LogUtil.e(crashInfo));
    }
}
