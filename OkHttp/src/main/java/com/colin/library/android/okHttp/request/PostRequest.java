package com.colin.library.android.okHttp.request;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.annotation.Method;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:24
 * <p>
 * 描述： TODO
 */
public class PostRequest extends BaseBodyRequest<PostRequest> {
    public PostRequest(@NonNull String url) {
        super(url, Method.POST);
    }
}
