package com.colin.library.android.okHttp.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.bean.CookieBean;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.SpUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class SpCookieStore implements CookieStore {
    private static final String COOKIE_PREFS = "http_cookie_";           //cookie使用prefs保存
    private static final String COOKIE_NAME_PREFIX = "cookie_";         //cookie持久化的统一前缀
    private final SharedPreferences mSp;
    private final Map<String, ConcurrentHashMap<String, Cookie>> mMap;

    public SpCookieStore() {
        this.mSp = SpUtil.getSp(COOKIE_PREFS, Context.MODE_PRIVATE);
        this.mMap = new HashMap<>();
        //将持久化的cookies缓存到内存中,数据结构为 Map<Url.host, Map<CookieToken, Cookie>>
        Map<String, ?> prefsMap = mSp.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if ((entry.getValue()) != null && !entry.getKey().startsWith(COOKIE_NAME_PREFIX)) {
                //获取url对应的所有cookie的key,用","分割
                String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                for (String name : cookieNames) {
                    //根据对应cookie的Key,从xml中获取cookie的真实值
                    final String encodedCookie = mSp.getString(COOKIE_NAME_PREFIX + name, null);
                    final Cookie cookie = null == encodedCookie ? null : CookieBean.decode(encodedCookie);
                    if (null == cookie) continue;
                    ConcurrentHashMap<String, Cookie> hashMap = mMap.get(entry.getKey());
                    if (null == hashMap) {
                        hashMap = new ConcurrentHashMap<>();
                        hashMap.put(entry.getKey(), cookie);
                        mMap.put(name, hashMap);
                    } else hashMap.put(entry.getKey(), cookie);
                }
            }
        }
    }

    @Override
    public synchronized void save(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        for (Cookie cookie : cookies) save(url, cookie);
    }

    @Override
    public synchronized void save(@NonNull HttpUrl url, @NonNull Cookie cookie) {
        if (isCookieExpired(cookie)) {
            remove(url, cookie);
            return;
        }
        final String host = url.host();
        if (TextUtils.isEmpty(host)) return;
        final String key = getCookieKey(cookie);
        ConcurrentHashMap<String, Cookie> hashMap = mMap.get(host);
        if (null == hashMap) {
            hashMap = new ConcurrentHashMap<>();
            hashMap.put(key, cookie);
            mMap.put(host, hashMap);
        } else hashMap.put(key, cookie);

        //文件缓存
        SharedPreferences.Editor prefsWriter = mSp.edit();
        prefsWriter.putString(host, TextUtils.join(",", mMap.get(host).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + key, CookieBean.encode(host, cookie));
        prefsWriter.apply();

    }

    @NonNull
    @Override
    public synchronized List<Cookie> load(@NonNull HttpUrl url) {
        List<Cookie> list = new ArrayList<>(2);
        final String host = url.host();

        if (TextUtils.isEmpty(host) || !mMap.containsKey(host)) return list;
        final Collection<Cookie> urlCookies = Objects.requireNonNull(mMap.get(host)).values();
        for (Cookie cookie : urlCookies) {
            if (isCookieExpired(cookie)) remove(url, cookie);
            else list.add(cookie);
        }
        return list;
    }

    @NonNull
    @Override
    public synchronized List<Cookie> loadAll() {
        List<Cookie> list = new ArrayList<>();
        for (String key : mMap.keySet()) {
            ConcurrentHashMap<String, Cookie> map = mMap.get(key);
            if (null == map || map.size() == 0) continue;
            Collection<Cookie> values = map.values();
            if (values.size() == 0) continue;
            list.addAll(values);
        }
        return list;
    }

    @NonNull
    @Override
    public String getCookieKey(@NonNull Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }

    @Override
    public boolean isCookieExpired(@NonNull Cookie cookie) {
        return HttpUtil.isCookieExpired(cookie);
    }

    @Override
    public synchronized void remove(@NonNull HttpUrl url, @NonNull Cookie cookie) {
        if (!mMap.containsKey(url.host())) return;
        //内存移除
        ConcurrentHashMap<String, Cookie> urlCookie = mMap.remove(url.host());
        if (null == urlCookie || urlCookie.size() == 0) return;
        //文件移除
        Set<String> cookieTokens = urlCookie.keySet();
        SharedPreferences.Editor prefsWriter = mSp.edit();
        for (String cookieToken : cookieTokens) {
            if (mSp.contains(COOKIE_NAME_PREFIX + cookieToken)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + cookieToken);
            }
        }
        prefsWriter.remove(url.host());
        prefsWriter.apply();
    }

    @Override
    public synchronized void removeAll() {
        //内存移除
        mMap.clear();
        //文件移除
        final SharedPreferences.Editor edit = mSp.edit();
        edit.clear().apply();
    }
}
