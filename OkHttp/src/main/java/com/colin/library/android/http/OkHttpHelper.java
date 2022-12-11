package com.colin.library.android.http;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.Utils;
import com.colin.library.android.http.bean.HttpConfig;

import okhttp3.OkHttpClient;

/**
 * 作者： ColinLu
 * 时间： 2022-12-11 09:49
 * <p>
 * 描述： OkHttp辅助类
 */
public final class OkHttpHelper {
    private static volatile OkHttpHelper sHelper;
    private HttpConfig mHttpConfig;

    private OkHttpHelper() {
    }

    public static OkHttpHelper getInstance() {
        if (sHelper == null) {
            synchronized (OkHttpHelper.class) {
                if (sHelper == null) sHelper = new OkHttpHelper();
            }
        }
        return sHelper;
    }

    public void init(@NonNull Application application) {
        init(HttpConfig.newBuilder(application).build());
    }

    public void init(@NonNull HttpConfig config) {
        this.mHttpConfig = config;
    }

    public HttpConfig getOkHttpConfig() {
        Utils.notNull(mHttpConfig, "OkHttpConfig init first !");
        return mHttpConfig;
    }

    public Context getContext() {
        Utils.notNull(mHttpConfig, "OkHttpConfig init first !");
        return mHttpConfig.getContext();
    }

    public OkHttpClient getOkHttpClient() {
        Utils.notNull(mHttpConfig, "OkHttpConfig init first !");
        return mHttpConfig.getOkHttpClient();
    }

}
