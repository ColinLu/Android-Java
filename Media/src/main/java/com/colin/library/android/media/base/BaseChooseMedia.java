package com.colin.library.android.media.base;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.media.def.Filter;
import com.colin.library.android.media.filter.IFilter;

/**
 * 作者： ColinLu
 * 时间： 2023-01-06 00:29
 * <p>
 * 描述： 选择多媒体 基类
 */
public abstract class BaseChooseMedia<Returner, Result, Cancel> extends Base<Returner, Result, Cancel>
        implements IChooseMedia<Returner>, IFilter<Returner> {
    public BaseChooseMedia(@MediaType int mediaType, @IntRange(from = 1) int limit) {
        super(mediaType, limit);
    }

    @NonNull
    @Override
    public Returner uri(@NonNull Uri uri) {
        this.mUri = uri;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner column(@IntRange(from = 2, to = 5) int column) {
        this.mColumn = column;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner camera(boolean display) {
        this.mDisplayCamera = display;
        return (Returner) this;
    }

    @NonNull
    @Override
    public Returner invalid(boolean display) {
        this.mDisplayInvalid = display;
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
    public Returner mimeType(@NonNull Filter<String> filter) {
        this.mFilterMimeType = filter;
        return (Returner) this;
    }

}
