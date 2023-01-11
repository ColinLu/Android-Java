package com.colin.library.android.album.api.camera;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.colin.library.android.album.def.MediaType;

public class CameraAudio extends Camera<CameraAudio> {

    public CameraAudio(@NonNull String path) {
        super(path, MediaType.AUDIO);
    }

    public CameraAudio(@NonNull Uri uri) {
        super(uri, MediaType.AUDIO);
    }

}
