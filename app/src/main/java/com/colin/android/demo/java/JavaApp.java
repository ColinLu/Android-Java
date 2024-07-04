package com.colin.android.demo.java;

import android.app.Application;

import com.colin.library.android.annotation.LogLevel;
import com.colin.library.android.helper.CrashHelper;
import com.colin.library.android.helper.UtilHelper;
import com.colin.library.android.http.OkHttpHelper;
import com.colin.library.android.http.def.HttpConfig;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.data.UtilConfig;


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
        //初始化
        UtilHelper.getInstance().init(getUtilConfig());
        CrashHelper.getInstance().init((error, crashInfo) -> LogUtil.e(crashInfo));
        OkHttpHelper.getInstance().init(getHttpConfig());
        //        MediaHelper.getInstance().init(MediaConfig.newBuilder().build());
    }

    private UtilConfig getUtilConfig() {
        return UtilConfig.newBuilder(this)
                         .setShowLog(true)
                         .setLogLevel(LogLevel.V)
                         .setLogMethodCount(3)
                         .setLogTag("Colin")
                         .build();
    }

    private HttpConfig getHttpConfig() {
        return HttpConfig.newBuilder(this)
                         .setHeader("GlobalHeader", "GlobalHeaderValue")
                         .setParam("GlobalParam", "GlobalParamValue")
                         .build();
    }
}
