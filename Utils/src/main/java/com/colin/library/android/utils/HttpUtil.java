package com.colin.library.android.utils;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.Encode;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Scanner;

/**
 * 作者： ColinLu
 * 时间： 2022-01-16 20:23
 * <p>
 * 描述： Http
 */
public final class HttpUtil {

    private HttpUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }


    private static final int CONNECT_TIMEOUT_TIME = 15_000;
    private static final int READ_TIMEOUT_TIME = 19_000;

    /**
     * @return {@code true} if the url is a network url.
     */
    public static boolean isUrl(@Nullable final String url) {
        return isHttp(url) || isHttps(url);
    }

    public static boolean isHttp(@Nullable final String url) {
        return isHttpUrl(url, 6, 0, 7, "http://");
    }

    public static boolean isHttps(@Nullable final String url) {
        return isHttpUrl(url, 7, 0, 8, "https://");
    }

    private static boolean isHttpUrl(@Nullable String url, int length, int startIndex, int endIndex, @NonNull String name) {
        return (null != url) && (url.length() > length) && url.substring(startIndex, endIndex).equalsIgnoreCase(name);
    }

    @Nullable
    public static String encode(@Nullable final String text, @NonNull final String encode) {
        if (TextUtils.isEmpty(text)) return null;
        try {
            return URLEncoder.encode(text, encode);
        } catch (UnsupportedEncodingException e) {
            LogUtil.log(e);
        }
        return null;
    }

    @Nullable
    public static String decode(@Nullable final String text, @NonNull final String encode) {
        if (TextUtils.isEmpty(text)) return null;
        try {
            return URLDecoder.decode(text, encode);
        } catch (UnsupportedEncodingException e) {
            LogUtil.log(e);
        }
        return null;
    }

    /**
     * 解析文件头
     * Content-Disposition:attachment;filename=FileName.txt
     * Content-Disposition: attachment; filename*="UTF-8''%E6%9B%BF%E6%8D%A2%E5%AE%9E%E9%AA%8C%E6%8A%A5%E5%91%8A.pdf"
     */
    @Nullable
    public static String getFileName(@Nullable final String disposition) {
        if (StringUtil.isEmpty(disposition)) return null;
        //文件名可能包含双引号，需要去除
        String fileName = disposition.replaceAll("\"", "");
        String split = "filename=";
        int indexOf = fileName.indexOf(split);
        if (indexOf != -1) return fileName.substring(indexOf + split.length());
        split = "filename*=";
        indexOf = fileName.indexOf(split);
        if (indexOf != -1) {
            fileName = fileName.substring(indexOf + split.length());
            if (fileName.startsWith(Encode.UTF_8))
                fileName = fileName.substring(Encode.UTF_8.length());
            return fileName;
        }
        return null;
    }

    /**
     * 解析URL地址 获取文件名字 带后缀 eg:  xxx.txt
     * https://txt.xbaoshu.com/d/file/down/2023/11/29/都市医仙.txt
     *
     * @param url
     * @param encode
     * @return
     */
    @Nullable
    public static String getFileName(@Nullable final String url, @Nullable String encode) {
        if (StringUtil.isEmpty(url)) return null;
        final String[] strings = url.split("/");
        String fileName = getFileName(strings);
        if (StringUtil.isEmpty(fileName)) fileName = strings[strings.length - 1];
        return StringUtil.isEmpty(encode) ? fileName : decode(fileName, encode);
    }

    private static String getFileName(@NonNull final String[] array) {
        final String key = "?";
        for (String name : array) {
            if (name.contains(key)) {
                int index = name.indexOf("?");
                if (index != -1) return name.substring(0, index);
            }
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

    /*文件名 xxx.mp3*/
    @Nullable
    public static String getMimeType(@Nullable final String fileName) {
        if (StringUtil.isSpace(fileName)) return null;
        return URLConnection.getFileNameMap().getContentTypeFor(fileName.toLowerCase(Locale.US));
    }


    @NonNull
    public static String getAcceptLanguage() {
        final Locale locale = Locale.getDefault();
        final String language = locale.getLanguage();
        final String country = locale.getCountry();
        final StringBuilder sb = new StringBuilder(language);
        if (!StringUtil.isEmpty(country))
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

        final Locale locale = Locale.getDefault();
        final String language = locale.getLanguage();
        if (!StringUtil.isEmpty(language)) {
            sb.append(language.toLowerCase(locale));
            final String country = locale.getCountry();
            if (!StringUtil.isEmpty(country)) sb.append("-").append(country.toLowerCase(locale));
        } else sb.append("en");// default to "en"

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (!StringUtil.isEmpty(model)) sb.append("; ").append(model);
        }
        final String id = Build.ID;
        if (!StringUtil.isEmpty(id)) sb.append(" Build/").append(id);
        return String.format(webUserAgent, sb, "Mobile ");
    }

    /**
     * POST + JSON
     *
     * @param url  target url
     * @param json send data
     * @return data receive from server
     * @author MilkZS
     */
    public static String postJson(@NonNull final String url, @NonNull final String json) {
        return http(url, true, json, true);
    }

    /**
     * POST + FORM
     *
     * @param url  target url
     * @param form send data
     * @return data receive from serv
     * @author MilkZS
     */
    public static String postForm(@NonNull final String url, @NonNull final String form) {
        return http(url, true, form, false);
    }

    /**
     * GET + JSON
     *
     * @param url  target url
     * @param json send data
     * @return data receive from server
     * @author MilkZS
     */
    public static String getJson(@NonNull final String url, @NonNull final String json) {
        return http(url, false, json, false);
    }

    /**
     * GET + FORM
     *
     * @param url  target url
     * @param form send data
     * @return data receive from server
     * @author MilkZS
     */
    public static String getForm(@NonNull final String url, @NonNull final String form) {
        return http(url, false, form, false);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法
    ///////////////////////////////////////////////////////////////////////////
    /*网络请求*/
    private static String http(@NonNull final String url, final boolean post, @NonNull final String data, final boolean json) {
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


}
