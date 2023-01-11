package com.colin.library.android.album.api.camera;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.colin.library.android.album.activity.CameraActivity;
import com.colin.library.android.album.api.base.Base;
import com.colin.library.android.album.def.AlbumFile;
import com.colin.library.android.album.def.Constants;
import com.colin.library.android.album.def.MediaType;


/**
 * 作者： ColinLu
 * 时间： 2020-05-22 00:10
 * <p>
 * 描述： 相机操作
 */
public abstract class Camera<Returner> extends Base<Returner, AlbumFile, String> {
    protected final Uri mUri;               //返回地址
    protected int mFacing;                  //前置 or 后置
    protected boolean mNeedCrop;            //是否剪切

    public Camera(@NonNull String path, @MediaType int mediaType) {
        super(mediaType, 1);
        this.mUri = Uri.parse(path);
    }

    public Camera(@NonNull Uri uri, @MediaType int mediaType) {
        super(mediaType, 1);
        this.mUri = uri;
    }

    @Override
    public void start(@NonNull Context context) {
        CameraActivity.sResult = mResult;
        CameraActivity.sCancel = mCancel;
        final Intent intent = new Intent(context, CameraActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putInt(Constants.ALBUM_MEDIA_TYPE, mMediaType);
        bundle.putString(Constants.ALBUM_TITLE, mTitle);
        bundle.putInt(Constants.ALBUM_CAMERA_FACING, mFacing);
        bundle.putParcelable(Constants.ALBUM_URI, mUri);
        bundle.putBoolean(Constants.ALBUM_NEED_CROP, mNeedCrop);
        bundle.putInt(Constants.ALBUM_LIMIT_QUALITY, mLimitQuality);
        bundle.putLong(Constants.ALBUM_LIMIT_SIZE, mLimitSize);
        bundle.putLong(Constants.ALBUM_LIMIT_DURATION, mLimitDuration);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
