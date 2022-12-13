package com.colin.library.android.http.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.colin.library.android.annotation.Encode;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.encrypt.EncodeUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2019-09-05 11:29
 * <p>
 * 描述： 请求头部信息
 */
public final class HttpParams {
    private final Map<String, List<String>> mParams;

    private HttpParams(@NonNull Builder builder) {
        this.mParams = builder.mMap;
    }

    @NonNull
    public Builder newBuilder() {
        final Builder builder = new Builder();
        builder.mMap.putAll(mParams);
        return builder;
    }

    public int size() {
        return mParams.size();
    }


    @Nullable
    public String getValue(@NonNull String key) {
        final List<String> list = mParams.get(key);
        if (null == list || list.size() == 0) return null;
        return list.get(0);
    }

    @Nullable
    public List<String> getValues(@NonNull String key) {
        return mParams.get(key);
    }

    @NonNull
    public Set<String> getKeySet() {
        return mParams.keySet();
    }


    public static final class Builder {
        @NonNull
        private final Map<String, List<String>> mMap;


        public Builder() {
            mMap = new LinkedHashMap<>();
        }

        public Builder(Map<String, List<String>> map) {
            mMap = map;
        }

        @NonNull
        public Builder add(@NonNull String key, @Nullable String value) {
            if (StringUtil.isEmpty(value)) return this;
            if (!mMap.containsKey(key)) {
                mMap.put(key, new ArrayList<>(1));
            }
            mMap.get(key).add(value);
            return this;
        }

        public Builder add(@NonNull String key, @Nullable List<String> list) {
            if (null == list || list.size() == 0) return this;
            if (mMap.containsKey(key)) mMap.get(key).addAll(list);
            else mMap.put(key, list);
            return this;
        }

        public Builder add(@Nullable @Size(min = 0) Map<String, String> map) {
            if (map != null && map.size() > 0) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    final String key = entry.getKey();
                    if (!StringUtil.isEmpty(key)) add(key, entry.getValue());
                }
            }
            return this;
        }

        public Builder set(@NonNull String key, @Nullable String value) {
            remove(key);
            return add(key, value);
        }

        public Builder set(@NonNull String key, @Nullable List<String> list) {
            if (list != null && list.size() > 0) mMap.put(key, list);
            return this;
        }

        public Builder set(@NonNull @Size(min = 1) Map<String, String> map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                set(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Builder remove(@NonNull String key) {
            mMap.remove(key);
            return this;
        }

        public Builder removeAll() {
            mMap.clear();
            return this;
        }

        public HttpParams build() {
            return new HttpParams(this);
        }

    }


    @Override
    public String toString() {
        return toString(Encode.UTF_8);
    }

    @NonNull
    public String toString(@NonNull String encode) {
        if (mParams.size() == 0) return "";
        final StringBuilder sb = new StringBuilder();
        for (String key : mParams.keySet()) {
            final String encodeKey = EncodeUtil.encode(key, encode);
            if (StringUtil.isEmpty(encodeKey)) continue;
            final List<String> values = getValues(key);
            if (null == values || values.size() == 0) {
                sb.append("&").append(encodeKey).append("=").append("");
            } else {
                for (String value : values) {
                    final String encodeValue = EncodeUtil.encode(value, encode);
                    sb.append("&").append(encodeKey).append("=").append(null == encodeValue ? "" : encodeValue);
                }
            }
        }
        if (sb.length() > 0) sb.deleteCharAt(0);
        return sb.toString();
    }

    @Nullable
    public RequestBody toRequestBody(@NonNull String encode) {
        final Set<String> strings = mParams.keySet();
        if (strings.size() == 0) return null;
        final FormBody.Builder builder = new FormBody.Builder(Charset.forName(encode));
        boolean empty = true;
        for (final String key : strings) {
            String encodeKey = EncodeUtil.encode(key, encode);
            if (StringUtil.isEmpty(encodeKey)) continue;
            final List<String> values = mParams.get(key);
            empty = false;
            if (values != null && values.size() > 0) {
                for (String value : values) builder.addEncoded(key, null == value ? "" : value);
            } else builder.addEncoded(key, "");
        }

        return empty ? null : builder.build();
    }
}
