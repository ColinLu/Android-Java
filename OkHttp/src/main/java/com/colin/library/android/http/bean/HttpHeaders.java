package com.colin.library.android.http.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.colin.library.android.http.OkHttp;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import okhttp3.Headers;
import okhttp3.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2019-09-05 11:29
 * <p>
 * 描述： 请求头部信息
 */
public final class HttpHeaders {
    private final String[] namesAndValues;

    private HttpHeaders(Builder builder) {
        this.namesAndValues = builder.namesAndValues.toArray(new String[builder.namesAndValues.size()]);
    }

    private HttpHeaders(String[] namesAndValues) {
        this.namesAndValues = namesAndValues;
    }


    public Builder newBuilder() {
        final Builder result = new Builder();
        Collections.addAll(result.namesAndValues, namesAndValues);
        return result;
    }

    /* Returns the last value corresponding to the specified field, or null.*/
    @Nullable
    public String get(@NonNull String name) {
        return get(name, namesAndValues);
    }

    /*Returns the field at {@code index}.*/
    public String name(int index) {
        return namesAndValues[index * 2];
    }

    /*Returns the value at {@code index}.*/
    public String value(int index) {
        return namesAndValues[index * 2 + 1];
    }


    /*Returns an immutable case-insensitive set of header names.*/
    public Set<String> names() {
        final TreeSet<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0, size = size(); i < size; i++) result.add(name(i));
        return Collections.unmodifiableSet(result);
    }

    /*Returns an immutable list of the header values for {@code name}.*/
    public List<String> values(String name) {
        List<String> result = null;
        for (int i = 0, size = size(); i < size; i++) {
            if (name.equalsIgnoreCase(name(i))) {
                if (result == null) result = new ArrayList<>(2);
                result.add(value(i));
            }
        }
        return result != null ? Collections.unmodifiableList(result) : Collections.emptyList();
    }


    /*Returns the number of field values.*/
    public int size() {
        return namesAndValues.length / 2;
    }


    public Map<String, List<String>> toMultiMap() {
        final Map<String, List<String>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0, size = size(); i < size; i++) {
            String name = name(i).toLowerCase(Locale.US);
            List<String> values = result.get(name);
            if (values == null) {
                values = new ArrayList<>(2);
                result.put(name, values);
            }
            values.add(value(i));
        }
        return result;
    }

    public Headers getHeader() {
        return Headers.of(namesAndValues);
    }

    /**
     * Returns true if {@code other} is a {@code Headers} object with the same headers, with the same
     * casing, in the same order. Note that two headers instances may be <i>semantically</i> equal
     * but not equal according to this method. In particular, none of the following sets of headers
     * are equal according to this method: <pre>   {@code
     *
     *   1. Original
     *   Content-Type: text/html
     *   Content-Length: 50
     *
     *   2. Different order
     *   Content-Length: 50
     *   Content-Type: text/html
     *
     *   3. Different case
     *   content-type: text/html
     *   content-length: 50
     *
     *   4. Different values
     *   Content-Type: text/html
     *   Content-Length: 050
     * }</pre>
     * <p>
     * Applications that require semantically equal headers should convert them into a canonical form
     * before comparing them for equality.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        return other instanceof HttpHeaders && Arrays.equals(((HttpHeaders) other).namesAndValues, namesAndValues);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(namesAndValues);
    }

    public static final class Builder {
        @NonNull
        final List<String> namesAndValues = new ArrayList<>(20);

        public Builder set(@NonNull String name, @NonNull String value) {
            checkNameAndValue(name, value);
            remove(name);
            return addLenient(name, value);
        }

        public Builder set(@NonNull Map<String, String> map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                set(entry.getKey(), entry.getValue());
            }
            return this;
        }

        public Builder add(@NonNull String name, @NonNull String value) {
            checkNameAndValue(name, value);
            return addLenient(name, value);
        }

        public Builder add(@NonNull Map<String, String> map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                add(entry.getKey(), entry.getValue());
            }
            return this;
        }


        public Builder remove(@NonNull String name) {
            for (int i = 0; i < namesAndValues.size(); i += 2) {
                if (name.equalsIgnoreCase(namesAndValues.get(i))) {
                    namesAndValues.remove(i); // name
                    namesAndValues.remove(i); // value
                    i -= 2;
                }
            }
            return this;
        }

        public Builder removeAll() {
            namesAndValues.clear();
            return this;
        }

        @NonNull
        public HttpHeaders build() {
            return new HttpHeaders(this);
        }

        private Builder addLenient(@NonNull String name, @NonNull String value) {
            namesAndValues.add(name);
            namesAndValues.add(value.trim());
            return this;
        }

        private void checkNameAndValue(@Nullable String name, @Nullable String value) {
            if (name == null) throw new NullPointerException("name == null");

            if (name.isEmpty()) throw new IllegalArgumentException("name is empty");

            if (value == null)
                throw new NullPointerException("value for name " + name + " == null");
        }

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0, size = size(); i < size; i++) {
            result.append(name(i)).append(": ").append(value(i)).append("\n");
        }
        return result.toString();
    }

    private static String get(@NonNull String name, @NonNull @Size(min = 2) String[] namesAndValues) {
        for (int i = namesAndValues.length - 2; i >= 0; i -= 2) {
            if (name.equalsIgnoreCase(namesAndValues[i])) return namesAndValues[i + 1];
        }
        return null;
    }


    /**
     * Format to Hump-shaped words.
     */
    public static String formatKey(String key) {
        if (TextUtils.isEmpty(key)) return "";

        key = key.toLowerCase(Locale.ENGLISH);
        String[] words = key.split("-");

        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            String first = word.substring(0, 1);
            String end = word.substring(1);
            builder.append(first.toUpperCase(Locale.ENGLISH)).append(end).append("-");
        }
        if (builder.length() > 0) builder.deleteCharAt(builder.lastIndexOf("-"));
        return builder.toString();
    }


    /**
     * A value of the header information.
     *
     * @param content      like {@code text/html;charset=utf-8}.
     * @param key          like {@code charset}.
     * @param defaultValue list {@code utf-8}.
     * @return If you have a value key, you will return the parsed value if you don't return the default value.
     */
    public static String getValue(@Nullable final String content, @Nullable final String key, @Nullable String defaultValue) {
        if (TextUtils.isEmpty(content) || TextUtils.isEmpty(key)) return defaultValue;
        StringTokenizer stringTokenizer = new StringTokenizer(content, ";");
        while (stringTokenizer.hasMoreElements()) {
            String valuePair = stringTokenizer.nextToken();
            int index = valuePair.indexOf('=');
            if (index > 0) {
                String name = valuePair.substring(0, index).trim();
                if (key.equalsIgnoreCase(name)) {
                    defaultValue = valuePair.substring(index + 1).trim();
                    break;
                }
            }
        }
        return defaultValue;
    }

    @Nullable
    public static MediaType getContextType(@Nullable Headers headers) {
        if (null == headers) return null;
        String contentType = headers.get(OkHttp.HEAD_KEY_CONTENT_TYPE);
        if (TextUtils.isEmpty(contentType)) return null;
        return MediaType.parse(contentType);
    }

    @Nullable
    public static Date getDate(@Nullable Headers header) {
        return null == header ? null : header.getDate(OkHttp.HEAD_KEY_DATE);
    }

    @Nullable
    public static String getExpires(@Nullable Headers header) {
        return getValue(header, OkHttp.HEAD_KEY_EXPIRES);
    }

    @Nullable
    public static String getCacheControl(@Nullable Headers header) {
        if (null == header) return null;
        final String control = header.get(OkHttp.HEAD_KEY_CACHE_CONTROL);
        return TextUtils.isEmpty(control) ? header.get(OkHttp.HEAD_KEY_PRAGMA) : control;
    }

    public static String getETag(@Nullable Headers headers) {
        return getValue(headers, OkHttp.HEAD_KEY_E_TAG);
    }

    public static String getValue(@Nullable Headers headers, @Nullable String name) {
        return null == headers || null == name ? null : headers.get(name);
    }

    public static long getTime(@Nullable String date) {
        return TimeUtil.parseHttpTime(date);
    }


    /**
     * 根据请求结果生成对应的缓存实体类，以下为缓存相关的响应头
     * Cache-Control: public                             响应被缓存，并且在多用户间共享
     * Cache-Control: private                            响应只能作为私有缓存，不能在用户之间共享
     * Cache-Control: no-cache                           提醒浏览器要从服务器提取文档进行验证
     * Cache-Control: no-store                           绝对禁止缓存（用于机密，敏感文件）
     * Cache-Control: max-age=60                         60秒之后缓存过期（相对时间）,优先级比Expires高
     * Date: Mon, 19 Nov 2012 08:39:00 GMT               当前response发送的时间
     * Expires: Mon, 19 Nov 2012 08:40:01 GMT            缓存过期的时间（绝对时间）
     * Last-Modified: Mon, 19 Nov 2012 08:38:01 GMT      服务器端文件的最后修改时间
     * ETag: "20b1add7ec1cd1:0"                          服务器端文件的ETag值
     * 如果同时存在cache-control和Expires，浏览器总是优先使用cache-control
     *
     * @param header 返回数据中的响应头
     * @return 缓存有效时间
     */
    public static long getLocalExpire(@Nullable final Headers header) {
        if (null == header) return 0;
        final String cacheControl = getCacheControl(header);
        final long expires = getTime(getExpires(header));
        if (!TextUtils.isEmpty(cacheControl)) {
            final long maxAge = getMaxAge(header, cacheControl);
            if (maxAge <= 0) return 0;
            Date date = getDate(header);
            long time = null == date ? 0 : date.getTime();
            long now = time > 0 ? time : System.currentTimeMillis();
            return now + maxAge * 1000;
        } else if (expires > 0) return expires;
        else return 0;
    }

    /**
     * Cache-Control: public                             响应被缓存，并且在多用户间共享
     * Cache-Control: private                            响应只能作为私有缓存，不能在用户之间共享
     * Cache-Control: no-cache                           提醒浏览器要从服务器提取文档进行验证
     * Cache-Control: no-store                           绝对禁止缓存（用于机密，敏感文件）
     * Cache-Control: max-age=60                         60秒之后缓存过期（相对时间）,优先级比Expires高
     *
     * @param header
     * @param cacheControl
     * @return
     */
    public static long getMaxAge(@Nullable Headers header, @Nullable String cacheControl) {
        if (null == header || TextUtils.isEmpty(cacheControl)) return 0;
        StringTokenizer tokens = new StringTokenizer(cacheControl, ",");
        long maxAge = 0;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim().toLowerCase(Locale.getDefault());
            //服务器指定不缓存
            if (token.equals("no-cache") || token.equals("no-store")) return 0;
            if (token.startsWith("max-age=")) {
                try {
                    //获取最大缓存时间
                    maxAge = Long.parseLong(token.substring(8), 10);
                    //服务器缓存设置立马过期，不缓存
                    if (maxAge < 0) return 0;
                } catch (Exception e) {
                    LogUtil.log(e);
                }
            }
        }
        return maxAge;
    }
}
