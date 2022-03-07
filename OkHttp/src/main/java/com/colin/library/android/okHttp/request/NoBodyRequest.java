package com.colin.library.android.okHttp.request;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.annotation.Method;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:24
 * <p>
 * 描述： 无请求体
 */
public final class NoBodyRequest extends BaseRequest<NoBodyRequest> {
    public NoBodyRequest(@Method String method, @NonNull String url) {
        super(url, method);
    }
}
