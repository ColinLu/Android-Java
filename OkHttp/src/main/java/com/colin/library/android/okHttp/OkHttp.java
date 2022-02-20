package com.colin.library.android.okHttp;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.bean.HttpConfig;
import com.colin.library.android.okHttp.request.GetRequest;
import com.colin.library.android.utils.data.Constants;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:21
 * <p>
 * 描述： TODO
 */
public final class OkHttp {
    public static final int RETRY_CALL_DEFAULT = 3;
    public static final long TIME_OUT_DEFAULT = 60L;
    /*Encode*/
    public static final String ENCODE_DEFAULT = Constants.UTF_8;
    /*Head*/
    public static final String HEAD_KEY_RESPONSE_CODE = "ResponseCode";
    public static final String HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage";
    public static final String HEAD_KEY_AUTHORIZATION = "Authorization";
    public static final String HEAD_KEY_ACCEPT = "Accept";
    public static final String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String HEAD_KEY_ACCEPT_RANGE = "Accept-Range";
    public static final String HEAD_KEY_RANGE = "Range";
    public static final String HEAD_KEY_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEAD_KEY_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";
    public static final String HEAD_KEY_CONTENT_RANGE = "Content-Range";
    public static final String HEAD_KEY_CONTENT_TYPE = "Content-Type";
    public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";
    public static final String HEAD_KEY_CONNECTION = "Connection";
    public static final String HEAD_KEY_DATE = "Date";
    public static final String HEAD_KEY_EXPIRES = "Expires";
    public static final String HEAD_KEY_E_TAG = "ETag";
    public static final String HEAD_KEY_PRAGMA = "Pragma";
    public static final String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";
    public static final String HEAD_KEY_LAST_MODIFIED = "Last-Modified";
    public static final String HEAD_KEY_LOCATION = "Location";
    public static final String HEAD_KEY_USER_AGENT = "User-Agent";
    public static final String HEAD_KEY_COOKIE = "Cookie";
    public static final String HEAD_KEY_COOKIE2 = "Cookie2";
    public static final String HEAD_KEY_SET_COOKIE = "Set-Cookie";
    public static final String HEAD_KEY_SET_COOKIE2 = "Set-Cookie2";
    public static final String ACCEPT_ENCODING_ZIP_DEFLATE = "gzip, deflate";
    public static final String CONNECTION_KEEP_ALIVE = "keep-alive";
    public static final String CONNECTION_CLOSE = "close";
    /*ContentType*/
    public static final String HEADER_ACCEPT_ALL = "application/json,application/xml,application/xhtml+xml,text/html;q=0.9,image/webp,*/*;q=0.8";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    public static final String CONTENT_TYPE_DEFAULT = "application/x-www-urlencoded";
    public static final String CONTENT_TYPE_STREAM = "application/octet-stream";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    private HttpConfig mHttpConfig;

    private OkHttp() {
    }


    private static class Holder {
        private static final OkHttp instance = new OkHttp();
    }

    public static OkHttp getInstance() {
        return OkHttp.Holder.instance;
    }

    public HttpConfig getOkHttpConfig() {
        if (null == mHttpConfig) throw new NullPointerException("OkHttpConfig init first !");
        return mHttpConfig;
    }

    public Context getContext() {
        if (null == mHttpConfig) throw new NullPointerException("OkHttpConfig init first !");
        return mHttpConfig.getContext();
    }

    public GetRequest get(@NonNull String url) {
        return new GetRequest(url);
    }

}
