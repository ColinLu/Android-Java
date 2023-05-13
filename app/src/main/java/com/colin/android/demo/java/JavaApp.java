package com.colin.android.demo.java;

import android.app.Application;

import com.colin.library.android.helper.CrashHelper;
import com.colin.library.android.helper.UtilHelper;
import com.colin.library.android.http.OkHttpHelper;
import com.colin.library.android.http.def.HttpConfig;
import com.colin.library.android.media.MediaHelper;
import com.colin.library.android.media.def.MediaConfig;
import com.colin.library.android.utils.LogUtil;


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
        UtilHelper.getInstance().init(this);
        CrashHelper.getInstance().init((error, crashInfo) -> LogUtil.e(crashInfo));
        OkHttpHelper.getInstance().init(getHttpConfig());
        MediaHelper.getInstance().init(MediaConfig.newBuilder().build());
    }

    private HttpConfig getHttpConfig() {
        return HttpConfig.newBuilder(this)
                .setHeader("GlobalHeader", "GlobalHeaderValue")
                .setHeader("全局头部", "全局头部值")
                .setParam("全局参数", "全局参数值")
                .setParam("GlobalParam", "GlobalParamValue").build();
    }
}
