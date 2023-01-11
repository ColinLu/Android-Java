package com.colin.library.android.album.api;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:40
 * <p>
 * 描述： TODO
 */
public interface IMedia<Image, Audio, Video> {
    @NonNull
    Image image();

    @NonNull
    Audio audio();

    @NonNull
    Video video();
}
