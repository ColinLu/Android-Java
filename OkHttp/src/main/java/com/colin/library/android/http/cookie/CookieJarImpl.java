package com.colin.library.android.http.cookie;


import androidx.annotation.NonNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {

    public static CookieJarImpl create(@NonNull CookieStore store) {
        return new CookieJarImpl(store);
    }

    private final CookieStore mCookieStore;

    public CookieJarImpl(@NonNull CookieStore cookieStore) {
        this.mCookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        mCookieStore.save(url, cookies);
    }

    @NonNull
    @Override
    public synchronized List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        return mCookieStore.load(url);
    }

    public CookieStore getCookieStore() {
        return mCookieStore;
    }
}
