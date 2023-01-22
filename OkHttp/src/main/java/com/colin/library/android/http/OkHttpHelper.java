package com.colin.library.android.http;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.Utils;
import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.bean.HttpConfig;
import com.colin.library.android.http.request.BodyRequest;
import com.colin.library.android.http.request.DeleteRequest;
import com.colin.library.android.http.request.GetRequest;
import com.colin.library.android.http.request.HeadRequest;
import com.colin.library.android.http.request.NoBodyRequest;
import com.colin.library.android.http.request.OptionsRequest;
import com.colin.library.android.http.request.PatchRequest;
import com.colin.library.android.http.request.PostRequest;
import com.colin.library.android.http.request.PutRequest;
import com.colin.library.android.http.request.TraceRequest;

import okhttp3.Call;
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

    @NonNull
    public HttpConfig getOkHttpConfig() {
        return Utils.notNull(mHttpConfig, "OkHttpConfig init first !");
    }

    @NonNull
    public GetRequest get(@NonNull String url) {
        return new GetRequest(url);
    }

    @NonNull
    public PostRequest post(@NonNull String url) {
        return new PostRequest(url);
    }

    @NonNull
    public DeleteRequest delete(@NonNull String url) {
        return new DeleteRequest(url);
    }

    @NonNull
    public HeadRequest head(@NonNull String url) {
        return new HeadRequest(url);
    }

    @NonNull
    public OptionsRequest options(@NonNull String url) {
        return new OptionsRequest(url);
    }

    @NonNull
    public PatchRequest patch(@NonNull String url) {
        return new PatchRequest(url);
    }

    @NonNull
    public PutRequest put(@NonNull String url) {
        return new PutRequest(url);
    }

    @NonNull
    public TraceRequest trace(@NonNull String url) {
        return new TraceRequest(url);
    }

    @NonNull
    public NoBodyRequest noBody(@Method String method, @NonNull String url) {
        return new NoBodyRequest(method, url);
    }

    @NonNull
    public BodyRequest body(@Method String method, @NonNull String url) {
        return new BodyRequest(method, url);
    }

    /**
     * 根据Tag取消请求
     */
    public void cancelTag(@Nullable Object tag) {
        cancelTag(OkHttpHelper.getInstance().getOkHttpConfig().getOkHttpClient(), tag);
    }

    /**
     * 根据Tag取消请求
     */
    public void cancelTag(@Nullable OkHttpClient client, @Nullable Object tag) {
        if (client == null || tag == null) return;
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) call.cancel();
        }
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        cancelAll(OkHttpHelper.getInstance().getOkHttpConfig().getOkHttpClient());
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll(@Nullable OkHttpClient client) {
        if (client == null) return;
        for (Call call : client.dispatcher().queuedCalls()) call.cancel();
        for (Call call : client.dispatcher().runningCalls()) call.cancel();
    }
}
