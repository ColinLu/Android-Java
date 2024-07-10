package com.colin.library.android.http.request;

import androidx.annotation.NonNull;

import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.request.base.BaseRequestBody;

/**
 * 作者： ColinLu
 * 时间： 2021-09-08 23:16
 * <p>
 * 描述： PUT 向指定资源位置上传其最新内容
 */
public class PutRequest extends BaseRequestBody<PutRequest> {
    public PutRequest(@NonNull String url) {
        super(url, Method.PUT);
    }
}
