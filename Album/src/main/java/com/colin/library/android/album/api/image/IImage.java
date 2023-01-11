package com.colin.library.android.album.api.image;

import androidx.annotation.NonNull;

import com.colin.library.android.album.def.Facing;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 02:03
 * <p>
 * 描述： 图片
 */
public interface IImage<Returner> {
    @NonNull
    Returner crop(boolean need);

    @NonNull
    Returner facing(@Facing int facing);
}
