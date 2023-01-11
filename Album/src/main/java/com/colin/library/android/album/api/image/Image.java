package com.colin.library.android.album.api.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.activity.AlbumActivity;
import com.colin.library.android.album.api.Filter;
import com.colin.library.android.album.api.media.ChooseFilterMedia;
import com.colin.library.android.album.api.filter.IFilterImage;
import com.colin.library.android.album.def.Constants;
import com.colin.library.android.album.def.Facing;
import com.colin.library.android.album.def.MediaType;

import java.util.ArrayList;

/**
 * 作者： ColinLu
 * 时间： 2019-12-15 08:34
 * <p>
 * 描述： 多媒体获取 图片
 */
public abstract class Image<Returner, Result, Cancel> extends ChooseFilterMedia<Returner, Result, Cancel>
        implements IImage<Returner>, IFilterImage<Returner> {

    public Image(@MediaType int mediaType, @IntRange(from = 1) int limit) {
        super(mediaType, limit);
    }

    @NonNull
    @Override
    public Returner crop(boolean need) {
        this.mNeedCrop = need;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner facing(@Facing int facing) {
        this.mFacing = facing;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner size(@NonNull Filter<Long> filter) {
        this.mFilterSize = filter;
        return (Returner) this;
    }

    @Override
    public void start(@NonNull Context context) {

        AlbumActivity.sFilterSize = mFilterSize;
        AlbumActivity.sFilterMime = mFilterMimeType;

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
        bundle.putParcelable(Constants.ALBUM_URI, mUri);
        bundle.putParcelableArrayList(Constants.ALBUM_SELECTED_LIST, (ArrayList<? extends Parcelable>) mList);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

}
