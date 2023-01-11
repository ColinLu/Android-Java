package com.colin.library.android.album.api.camera;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.api.video.IVideo;
import com.colin.library.android.album.def.Facing;
import com.colin.library.android.album.def.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2020-05-22 13:13
 * <p>
 * 描述： 相机 录视频
 */
public class CameraVideo extends Camera<CameraVideo> implements IVideo<CameraVideo> {

    public CameraVideo(@NonNull String path) {
        super(path, MediaType.VIDEO);
    }

    public CameraVideo(@NonNull Uri uri) {
        super(uri, MediaType.VIDEO);
    }

    @NonNull
    @Override
    public CameraVideo facing(@Facing int facing) {
        this.mFacing = facing;
        return this;
    }

    @Override
    @NonNull
    public CameraVideo quality(@IntRange(from = 0, to = 1)int limit) {
        this.mLimitQuality = limit;
        return this;
    }

    @Override
    @NonNull
    public CameraVideo size(long limit) {
        this.mLimitSize = limit;
        return this;
    }

    @Override
    @NonNull
    public CameraVideo duration(long limit) {
        this.mLimitDuration = limit;
        return this;
    }
}
