package com.colin.library.android.media.def;

import android.widget.ImageView;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 23:16
 * <p>
 * 描述： 相册文件加载器
 */
public interface MediaLoader {
    MediaLoader DEFAULT = (imageView, media) -> {
    };

    void load(@NonNull ImageView imageView, @NonNull MediaFile media);
}
