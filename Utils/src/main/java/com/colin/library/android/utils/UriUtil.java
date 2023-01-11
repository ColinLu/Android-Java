package com.colin.library.android.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

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
        return getUri(UtilHelper.getInstance().getContext(), uri, id, path);
    }

    @NonNull
    public static Uri getUri(@NonNull Context context, @NonNull final Uri uri, int id, @NonNull String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return uri.buildUpon().appendPath(String.valueOf(id)).build();
        }
        return AppFileProvider.getUri(context, new File(path));
    }

    /*File 转 Uri*/
    @NonNull
    public static Uri getUri(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return AppFileProvider.getUri(context, file);
        }
        return Uri.fromFile(file);
    }

}
