package com.colin.library.android.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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
}
