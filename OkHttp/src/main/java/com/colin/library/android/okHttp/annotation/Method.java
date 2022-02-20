package com.colin.library.android.okHttp.annotation;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:26
 * <p>
 * 描述： 请求方法
 */

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({Method.GET, Method.HEAD, Method.DELETE, Method.OPTIONS,
        Method.POST, Method.PUT, Method.PATCH, Method.REPORT, Method.TRACE})
@Retention(RetentionPolicy.SOURCE)
public @interface Method {
    String GET = "GET";
    String HEAD = "HEAD";
    String DELETE = "DELETE";
    String OPTIONS = "OPTIONS";
    String POST = "POST";
    String PUT = "PUT";
    String PATCH = "PATCH";
    String REPORT = "REPORT";
    String TRACE = "TRACE";
}