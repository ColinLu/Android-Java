package com.colin.library.android.http.request.base;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.progress.IProgress;

import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:40
 * <p>
 * 描述： 基础Request
 */
public interface IRequest<Returner> {
    @NonNull
    Returner tag(@NonNull Object tag);

    @NonNull
    Returner encode(@NonNull String encode);

    @NonNull
    Returner retryCall(int retryCall);

    @NonNull
    Returner read(@IntRange(from = 0) long timeOut);

    @NonNull
    Returner write(@IntRange(from = 0) long timeOut);

    @NonNull
    Returner connect(@IntRange(from = 0) long timeOut);

    @NonNull
    Returner okHttpClient(@NonNull OkHttpClient client);

    @NonNull
    Returner header(@NonNull String name, @NonNull String value);

    @NonNull
    Returner header(@NonNull Map<String, String> map);

    @NonNull
    Returner header(@NonNull String name, @NonNull String value, boolean replace);

    @NonNull
    Returner header(@NonNull Map<String, String> map, boolean replace);

    @NonNull
    Returner removeHeader(@NonNull String name);

    @NonNull
    Returner removeHeaderAll();

    @NonNull
    Returner param(@NonNull String key, @Nullable String value);

    @NonNull
    Returner param(@NonNull String key, @Nullable List<String> list);

    @NonNull
    Returner param(@NonNull Map<String, String> map);

    @NonNull
    Returner param(@NonNull String key, @Nullable String value, boolean replace);

    @NonNull
    Returner param(@NonNull String key, @Nullable List<String> list, boolean replace);

    @NonNull
    Returner param(@NonNull Map<String, String> map, boolean replace);


    @NonNull
    Returner removeParam(@NonNull String key);

    @NonNull
    Returner removeParamAll();

    @Nullable
    String getEncode();

    int getRetryCall();

    @NonNull
    String getUrl();

    @Nullable
    default Object getTag() {
        return null;
    }

    @NonNull
    Headers getHeaders();

    @NonNull
    String getContentType();

    @NonNull
    @Method
    String getMethod();

    @Nullable
    default RequestBody getRequestBody(@Nullable IProgress progress) {
        return null;
    }

    @NonNull
    OkHttpClient getOkHttpClient();

    @NonNull
    Request toRequest(@Nullable IProgress progress);
}
