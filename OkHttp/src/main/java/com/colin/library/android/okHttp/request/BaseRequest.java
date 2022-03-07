package com.colin.library.android.okHttp.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.OkHttp;
import com.colin.library.android.okHttp.annotation.Method;
import com.colin.library.android.okHttp.bean.HttpConfig;
import com.colin.library.android.okHttp.bean.HttpHeaders;
import com.colin.library.android.okHttp.bean.HttpParams;
import com.colin.library.android.okHttp.callback.IHttpCallback;
import com.colin.library.android.okHttp.policy.HttpPolicy;
import com.colin.library.android.okHttp.progress.IProgress;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:25
 * <p>
 * 描述： 基本请求，不含请求体
 */
public class BaseRequest<Returner> implements IRequest<Returner> {
    @NonNull
    protected transient final String mUrl;
    @Method
    @NonNull
    protected transient final String mMethod;
    protected transient OkHttpClient mOkHttpClient;
    protected transient Object mTag;
    protected transient int mRetryCall;
    protected transient String mEncode;
    protected transient long mReadTimeout;
    protected transient long mWriteTimeout;
    protected transient long mConnectTimeout;
    protected transient HttpHeaders.Builder mHeader;
    protected transient HttpParams.Builder mParams;

    public BaseRequest(@NonNull String url, @Method @NonNull String method) {
        final HttpConfig config = OkHttp.getInstance().getOkHttpConfig();
        this.mUrl = url;
        this.mMethod = method;
        this.mOkHttpClient = config.getOkHttpClient();
        this.mEncode = config.getEncode();
        this.mReadTimeout = config.getReadTimeout();
        this.mWriteTimeout = config.getWriteTimeout();
        this.mConnectTimeout = config.getConnectTimeout();
        this.mRetryCall = config.getRetryCall();
        this.mHeader = config.getHeaderBuilder();
        this.mParams = config.getParamsBuilder();
        this.mHeader.set(OkHttp.HEAD_KEY_ACCEPT, OkHttp.HEADER_ACCEPT_ALL);
        this.mHeader.set(OkHttp.HEAD_KEY_ACCEPT_ENCODING, OkHttp.ACCEPT_ENCODING_ZIP_DEFLATE);
        this.mHeader.set(OkHttp.HEAD_KEY_ACCEPT_LANGUAGE, config.getAcceptLanguage());
        this.mHeader.set(OkHttp.HEAD_KEY_USER_AGENT, config.getUserAgent());
    }

    @NonNull
    @Override
    public String getUrl() {
        return HttpUtil.getUrl(mUrl, mParams.build().toString(getEncode()));
    }

    @NonNull
    @Method
    @Override
    public String getMethod() {
        return mMethod;
    }

    @NonNull
    @Override
    public Headers getHeaders() {
        return mHeader.build().getHeader();
    }

    @NonNull
    @Override
    public String getContentType() {
        final String contentType = mHeader.build().get(OkHttp.HEAD_KEY_CONTENT_TYPE);
        return StringUtil.isEmpty(contentType) ? OkHttp.CONTENT_TYPE_DEFAULT : contentType;
    }

    @Nullable
    @Override
    public RequestBody getRequestBody() {
        return null;
    }

    @NonNull
    @Override
    public Request getRequest() {
        return new Request.Builder()
                .tag(mTag)
                .url(getUrl())
                .method(getMethod(), getRequestBody())
                .headers(getHeaders())
                .build();
    }

    @NonNull
    @Override
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient.newBuilder()
                .readTimeout(mReadTimeout, TimeUnit.SECONDS)
                .writeTimeout(mWriteTimeout, TimeUnit.SECONDS)
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public int getRetryCall() {
        return mRetryCall;
    }

    @NonNull
    @Override
    public String getEncode() {
        return mEncode;
    }

    @NonNull
    @Override
    public Returner setOkHttpClient(@NonNull OkHttpClient client) {
        this.mOkHttpClient = client;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setTag(@Nullable Object tag) {
        this.mTag = tag;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setReadTimeout(long timeOut) {
        this.mReadTimeout = timeOut;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setWriteTimeout(long timeOut) {
        this.mWriteTimeout = timeOut;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setConnectTimeout(long timeOut) {
        this.mConnectTimeout = timeOut;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setRetryCall(int retryCall) {
        this.mRetryCall = retryCall;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setEncode(@NonNull String encode) {
        this.mEncode = encode;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setHeader(@NonNull String name, @NonNull String value) {
        this.mHeader.set(name, value);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setHeader(@NonNull Map<String, String> map) {
        this.mHeader.set(map);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner addHeader(@NonNull String name, @NonNull String value) {
        this.mHeader.add(name, value);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner addHeader(@NonNull Map<String, String> map) {
        this.mHeader.add(map);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner removeHeader(@NonNull String name) {
        this.mHeader.remove(name);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner removeHeaderAll() {
        this.mHeader.removeAll();
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setParam(@NonNull String key, @Nullable String value) {
        this.mParams.set(key, value);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setParam(@NonNull String key, @Nullable List<String> list) {
        this.mParams.set(key, list);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner setParam(@NonNull Map<String, String> map) {
        this.mParams.set(map);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner addParam(@NonNull String key, @Nullable String value) {
        this.mParams.add(key, value);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner addParam(@NonNull String key, @Nullable List<String> list) {
        this.mParams.add(key, list);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner addParam(@NonNull Map<String, String> map) {
        this.mParams.add(map);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner removeParam(@NonNull String key) {
        this.mParams.remove(key);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner removeParamAll() {
        this.mParams.removeAll();
        return (Returner) this;
    }

    @Nullable
    @Override
    public Response execute() {
        return new HttpPolicy<Response>(getOkHttpClient(), getRequest(), getRetryCall()).execute();
    }

    @Override
    public <Result> void execute(@NonNull IHttpCallback<Result> callback) {
        new HttpPolicy<Result>(getOkHttpClient(), getRequest(), getRetryCall()).execute(callback);
    }
}
