package com.colin.library.android.album.api.camera;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.colin.library.android.album.api.image.IImage;
import com.colin.library.android.album.def.Facing;
import com.colin.library.android.album.def.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2023-01-06 00:04
 * <p>
 * 描述： TODO
 */
public class CameraImage extends Camera<CameraImage> implements IImage<CameraImage> {
    public CameraImage(@NonNull String path) {
        super(path, MediaType.IMAGE);
    }

    public CameraImage(@NonNull Uri uri) {
        super(uri, MediaType.IMAGE);
    }

    @NonNull
    public CameraImage crop(boolean need) {
        this.mNeedCrop = need;
        return this;
    }

    @NonNull
    @Override
    public CameraImage facing(@Facing int facing) {
        this.mFacing = facing;
        return this;
    }
}
