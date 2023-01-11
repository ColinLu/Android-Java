package com.colin.library.android.album.api.camera;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.colin.library.android.album.api.ICamera;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 21:43
 * <p>
 * 描述： 选择
 */
public class ChooseCamera implements ICamera<CameraImage, CameraAudio, CameraVideo> {
    public ChooseCamera() {
    }

    @NonNull
    public CameraImage image(@NonNull String path) {
        return new CameraImage(path);
    }

    @NonNull
    public CameraImage image(@NonNull Uri uri) {
        return new CameraImage(uri);
    }

    @NonNull
    public CameraAudio audio(@NonNull String path) {
        return new CameraAudio(path);
    }

    @NonNull
    public CameraAudio audio(@NonNull Uri uri) {
        return new CameraAudio(uri);
    }

    @NonNull
    public CameraVideo video(@NonNull String path) {
        return new CameraVideo(path);
    }

    @NonNull
    public CameraVideo video(@NonNull Uri uri) {
        return new CameraVideo(uri);
    }


}
