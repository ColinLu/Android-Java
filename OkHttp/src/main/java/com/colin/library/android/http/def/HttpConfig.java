package com.colin.library.android.http.def;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.colin.library.android.http.interceptor.LogInterceptor;
import com.colin.library.android.utils.HttpUtil;

import java.util.Map;

import okhttp3.OkHttpClient;


/**
 * 作者： ColinLu
 * 时间： 2021-09-09 00:02
 * <p>
 * 描述： 全局配置
 */
public final class HttpConfig {
    private final Application mApplication;             //上下文
    private final OkHttpClient mOkHttpClient;           //统一请求客户端
    private final String mEncode;                       //URL 加密方式
    private final long mReadTimeout;                    //读取时间
    private final long mWriteTimeout;                   //写入时间
    private final long mConnectTimeout;                 //连接时间
    private final int mRetryCall;                       //失败重新连接次数
    private final HttpHeaders.Builder mHeaderBuilder;   //全局 头部
    private final HttpParams.Builder mParamsBuilder;    //全局请求参数
    private String mUserAgent;
    private String mAcceptLanguageInstance;

    public HttpConfig(@NonNull Builder builder) {
        this.mApplication = builder.mApplication;
        this.mOkHttpClient = builder.mOkHttpClient;
        this.mEncode = builder.mEncode;
        this.mReadTimeout = builder.mReadTimeout;
        this.mWriteTimeout = builder.mWriteTimeout;
        this.mConnectTimeout = builder.mConnectTimeout;
        this.mRetryCall = builder.mRetryCall;
        this.mHeaderBuilder = builder.mHeaderBuilder;
        this.mParamsBuilder = builder.mParamsBuilder;
    }

    public static HttpConfig.Builder newBuilder(@NonNull Application application) {
        return new Builder(application);
    }


    public Application getContext() {
        return mApplication;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public String getEncode() {
        return mEncode;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public int getRetryCall() {
        return mRetryCall;
    }

    public HttpHeaders.Builder getHeaderBuilder() {
        if (mHeaderBuilder != null) mHeaderBuilder.build().newBuilder();
        return new HttpHeaders.Builder();
    }

    public HttpParams.Builder getParamsBuilder() {
        if (mParamsBuilder != null) mParamsBuilder.build().newBuilder();
        return new HttpParams.Builder();
    }


    /**
     * Accept-Language: zh-CN,zh;q=0.8
     */
    public String getAcceptLanguage() {
        if (!TextUtils.isEmpty(mAcceptLanguageInstance)) return mAcceptLanguageInstance;
        mAcceptLanguageInstance = HttpUtil.getAcceptLanguage();
        return mAcceptLanguageInstance;
    }

    /**
     * User-Agent: Mozilla/5.0 (Linux; U; Android 5.0.2; zh-cn;
     * Redmi Note 3 Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Mobile Safari/537.36
     */
    @NonNull
    public String getUserAgent() {
        if (!TextUtils.isEmpty(mUserAgent)) return mUserAgent;
        mUserAgent = HttpUtil.getUserAgent();
        return mUserAgent;
    }


    public final static class Builder {
        @NonNull
        private final Application mApplication;
        private OkHttpClient mOkHttpClient;
        private String mEncode;
        private long mReadTimeout = Constants.TIME_OUT_DEFAULT;
        private long mWriteTimeout = Constants.TIME_OUT_DEFAULT;
        private long mConnectTimeout = Constants.TIME_OUT_DEFAULT;
        private int mRetryCall = Constants.RETRY_CALL_DEFAULT;
        private HttpHeaders.Builder mHeaderBuilder;
        private HttpParams.Builder mParamsBuilder;

        public Builder(@NonNull Application application) {
            this.mApplication = application;
            this.mOkHttpClient = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();
            this.mHeaderBuilder = new HttpHeaders.Builder();
            this.mParamsBuilder = new HttpParams.Builder();
        }

        public Builder setOkHttpClient(@NonNull OkHttpClient okHttpClient) {
            this.mOkHttpClient = okHttpClient;
            return this;
        }

        public Builder setReadTimeout(@IntRange(from = 0) long readTimeOut) {
            this.mReadTimeout = readTimeOut;
            return this;
        }

        public Builder setWriteTimeout(@IntRange(from = 0) long writeTimeOut) {
            this.mWriteTimeout = writeTimeOut;
            return this;
        }

        public Builder setConnectTimeout(@IntRange(from = 0) long connectTimeOut) {
            this.mConnectTimeout = connectTimeOut;
            return this;
        }

        public Builder setHeader(@NonNull String name, @NonNull String value) {
            this.mHeaderBuilder.set(name, value);
            return this;
        }

        public Builder addHeader(@NonNull String name, @NonNull String value) {
            this.mHeaderBuilder.add(name, value);
            return this;
        }

        public Builder setHeader(@NonNull @Size(min = 1) Map<String, String> map) {
            this.mHeaderBuilder.set(map);
            return this;
        }

        public Builder addHeader(@NonNull @Size(min = 1) Map<String, String> map) {
            this.mHeaderBuilder.add(map);
            return this;
        }

        public Builder setParam(@NonNull String key, @Nullable String value) {
            this.mParamsBuilder.set(key, value);
            return this;
        }

        public Builder addParam(@NonNull String key, @Nullable String value) {
            this.mParamsBuilder.add(key, value);
            return this;
        }

        public Builder setParam(@NonNull @Size(min = 1) Map<String, String> map) {
            this.mParamsBuilder.set(map);
            return this;
        }

        public Builder addParam(@NonNull @Size(min = 1) Map<String, String> map) {
            this.mParamsBuilder.add(map);
            return this;
        }

        public Builder setRetryCall(int retryCall) {
            this.mRetryCall = retryCall;
            return this;
        }

        public Builder setEncode(@Nullable String encode) {
            this.mEncode = encode;
            return this;
        }


        public HttpConfig build() {
            return new HttpConfig(this);
        }

    }
}
