package com.colin.library.android.media.choose.camera;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.colin.library.android.media.base.IChooseMedia;
import com.colin.library.android.media.def.Facing;
import com.colin.library.android.media.def.MediaType;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2023-01-06 00:04
 * <p>
 * 描述： TODO
 */
public class CameraImage extends Camera<CameraImage> implements IChooseMedia.Image<CameraImage> {
    public CameraImage(@NonNull File file) {
        super(MediaType.IMAGE, file);
    }

    public CameraImage(@NonNull Uri uri) {
        super(MediaType.IMAGE, uri);
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
