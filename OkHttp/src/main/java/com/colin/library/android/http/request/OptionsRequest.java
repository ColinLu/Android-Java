package com.colin.library.android.http.request;

import androidx.annotation.NonNull;

import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.request.base.BaseRequest;

/**
 * 作者： ColinLu
 * 时间： 2021-09-08 23:16
 * <p>
 * 描述： OPTIONS 返回服务器针对特定资源所支持的HTTP请求方法，也可以利用向web服务器发送‘*’的请求来测试服务器的功能性
 */
public class OptionsRequest extends BaseRequest<OptionsRequest> {
    public OptionsRequest(@NonNull String url) {
        super(url, Method.OPTIONS);
    }
}
