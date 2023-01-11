package com.colin.library.android.media;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 22:31
 * <p>
 * 描述： Camera选择
 */
public interface ICamera<Image, Audio, Video> {
    /*拍照*/
    @NonNull
    Image image(@NonNull File file);

    @NonNull
    Image image(@NonNull Uri uri);

    /*录音*/
    @NonNull
    Audio audio(@NonNull File file);

    @NonNull
    Audio audio(@NonNull Uri uri);

    /*视频*/
    @NonNull
    Video video(@NonNull File file);

    @NonNull
    Video video(@NonNull Uri uri);

}