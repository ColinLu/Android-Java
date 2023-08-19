package com.colin.library.android.media.choose.image;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.activity.MediaActivity;
import com.colin.library.android.media.base.BaseChooseMedia;
import com.colin.library.android.media.base.IChooseMedia;
import com.colin.library.android.media.def.Constants;
import com.colin.library.android.media.def.Facing;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.media.filter.IFilter;
import com.colin.library.android.utils.IntentUtil;

import java.util.ArrayList;

/**
 * 作者： ColinLu
 * 时间： 2019-12-15 08:34
 * <p>
 * 描述： 多媒体获取 图片
 */
public abstract class ChooseMediaImage<Returner, Result, Cancel> extends BaseChooseMedia<Returner, Result, Cancel> implements
        IChooseMedia.Image<Returner>, IFilter.Image<Returner> {

    public ChooseMediaImage(@MediaType int mediaType, @IntRange(from = 1) int limit) {
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

    @Override
    public void start(@NonNull Context context) {
        MediaActivity.sFilterSize = mFilterSize;
        MediaActivity.sFilterMimeType = mFilterMimeType;
        final Intent intent = new Intent(context, MediaActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putCharSequence(Constants.MEDIA_TITLE, mTitle);
        bundle.putInt(Constants.MEDIA_TYPE, mMediaType);
        bundle.putBoolean(Constants.MEDIA_NEED_CROP, mNeedCrop);
        bundle.putBoolean(Constants.MEDIA_DISPLAY_CAMERA, mDisplayCamera);
        bundle.putBoolean(Constants.MEDIA_DISPLAY_INVALID, mDisplayInvalid);
        bundle.putBoolean(Constants.MEDIA_MULTIPLE_MODE, mMultiple);
        bundle.putInt(Constants.MEDIA_CAMERA_FACING, mFacing);
        bundle.putInt(Constants.MEDIA_LIST_COLUMN, mColumn);
        bundle.putInt(Constants.MEDIA_LIMIT_COUNT, mLimitCount);
        bundle.putLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
        bundle.putParcelable(Constants.MEDIA_URI, mUri);
        bundle.putParcelableArrayList(Constants.MEDIA_SELECTED_LIST, (ArrayList<? extends Parcelable>) mList);
        intent.putExtras(bundle);
        IntentUtil.start(context, intent);
    }

}
