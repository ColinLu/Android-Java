package com.colin.library.android.http.request;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.callback.IHttpCallback;
import com.colin.library.android.http.progress.IProgress;

import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:40
 * <p>
 * 描述： TODO
 */
public interface IRequest<Returner> {

    @NonNull
    String getUrl();

    @NonNull
    @Method
    String getMethod();

    @NonNull
    OkHttpClient getOkHttpClient();

    @NonNull
    Request getRequest(@Nullable IProgress progress);

    @Nullable
    RequestBody getRequestBody(@Nullable IProgress progress);

    @NonNull
    String getEncode();

    @NonNull
    Headers getHeaders();

    @NonNull
    String getContentType();


    int getRetryCall();

    @NonNull
    Returner setOkHttpClient(@NonNull OkHttpClient client);

    @NonNull
    Returner setTag(@NonNull Object tag);

    @NonNull
    Returner setReadTimeout(@IntRange(from = 0) long timeOut);

    @NonNull
    Returner setWriteTimeout(@IntRange(from = 0) long timeOut);

    @NonNull
    Returner setConnectTimeout(@IntRange(from = 0) long timeOut);

    @NonNull
    Returner setRetryCall(int retryCall);

    @NonNull
    Returner setEncode(@NonNull String encode);

    @NonNull
    Returner setHeader(@NonNull String name, @NonNull String value);

    @NonNull
    Returner setHeader(@NonNull Map<String, String> map);

    @NonNull
    Returner addHeader(@NonNull String name, @NonNull String value);

    @NonNull
    Returner addHeader(@NonNull Map<String, String> map);

    @NonNull
    Returner removeHeader(@NonNull String name);

    @NonNull
    Returner removeHeaderAll();

    @NonNull
    Returner setParam(@NonNull String key, @Nullable String value);

    @NonNull
    Returner setParam(@NonNull String key, @Nullable List<String> list);

    @NonNull
    Returner setParam(@NonNull Map<String, String> map);

    @NonNull
    Returner addParam(@NonNull String key, @Nullable String value);

    @NonNull
    Returner addParam(@NonNull String key, @Nullable List<String> list);

    @NonNull
    Returner addParam(@NonNull Map<String, String> map);


    @NonNull
    Returner removeParam(@NonNull String key);

    @NonNull
    Returner removeParamAll();

    @Nullable
    @WorkerThread
    Response execute();

    <Result> void execute(@NonNull IHttpCallback<Result> callback);


}
