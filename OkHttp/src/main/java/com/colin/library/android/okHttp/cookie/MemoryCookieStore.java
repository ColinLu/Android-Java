package com.colin.library.android.okHttp.cookie;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.colin.library.android.utils.HttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class MemoryCookieStore implements CookieStore {
    private final Map<String, ConcurrentHashMap<String, Cookie>> mMap;

    public MemoryCookieStore() {
        mMap = new HashMap<>();
    }

    @Override
    public synchronized void save(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            if (isCookieExpired(cookie)) remove(url, cookie);
            else save(url, cookie);
        }
    }

    @Override
    public synchronized void save(@NonNull HttpUrl url, @NonNull Cookie cookie) {
        final String host = url.host();
        if (TextUtils.isEmpty(host) || isCookieExpired(cookie)) return;
        ConcurrentHashMap<String, Cookie> hashMap = mMap.get(host);
        if (null == hashMap) {
            hashMap = new ConcurrentHashMap<>();
            hashMap.put(getCookieKey(cookie), cookie);
            mMap.put(host, hashMap);
        } else hashMap.put(getCookieKey(cookie), cookie);
    }

    @NonNull
    @Override
    public synchronized List<Cookie> load(@NonNull HttpUrl url) {
        final List<Cookie> list = new ArrayList<>(2);
        final String host = url.host();
        if (TextUtils.isEmpty(host) || !mMap.containsKey(host)) return list;
        for (Map.Entry<String, ConcurrentHashMap<String, Cookie>> next : mMap.entrySet()) {
            final String key = next.getKey();
            if (!host.equals(key)) continue;
            final Iterator<Map.Entry<String, Cookie>> it = next.getValue().entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, Cookie> cookieEntry = it.next();
                final Cookie cookie = cookieEntry.getValue();
                if (isCookieExpired(cookie)) it.remove();
                else list.add(cookie);
            }
        }
        return list;
    }

    @NonNull
    @Override
    public synchronized List<Cookie> loadAll() {
        final List<Cookie> list = new ArrayList<>(2);
        for (Map.Entry<String, ConcurrentHashMap<String, Cookie>> next : mMap.entrySet()) {
            final Iterator<Map.Entry<String, Cookie>> it = next.getValue().entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, Cookie> cookieEntry = it.next();
                final Cookie cookie = cookieEntry.getValue();
                if (isCookieExpired(cookie)) it.remove();
                else list.add(cookie);
            }
        }
        return list;
    }

    @Override
    public synchronized void remove(@NonNull HttpUrl url, @NonNull Cookie cookie) {
        final String host = url.host();
        if (TextUtils.isEmpty(host) || !mMap.containsKey(host)) return;
        final String cookieKey = getCookieKey(cookie);
        for (Map.Entry<String, ConcurrentHashMap<String, Cookie>> next : mMap.entrySet()) {
            final String key = next.getKey();
            if (!host.equals(key)) continue;
            final Iterator<Map.Entry<String, Cookie>> it = next.getValue().entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, Cookie> entry = it.next();
                final Cookie entryValue = entry.getValue();
                final String currentKey = getCookieKey(entryValue);
                if (cookieKey.equals(currentKey) || isCookieExpired(entryValue)) it.remove();
            }
        }
    }

    /* 获取 cookie key */
    @NonNull
    @Override
    public String getCookieKey(@NonNull Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }

    /* 当前cookie是否过期 */
    @Override
    public boolean isCookieExpired(@NonNull Cookie cookie) {
        return HttpUtil.isCookieExpired(cookie);
    }

    @Override
    public void removeAll() {
        mMap.clear();
    }


}
