package com.colin.library.android.http.def;

import com.colin.library.android.annotation.Encode;

/**
 * 作者： ColinLu
 * 时间： 2022-12-13 12:53
 * <p>
 * 描述： 网络请求常量
 */
public interface Constants {
    int RETRY_CALL_DEFAULT = 3;
    long TIME_OUT_DEFAULT = 60L;
    /*Encode*/
    String ENCODE_DEFAULT = Encode.UTF_8;
    /*Head*/
    String HEAD_KEY_RESPONSE_CODE = "ResponseCode";
    String HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage";
    String HEAD_KEY_AUTHORIZATION = "Authorization";
    String HEAD_KEY_ACCEPT = "Accept";
    String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";
    String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";
    String HEAD_KEY_ACCEPT_RANGE = "Accept-Range";
    String HEAD_KEY_RANGE = "Range";
    String HEAD_KEY_CONTENT_DISPOSITION = "Content-Disposition";
    String HEAD_KEY_CONTENT_ENCODING = "Content-Encoding";
    String HEAD_KEY_CONTENT_LENGTH = "Content-Length";
    String HEAD_KEY_CONTENT_RANGE = "Content-Range";
    String HEAD_KEY_CONTENT_TYPE = "Content-Type";
    String HEAD_KEY_CACHE_CONTROL = "Cache-Control";
    String HEAD_KEY_CONNECTION = "Connection";
    String HEAD_KEY_DATE = "Date";
    String HEAD_KEY_EXPIRES = "Expires";
    String HEAD_KEY_E_TAG = "ETag";
    String HEAD_KEY_PRAGMA = "Pragma";
    String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";
    String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";
    String HEAD_KEY_LAST_MODIFIED = "Last-Modified";
    String HEAD_KEY_LOCATION = "Location";
    String HEAD_KEY_USER_AGENT = "User-Agent";
    String HEAD_KEY_COOKIE = "Cookie";
    String HEAD_KEY_COOKIE2 = "Cookie2";
    String HEAD_KEY_SET_COOKIE = "Set-Cookie";
    String HEAD_KEY_SET_COOKIE2 = "Set-Cookie2";
    String ACCEPT_ENCODING_ZIP_DEFLATE = "gzip, deflate";
    String CONNECTION_KEEP_ALIVE = "keep-alive";
    String CONNECTION_CLOSE = "close";
    /*ContentType*/
    String HEADER_ACCEPT_ALL = "application/json,application/xml,application/xhtml+xml,text/html;q=0.9,image/webp,*/*;q=0.8";
    String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    String CONTENT_TYPE_DEFAULT = "application/x-www-urlencoded";
    String CONTENT_TYPE_STREAM = "application/octet-stream";
    String CONTENT_TYPE_JSON = "application/json";
    String CONTENT_TYPE_XML = "application/xml";

}
