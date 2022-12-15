package com.colin.library.android.http.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2022-02-20 22:38
 * <p>
 * 描述： TODO
 */
public interface IRequestBody {
    @Nullable
    MediaType getMediaType(@Nullable String charset);

    @NonNull
    RequestBody getRequestBody(@Nullable String charset);
}
