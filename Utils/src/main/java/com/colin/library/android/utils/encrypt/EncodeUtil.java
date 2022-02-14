package com.colin.library.android.utils.encrypt;

import android.os.Build;
import android.text.Html;
import android.text.TextUtils;

import androidx.annotation.Nullable;


import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.data.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


/**
 * 作者： ColinLu
 * 时间： 2019-02-21 17:30
 * <p>
 * 描述： Url 网络接口 加密解密
 */
public final class EncodeUtil {

    /*加密 url*/
    @Nullable
    public static String encode(@Nullable final String url) {
        return encode(url, Constants.UTF_8);
    }

    /*加密 url*/
    @Nullable
    public static String encode(@Nullable final String url, @Nullable final String enc) {
        if (TextUtils.isEmpty(enc)) return url;
        if (TextUtils.isEmpty(url)) return null;
        try {
            return URLEncoder.encode(url, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*解密 url*/
    @Nullable
    public static String decode(@Nullable final String url) {
        return encode(url, Constants.UTF_8);
    }

    /*解密 url*/
    @Nullable
    public static String decode(@Nullable final String url, @Nullable final String enc) {
        if (TextUtils.isEmpty(enc)) return url;
        if (TextUtils.isEmpty(url)) return null;
        try {
            return URLDecoder.decode(url, enc);
        } catch (UnsupportedEncodingException e) {
            LogUtil.log(e);
        }
        return null;
    }

    /**
     * Return html-encode string.
     *
     * @param input The input.
     * @return html-encode string
     */
    public static String htmlEncode(@Nullable final CharSequence input) {
        if (input == null || input.length() == 0) return "";
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0, len = input.length(); i < len; i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was
                    // introduced in XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead of &apos; to work as expected in HTML 4
                    // user agents.
                    sb.append("&#39;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Return the string of decode html-encode string.
     *
     * @param input The input.
     * @return the string of decode html-encode string
     */
    @SuppressWarnings("deprecation")
    public static CharSequence htmlDecode(@Nullable final String input) {
        if (input == null || input.length() == 0) return "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(input);
        }
    }

    /**
     * Return the binary encoded string padded with one space
     *
     * @param input
     * @return binary string
     */
    @Nullable
    public static String binEncode(@Nullable final String input) {
        if (TextUtils.isEmpty(input)) return null;
        final StringBuilder stringBuilder = new StringBuilder();
        for (char i : input.toCharArray()) {
            stringBuilder.append(Integer.toBinaryString(i));
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    /**
     * Return UTF-8 String from binary
     *
     * @param input binary string
     * @return UTF-8 String
     */
    @Nullable
    public static String binDecode(@Nullable final String input) {
        if (TextUtils.isEmpty(input)) return null;
        final String[] array = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String i : array) {
            sb.append(((char) Integer.parseInt(i.replace(" ", ""), 2)));
        }
        return sb.toString();
    }
}