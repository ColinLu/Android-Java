package com.colin.library.android.media;

import androidx.annotation.NonNull;

import com.colin.library.android.media.def.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:40
 * <p>
 * 描述： 多媒体类型选择
 */
public interface IMedia<Image, Audio, Video, Camera, Preview> {
    @NonNull
    Image image();

    Image image(@MediaType int mediaType);

    @NonNull
    Audio audio();

    Audio audio(@MediaType int mediaType);

    @NonNull
    Video video();

    Video video(@MediaType int mediaType);

    @NonNull
    Camera camera();

    @NonNull
    Preview preview();

    Preview preview(@MediaType int mediaType);
}
