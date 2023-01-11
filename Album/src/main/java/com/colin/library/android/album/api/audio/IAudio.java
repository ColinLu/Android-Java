package com.colin.library.android.album.api.audio;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 01:47
 * <p>
 * 描述： 音频
 */
public interface IAudio<Returner> {

    /*多媒体音频大小*/
    @NonNull
    Returner size(@IntRange(from = 0) long limit);

    /*多媒体音频时长*/
    @NonNull
    Returner duration(@IntRange(from = 0) long limit);

}
