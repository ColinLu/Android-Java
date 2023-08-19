package com.colin.library.android.media.choose.camera;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.base.IChooseMedia;
import com.colin.library.android.media.def.Facing;
import com.colin.library.android.media.def.MediaType;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2020-05-22 13:13
 * <p>
 * 描述： 相机 录视频
 */
public class CameraVideo extends Camera<CameraVideo> implements IChooseMedia.Video<CameraVideo> {

    public CameraVideo(@NonNull File file) {
        super(MediaType.VIDEO, file);
    }

    public CameraVideo(@NonNull Uri uri) {
        super(MediaType.VIDEO, uri);
    }

    @NonNull
    @Override
    public CameraVideo facing(@Facing int facing) {
        this.mFacing = facing;
        return this;
    }

    @Override
    @NonNull
    public CameraVideo quality(@IntRange(from = 0, to = 1) int limit) {
        this.mLimitQuality = limit;
        return this;
    }

    @Override
    @NonNull
    public CameraVideo size(@IntRange(from = 0) long limit) {
        this.mLimitSize = limit;
        return this;
    }

    @Override
    @NonNull
    public CameraVideo duration(@IntRange(from = 0) long limit) {
        this.mLimitDuration = limit;
        return this;
    }
}
