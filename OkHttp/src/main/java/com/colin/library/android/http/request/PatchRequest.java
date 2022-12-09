package com.colin.library.android.http.request;

import androidx.annotation.NonNull;

import com.colin.library.android.http.annotation.Method;

/**
 * 作者： ColinLu
 * 时间： 2021-09-08 23:16
 * <p>
 * 描述： PATCH 请求
 */
public class PatchRequest extends BaseRequestBody<PatchRequest> {
    public PatchRequest(@NonNull String url) {
        super(url, Method.PATCH);
    }
}
