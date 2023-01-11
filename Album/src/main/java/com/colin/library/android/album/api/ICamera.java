package com.colin.library.android.album.api;

import android.net.Uri;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 22:31
 * <p>
 * 描述： Camera选择
 */
public interface ICamera<Image, Audio, Video> {
    /*拍照*/
    @NonNull
    Image image(@NonNull String path);

    @NonNull
    Image image(@NonNull Uri uri);

    /*录音*/
    @NonNull
    Audio audio(@NonNull String path);

    @NonNull
    Audio audio(@NonNull Uri uri);

    /*视频*/
    @NonNull
    Video video(@NonNull String path);

    @NonNull
    Video video(@NonNull Uri uri);

}