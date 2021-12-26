package com.colin.android.demo.java;

import android.app.Application;

import com.colin.android.demo.java.utils.data.UtilConfig;
import com.colin.android.demo.java.utils.data.UtilHelper;

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
        UtilHelper.init(new UtilConfig.Builder(this).build());
    }
}
