package com.colin.library.android.utils;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;

import okhttp3.HttpUrl;
import okhttp3.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2022-01-16 20:23
 * <p>
 * 描述： TODO
 */
public final class HttpUtil {

    private HttpUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }


    private static final int CONNECT_TIMEOUT_TIME = 15000;
    private static final int READ_TIMEOUT_TIME = 19000;

    /**
     * POST + JSON
     *
     * @param url  target url
     * @param json send data
     * @return data receive from server
     * @author MilkZS
     */
    public static String postJson(@NonNull String url, @NonNull String json) {
        return doHttpAction(url, true, json, true);
    }

    /**
     * POST + FORM
     *
     * @param url  target url
     * @param form send data
     * @return data receive from serv
     * @author MilkZS
     */
    public static String postForm(@NonNull String url, @NonNull String form) {
        return doHttpAction(url, true, form, false);
    }

    /**
     * GET + JSON
     *
     * @param url  target url
     * @param json send data
     * @return data receive from server
     * @author MilkZS
     */
    public static String getJson(@NonNull String url, @NonNull String json) {
        return doHttpAction(url, false, json, false);
    }

    /**
     * GET + FORM
     *
     * @param url  target url
     * @param form send data
     * @return data receive from server
     * @author MilkZS
     */
    public static String getForm(@NonNull String url, @NonNull String form) {
        return doHttpAction(url, false, form, false);
    }

    private static String doHttpAction(String url, boolean post, String data, boolean json) {
        HttpURLConnection connection = null;
        DataOutputStream os = null;
        InputStream is = null;
        try {
            URL sUrl = new URL(url);
            connection = (HttpURLConnection) sUrl.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_TIME);
            connection.setReadTimeout(READ_TIMEOUT_TIME);
            connection.setRequestMethod(post ? "POST" : "GET");
            //允许输入输出
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // 是否使用缓冲
            connection.setUseCaches(false);
            // 本次连接是否处理重定向，设置成true，系统自动处理重定向；
            // 设置成false，则需要自己从http reply中分析新的url自己重新连接。
            connection.setInstanceFollowRedirects(true);
            // 设置请求头里的属性
            if (json) {
                connection.setRequestProperty("Content-Type", "application/json");
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", String.valueOf(data.length()));
            }
            connection.connect();

            os = new DataOutputStream(connection.getOutputStream());
            os.write(data.getBytes(), 0, data.getBytes().length);
            os.flush();
            os.close();

            is = connection.getInputStream();
            Scanner scan = new Scanner(is);
            scan.useDelimiter("\\A");
            if (scan.hasNext()) return scan.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
            IOUtil.close(os, is);
        }
        return null;
    }

    @Nullable
    public static String getFileName(@Nullable final HttpUrl url) {
        return getFileName(url == null ? null : url.toString());
    }

    /*解析URL地址 获取文件名字 带后缀 eg:  xxx.txt*/
    @Nullable
    public static String getFileName(@Nullable final String url) {
        if (null == url || url.length() == 0) return null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) return string.substring(0, endIndex);
            }
        }
        if (strings.length > 0) return strings[strings.length - 1];
        return null;
    }


    /**
     * A value of the header information.
     *
     * @param header like {@code text/html;charset=utf-8}.
     * @param key    like {@code charset}.
     * @param def    list {@code utf-8}.
     * @return If you have a value key, you will return the parsed value if you don't return the default value.
     */
    @Nullable
    public static String head(@Nullable String header, @Nullable String key, @Nullable String def) {
        if (!TextUtils.isEmpty(header) && !TextUtils.isEmpty(key)) {
            final StringTokenizer stringTokenizer = new StringTokenizer(header, ";");
            while (stringTokenizer.hasMoreElements()) {
                final String valuePair = stringTokenizer.nextToken();
                final int index = valuePair.indexOf('=');
                if (index > 0) {
                    String name = valuePair.substring(0, index).trim();
                    if (key.equalsIgnoreCase(name)) {
                        def = valuePair.substring(index + 1).trim();
                        break;
                    }
                }
            }
        }
        return def;
    }

    @Nullable
    public static String encode(@Nullable final String txt, @NonNull final String encode) {
        if (TextUtils.isEmpty(txt)) return null;
        try {
            return URLEncoder.encode(txt, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    public static String getUrl(@NonNull final String url, @Nullable final String params) {
        if (StringUtil.isEmpty(params)) return url;
        final StringBuilder sb = new StringBuilder(url);
        //处理首个字符
        if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
        else sb.append("?");
        sb.append(params);
        return sb.toString();
    }

    @Nullable
    public static MediaType getMediaType(@Nullable String mimeType, @Nullable String encode) {
        if (StringUtil.isEmpty(mimeType) || StringUtil.isEmpty(encode)) return null;
        return MediaType.parse(mimeType + "; charset=" + encode);
    }

    /*多媒体类型*/
    @Nullable
    public static String getMimeType(@Nullable String fileName) {
        if (StringUtil.isEmpty(fileName)) return null;
        final String extension = MimeTypeMap.getFileExtensionFromUrl(fileName);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    @NonNull
    public static String getAcceptLanguage() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        StringBuilder sb = new StringBuilder(language);
        if (!TextUtils.isEmpty(country))
            sb.append('-').append(country).append(',').append(language).append(";q=0.8");
        return sb.toString();
    }

    @NonNull
    public static String getUserAgent() {
        final String webUserAgent = "Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko) Version/5.0 %sSafari/533.1";
        final StringBuilder sb = new StringBuilder();
        // Add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) sb.append(version);
        else sb.append("1.0");// default to "1.0"
        sb.append("; ");

        Locale locale = Locale.getDefault();
        final String language = locale.getLanguage();
        if (!TextUtils.isEmpty(language)) {
            sb.append(language.toLowerCase(locale));
            final String country = locale.getCountry();
            if (!TextUtils.isEmpty(country)) sb.append("-").append(country.toLowerCase(locale));
        } else sb.append("en");// default to "en"

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (!TextUtils.isEmpty(model)) sb.append("; ").append(model);
        }
        String id = Build.ID;
        if (!TextUtils.isEmpty(id)) sb.append(" Build/").append(id);
        return String.format(webUserAgent, sb, "Mobile ");
    }


}
