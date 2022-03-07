package com.colin.library.android.okHttp.cookie;


import androidx.annotation.NonNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {

    public static CookieJarImpl create(@NonNull CookieStore store) {
        return new CookieJarImpl(store);
    }

    private final CookieStore cookieStore;

    public CookieJarImpl(@NonNull CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        cookieStore.save(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        return cookieStore.load(url);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
