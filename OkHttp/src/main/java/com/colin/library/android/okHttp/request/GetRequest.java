package com.colin.library.android.okHttp.request;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.annotation.Method;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:24
 * <p>
 * 描述： 请求指定的页面信息，并返回实体主体。
 */
public final class GetRequest extends BaseRequest<GetRequest> {
    public GetRequest(@NonNull String url) {
        super(url, Method.GET);
    }
}
