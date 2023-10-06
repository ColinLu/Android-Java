package com.colin.library.android.http.request.base;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.OkHttpHelper;
import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.def.Constants;
import com.colin.library.android.http.def.HttpConfig;
import com.colin.library.android.http.def.HttpHeaders;
import com.colin.library.android.http.def.HttpParams;
import com.colin.library.android.http.action.IAction;
import com.colin.library.android.http.progress.IProgress;
import com.colin.library.android.http.def.IExecute;
import com.colin.library.android.http.policy.HttpPolicy;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.StringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:25
 * <p>
 * 描述： 基本请求，不含请求体
 */
public class BaseRequest<Returner> implements IRequest<Returner>, IExecute {
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
    protected transient IExecute mExecute;

    public BaseRequest(@NonNull String url, @Method @NonNull String method) {
        this(url, method, OkHttpHelper.getInstance().getOkHttpConfig());
    }

    private BaseRequest(@NonNull String url, @Method @NonNull String method, @NonNull HttpConfig config) {
        this.mUrl = url;
        this.mMethod = method;
        this.mOkHttpClient = config.getOkHttpClient();
        this.mEncode = config.getEncode();
        this.mReadTimeout = config.getReadTimeout();
        this.mWriteTimeout = config.getWriteTimeout();
        this.mConnectTimeout = config.getConnectTimeout();
        this.mRetryCall = config.getRetryCall();
        this.mParams = config.getParamsBuilder();
        this.mHeader = config.getHeaderBuilder();
        this.mHeader.set(Constants.HEAD_KEY_ACCEPT_LANGUAGE, config.getAcceptLanguage());
        this.mHeader.set(Constants.HEAD_KEY_USER_AGENT, config.getUserAgent());
        this.mHeader.set(Constants.HEAD_KEY_ACCEPT, Constants.HEADER_ACCEPT_ALL);
        this.mHeader.set(Constants.HEAD_KEY_ACCEPT_ENCODING, Constants.ACCEPT_ENCODING_ZIP_DEFLATE);
    }

    @NonNull
    @Override
    public Returner tag(@Nullable Object tag) {
        this.mTag = tag;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner encode(@NonNull String encode) {
        this.mEncode = encode;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner retryCall(int retryCall) {
        this.mRetryCall = retryCall;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner read(@IntRange(from = 0) long timeOut) {
        this.mReadTimeout = timeOut;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner write(@IntRange(from = 0) long timeOut) {
        this.mWriteTimeout = timeOut;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner connect(@IntRange(from = 0) long timeOut) {
        this.mConnectTimeout = timeOut;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner okHttpClient(@NonNull OkHttpClient client) {
        this.mOkHttpClient = client;
        return (Returner) this;
    }


    @NonNull
    @Override
    public Returner header(@NonNull String name, @NonNull String value) {
        return header(name, value, false);
    }

    @NonNull
    @Override
    public Returner header(@NonNull Map<String, String> map) {
        return header(map, false);
    }

    @NonNull
    @Override
    public Returner header(@NonNull String name, @NonNull String value, boolean replace) {
        this.mHeader.set(name, value, replace);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner header(@NonNull Map<String, String> map, boolean replace) {
        this.mHeader.set(map, replace);
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
    public Returner param(@NonNull String key, @Nullable String value) {
        return param(key, value, false);
    }

    @NonNull
    @Override
    public Returner param(@NonNull String key, @Nullable List<String> list) {
        return param(key, list, false);

    }

    @NonNull
    @Override
    public Returner param(@NonNull Map<String, String> map) {
        return param(map, false);
    }

    @NonNull
    @Override
    public Returner param(@NonNull String key, @Nullable String value, boolean replace) {
        this.mParams.set(key, value, replace);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner param(@NonNull String key, @Nullable List<String> list, boolean replace) {
        this.mParams.set(key, list, replace);
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner param(@NonNull Map<String, String> map, boolean replace) {
        this.mParams.set(map, replace);
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
    public String getEncode() {
        return mEncode;
    }


    @Override
    public int getRetryCall() {
        return mRetryCall;
    }

    @NonNull
    @Override
    public String getUrl() {
        return HttpUtil.getUrl(mUrl, mParams.build().toString(getEncode()));
    }

    @Nullable
    @Override
    public Object getTag() {
        return mTag;
    }

    @Method
    @NonNull
    @Override
    public String getMethod() {
        return mMethod;
    }

    @NonNull
    @Override
    public Headers getHeaders() {
        return mHeader.build().getHeader(getEncode());
    }

    @NonNull
    @Override
    public String getContentType() {
        final String contentType = mHeader.build().get(Constants.HEAD_KEY_CONTENT_TYPE);
        return StringUtil.isEmpty(contentType) ? Constants.CONTENT_TYPE_DEFAULT : contentType;
    }

    @NonNull
    @Override
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient.newBuilder().readTimeout(mReadTimeout, TimeUnit.SECONDS).writeTimeout(mWriteTimeout, TimeUnit.SECONDS)
                            .connectTimeout(mConnectTimeout, TimeUnit.SECONDS).build();
    }

    @NonNull
    @Override
    public Request toRequest(@Nullable IProgress progress) {
        return new Request.Builder().tag(getTag()).url(getUrl()).method(getMethod(), getRequestBody(progress)).headers(getHeaders()).build();

    }

    @Nullable
    @Override
    public Response execute() {
        mExecute = new HttpPolicy(getOkHttpClient(), toRequest(null), getRetryCall());
        return mExecute.execute();
    }

    @Override
    public <Result> void execute(@NonNull IAction<Result> action) {
        mExecute = new HttpPolicy(getOkHttpClient(), toRequest(action), getRetryCall());
        mExecute.execute(action);
    }

    @Override
    public boolean isExecuted() {
        return mExecute != null && mExecute.isExecuted();
    }
}
