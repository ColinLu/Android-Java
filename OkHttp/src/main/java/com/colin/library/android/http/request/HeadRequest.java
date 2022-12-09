package com.colin.library.android.http.request;

import androidx.annotation.NonNull;

import com.colin.library.android.http.annotation.Method;

/**
 * 作者： ColinLu
 * 时间： 2021-09-08 23:16
 * <p>
 * 描述： HEAD 类似于get请求，只不过返回的响应中没有具体的内容，用于获取报头
 */
public class HeadRequest extends BaseRequest<HeadRequest> {
    public HeadRequest(@NonNull String url) {
        super(url, Method.HEAD);
    }
}
