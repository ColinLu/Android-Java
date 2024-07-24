package com.colin.library.android.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.colin.library.android.helper.UtilHelper;
import com.colin.library.android.provider.AppFileProvider;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 23:40
 * <p>
 * 描述： Uri 工具类
 */
public final class UriUtil {
    private UriUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @NonNull
    public static Uri getUri(@NonNull final Uri uri, int id, @NonNull String path) {
        return getUri(UtilHelper.getInstance().getUtilConfig().getApplication(), uri, id, path);
    }

    @NonNull
    public static Uri getUri(@NonNull Context context, @NonNull final Uri uri, int id, @NonNull String path) {
        return uri.buildUpon().appendPath(String.valueOf(id)).build();
    }

    /*File 转 Uri*/
    @NonNull
    public static Uri getUri(@NonNull Context context, @NonNull File file) {
        return AppFileProvider.getUri(context, file);
    }

}
