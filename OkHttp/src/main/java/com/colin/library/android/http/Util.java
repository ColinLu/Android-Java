package com.colin.library.android.http;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.colin.library.android.http.def.Constants;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.StringUtil;

import java.util.StringTokenizer;

import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2023-04-22 14:52
 * <p>
 * 描述： Util for OkHttp
 */
public final class Util {

    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String getFileName(@NonNull final Response response, @Nullable String encode) {
        final Request request = response.request();
        final Headers headers = request.headers();
        final String disposition = headers.get(Constants.HEAD_KEY_CONTENT_DISPOSITION);
        final String filename = HttpUtil.getFileName(disposition);
        return TextUtils.isEmpty(filename) ? HttpUtil.getFileName(request.url().toString(), encode) : filename;
    }



    public static boolean isCookieExpired(@NonNull Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
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
    public static MediaType getMediaType(@Nullable String mimeType, @Nullable String encode) {
        if (StringUtil.isEmpty(mimeType, encode)) return null;
        return MediaType.parse(mimeType + "; charset=" + encode);
    }
}
