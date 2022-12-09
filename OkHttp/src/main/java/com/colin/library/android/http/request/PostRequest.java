package com.colin.library.android.http.request;

import androidx.annotation.NonNull;

import com.colin.library.android.http.annotation.Method;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:24
 * <p>
 * 描述： POST 向指定资源提交数据进行处理请求（例如提交表单或者上传文件）。
 * 数据被包含在请求体中。POST请求可能会导致新的资源的建立和/或已有资源的修改。
 */
public class PostRequest extends BaseRequestBody<PostRequest> {
    public PostRequest(@NonNull String url) {
        super(url, Method.POST);
    }
}
