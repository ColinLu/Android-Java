package com.colin.library.android.media.camera;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.base.IChooseMedia;
import com.colin.library.android.media.def.MediaType;

import java.io.File;


public class CameraAudio extends Camera<CameraAudio> implements IChooseMedia.Audio<CameraAudio> {

    public CameraAudio(@NonNull File file) {
        super(MediaType.AUDIO, file);
    }

    public CameraAudio(@NonNull Uri uri) {
        super(MediaType.AUDIO, uri);
    }


    @Override
    @NonNull
    public CameraAudio size(@IntRange(from = 0) long limit) {
        this.mLimitSize = limit;
        return this;
    }

    @Override
    @NonNull
    public CameraAudio duration(@IntRange(from = 0) long limit) {
        this.mLimitDuration = limit;
        return this;
    }
}
