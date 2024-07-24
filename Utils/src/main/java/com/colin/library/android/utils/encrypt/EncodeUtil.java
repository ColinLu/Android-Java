package com.colin.library.android.utils.encrypt;

import android.os.Build;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.colin.library.android.annotation.Encode;
import com.colin.library.android.utils.StringUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;


/**
 * 作者： ColinLu
 * 时间： 2019-02-21 17:30
 * <p>
 * 描述： Url 网络接口 加密解密
 */
public final class EncodeUtil {

    /*url 加密*/
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String encode(@Nullable final String url) {
        return encode(url, Encode.UTF_8);
    }

    /*url 加密*/
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String encode(@Nullable final String url, @NonNull @Encode final String encode) {
        return encode(url, Charset.forName(encode));
    }

    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String encode(@Nullable final String url, @Nullable final Charset charset) {
        return StringUtil.isEmpty(url) || charset == null ? url : URLEncoder.encode(url, charset);
    }

    /*url 解密*/
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String decode(@Nullable final String url) {
        return decode(url, Encode.UTF_8);
    }

    /*url 解密*/
    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String decode(@Nullable final String url, @NonNull @Encode final String encode) {
        return decode(url, Charset.forName(encode));
    }

    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String decode(@Nullable final String url, @Nullable final Charset charset) {
        return StringUtil.isEmpty(url) || charset == null ? url : URLDecoder.decode(url, charset);
    }

    /**
     * Return html-encode string.
     *
     * @param input The input.
     * @return html-encode string
     */
    @Nullable
    public static String htmlEncode(@Nullable final CharSequence input) {
        if (StringUtil.isEmpty(input)) return null;
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
    @Nullable
    public static CharSequence htmlDecode(@Nullable final String input) {
        if (StringUtil.isEmpty(input)) return null;
        return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
    }

    /**
     * Return the binary encoded string padded with one space
     *
     * @param input
     * @return binary string
     */
    @Nullable
    public static String binEncode(@Nullable final String input) {
        if (StringUtil.isEmpty(input)) return null;
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
        if (StringUtil.isEmpty(input)) return null;
        final String[] array = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String i : array) {
            sb.append(((char) Integer.parseInt(i.replace(" ", ""), 2)));
        }
        return sb.toString();
    }
}