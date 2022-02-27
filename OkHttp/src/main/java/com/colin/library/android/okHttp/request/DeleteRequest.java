package com.colin.library.android.okHttp.request;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.annotation.Method;

/**
 * 作者： ColinLu
 * 时间： 2021-09-08 23:16
 * <p>
 * 描述： DELETE 请求服务器删除Request-URL所标识的资源
 */
public class DeleteRequest extends BaseRequest<DeleteRequest> {
    public DeleteRequest(@NonNull String url) {
        super(url, Method.DELETE);
    }
}