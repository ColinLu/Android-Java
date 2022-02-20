package com.colin.library.android.okHttp.request;

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
    MediaType getMediaType(@NonNull String encode);

    @NonNull
    RequestBody getRequestBody(@NonNull String encode);
}
