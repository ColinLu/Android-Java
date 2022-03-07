package com.colin.library.android.okHttp.cookie;


import androidx.annotation.NonNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 作者： ColinLu
 * 时间： 2020-12-07 19:16
 * <p>
 * 描述： Cookie 读写操作
 */
public interface CookieStore {

    /*保存 指定cookies集合*/
    void save(@NonNull HttpUrl url, @NonNull List<Cookie> cookies);

    /*保存 指定cookie*/
    void save(@NonNull HttpUrl url, @NonNull Cookie cookie);

    /*加载指定有效cookie*/
    @NonNull
    List<Cookie> load(@NonNull HttpUrl url);

    /*获取所有有效cookie*/
    @NonNull
    List<Cookie> loadAll();

    /*获取Cookie 的设置Key*/
    @NonNull
    String getCookieKey(@NonNull Cookie cookie);

    /* 当前cookie是否过期 */
    boolean isCookieExpired(@NonNull Cookie cookie);

    /*移除指定 cookie*/
    void remove(@NonNull HttpUrl url, @NonNull Cookie cookie);

    /*移除所有cookie*/
    void removeAll();

}
