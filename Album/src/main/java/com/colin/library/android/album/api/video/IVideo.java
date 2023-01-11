package com.colin.library.android.album.api.video;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.def.Facing;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 02:03
 * <p>
 * 描述： 视频
 */
public interface IVideo<Returner> {
    @NonNull
    Returner facing(@Facing int facing);

    /*多媒体视频质量*/
    @NonNull
    Returner quality(@IntRange(from = 0, to = 1) int limit);

    /*多媒体视频大小*/
    @NonNull
    Returner size(@IntRange(from = 0) long limit);

    /*多媒体视频时长*/
    @NonNull
    Returner duration(@IntRange(from = 0) long limit);

}
