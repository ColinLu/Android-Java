package com.colin.library.android.media.audio;

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
import com.colin.library.android.media.def.Filter;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.media.filter.IFilter;

import java.util.ArrayList;

/**
 * 作者： ColinLu
 * 时间： 2023-01-06 00:50
 * <p>
 * 描述： TODO
 */
public class ChooseMediaAudio<Returner, Result, Cancel> extends BaseChooseMedia<Returner, Result, Cancel>
        implements IChooseMedia.Audio<Returner>, IFilter.Audio<Returner> {
    public ChooseMediaAudio(@MediaType int mediaType, @IntRange(from = 1) int limit) {
        super(mediaType, limit);
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
    public Returner duration(@NonNull Filter<Long> filter) {
        this.mFilterDuration = filter;
        return (Returner) this;
    }

    @Override
    public void start(@NonNull Context context) {
        MediaActivity.sFilterSize = mFilterSize;
        MediaActivity.sFilterMimeType = mFilterMimeType;
        MediaActivity.sFilterDuration = mFilterDuration;

        final Intent intent = new Intent(context, MediaActivity.class);
        final Bundle bundle = new Bundle();
        bundle.putString(Constants.MEDIA_TITLE, mTitle);
        bundle.putInt(Constants.MEDIA_TYPE, mMediaType);
        bundle.putInt(Constants.MEDIA_LIST_COLUMN, mColumn);
        bundle.putInt(Constants.MEDIA_LIMIT_COUNT, mLimitCount);
        bundle.putLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
        bundle.putLong(Constants.MEDIA_LIMIT_DURATION, mLimitDuration);
        bundle.putBoolean(Constants.MEDIA_DISPLAY_CAMERA, mDisplayCamera);
        bundle.putBoolean(Constants.MEDIA_DISPLAY_INVALID, mDisplayInvalid);
        bundle.putParcelable(Constants.MEDIA_URI, mUri);
        bundle.putParcelableArrayList(Constants.MEDIA_SELECTED_LIST, (ArrayList<? extends Parcelable>) mList);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


}
