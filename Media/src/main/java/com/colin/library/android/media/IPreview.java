package com.colin.library.android.media;

import android.media.Image;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 02:54
 * <p>
 * 描述： TODO
 */
public interface IPreview<Image, Audio, Video, Media> {
    @NonNull
    Image image();

    @NonNull
    Audio audio();

    @NonNull
    Video video();

    @NonNull
    Media media();

}
