package com.colin.library.android.media.camera;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.colin.library.android.media.ICamera;

import java.io.File;


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
    public CameraImage image(@NonNull File file) {
        return new CameraImage(file);
    }

    @NonNull
    public CameraImage image(@NonNull Uri uri) {
        return new CameraImage(uri);
    }

    @NonNull
    public CameraAudio audio(@NonNull File file) {
        return new CameraAudio(file);
    }

    @NonNull
    public CameraAudio audio(@NonNull Uri uri) {
        return new CameraAudio(uri);
    }

    @NonNull
    public CameraVideo video(@NonNull File file) {
        return new CameraVideo(file);
    }

    @NonNull
    public CameraVideo video(@NonNull Uri uri) {
        return new CameraVideo(uri);
    }


}
