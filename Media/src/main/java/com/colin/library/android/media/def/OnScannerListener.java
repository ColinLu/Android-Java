package com.colin.library.android.media.def;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2020-07-14 21:43
 * <p>
 * 描述： 扫描文件
 */
public interface OnScannerListener {
    default void start(@NonNull String path) {

    }

    void finish(@Nullable String path, @Nullable Uri uri);

}
