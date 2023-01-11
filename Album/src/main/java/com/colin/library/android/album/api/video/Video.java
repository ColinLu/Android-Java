package com.colin.library.android.album.api.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.activity.AlbumActivity;
import com.colin.library.android.album.api.Filter;
import com.colin.library.android.album.api.media.ChooseFilterMedia;
import com.colin.library.android.album.api.filter.IFilterVideo;
import com.colin.library.android.album.def.Constants;
import com.colin.library.android.album.def.Facing;
import com.colin.library.android.album.def.MediaType;

import java.util.ArrayList;


/**
 * 作者： ColinLu
 * 时间： 2019-12-15 08:34
 * <p>
 * 描述： 多媒体获取 视频
 */
public class Video<Returner, Result, Cancel> extends ChooseFilterMedia<Returner, Result, Cancel>
        implements IVideo<Returner>, IFilterVideo<Returner> {

    public Video(@MediaType int mediaType, @IntRange(from = 1) int limit) {
        super(mediaType, limit);
    }

    @NonNull
    @Override
    public Returner facing(@Facing int facing) {
        this.mFacing = facing;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner quality(@IntRange(from = 0) int limit) {
        this.mLimitQuality = limit;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner size(@IntRange(from = 0) long limit) {
        this.mLimitSize = limit;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner duration(@IntRange(from = 0) long limit) {
        this.mLimitDuration = limit;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner size(@NonNull Filter<Long> filter) {
        this.mFilterSize = filter;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner duration(@NonNull Filter<Long> filter) {
        this.mFilterDuration = filter;
        return (Returner) this;
    }

    @Override
    public void start(@NonNull Context context) {

        AlbumActivity.sFilterSize = mFilterSize;
        AlbumActivity.sFilterMime = mFilterMimeType;
        AlbumActivity.sFilterDuration = mFilterDuration;

        final Intent intent = new Intent(context, AlbumActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.ALBUM_TITLE, mTitle);
        bundle.putInt(Constants.ALBUM_MEDIA_TYPE, mMediaType);
        bundle.putBoolean(Constants.ALBUM_NEED_CROP, mNeedCrop);
        bundle.putBoolean(Constants.ALBUM_DISPLAY_CAMERA, mDisplayCamera);
        bundle.putBoolean(Constants.ALBUM_DISPLAY_INVALID, mDisplayInvalid);
        bundle.putInt(Constants.ALBUM_CAMERA_FACING, mFacing);
        bundle.putInt(Constants.ALBUM_LIST_COLUMN, mColumn);
        bundle.putInt(Constants.ALBUM_LIMIT_COUNT, mLimitCount);
        bundle.putLong(Constants.ALBUM_LIMIT_SIZE, mLimitSize);
        bundle.putLong(Constants.ALBUM_LIMIT_DURATION, mLimitDuration);
        bundle.putParcelable(Constants.ALBUM_URI, mUri);
        bundle.putParcelableArrayList(Constants.ALBUM_SELECTED_LIST, (ArrayList<? extends Parcelable>) mList);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


}
