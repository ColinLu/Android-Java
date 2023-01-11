package com.colin.library.android.media.camera;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.colin.library.android.media.activity.CameraActivity;
import com.colin.library.android.media.base.Base;
import com.colin.library.android.media.def.Constants;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.provider.AppFileProvider;

import java.io.File;


/**
 * 作者： ColinLu
 * 时间： 2020-05-22 00:10
 * <p>
 * 描述： 相机操作
 */
public abstract class Camera<Returner> extends Base<Returner, MediaFile, String> {
    public Camera(@MediaType int mediaType, @NonNull File file) {
        super(mediaType, AppFileProvider.getUri(file), 1);
    }

    public Camera(@MediaType int mediaType, @NonNull Uri uri) {
        super(mediaType, uri, 1);
    }

    @Override
    public void start(@NonNull Context context) {
        CameraActivity.sResult = mResult;
        CameraActivity.sCancel = mCancel;
        final Intent intent = new Intent(context, CameraActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.MEDIA_TITLE, mTitle);
        bundle.putParcelable(Constants.MEDIA_URI, mUri);
        bundle.putInt(Constants.MEDIA_TYPE, mMediaType);
        bundle.putInt(Constants.MEDIA_CAMERA_FACING, mFacing);
        bundle.putBoolean(Constants.MEDIA_NEED_CROP, mNeedCrop);
        bundle.putInt(Constants.MEDIA_LIMIT_QUALITY, mLimitQuality);
        bundle.putLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
        bundle.putLong(Constants.MEDIA_LIMIT_DURATION, mLimitDuration);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
