package com.colin.library.android.http.def;

import com.colin.library.android.utils.data.Constants;

import java.io.IOException;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 20:26
 * <p>
 * 描述：网络请求异常定义
 */
public class HttpException extends IOException {
    public static final int CODE_HTTP_STATE = -2;
    public static final int CODE_HTTP_FAIL = -1;
    private final int mCode;
    private final String mUrl;


    public HttpException(int code, String url, String msg) {
        super(msg, null);
        this.mCode = code;
        this.mUrl = url;
    }

    public HttpException(int code, String url, Throwable throwable) {
        super(null, throwable);
        this.mCode = code;
        this.mUrl = url;
    }

    public HttpException(int code, String url, Exception exception) {
        super(exception.getMessage(), exception.getCause());
        this.mCode = code;
        this.mUrl = url;
    }

    @Override
    public String toString() {
        return "Code:" + mCode + Constants.LINE_SEP + "Url:" + mUrl + Constants.LINE_SEP +
                "Reason:" + super.toString();

    }
}
