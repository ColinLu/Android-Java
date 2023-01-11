package com.colin.library.android.album.api.media;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.api.Filter;
import com.colin.library.android.album.api.base.Base;
import com.colin.library.android.album.api.filter.IFilterMedia;
import com.colin.library.android.album.def.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2023-01-06 00:29
 * <p>
 * 描述： TODO
 */
public abstract class ChooseFilterMedia<Returner, Result, Cancel> extends Base<Returner, Result, Cancel>
        implements IChooseMedia<Returner>, IFilterMedia<Returner> {
    public ChooseFilterMedia(@MediaType int mediaType, @IntRange(from = 1) int limit) {
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
    public Returner mimeType(@NonNull Filter<String> filter) {
        this.mFilterMimeType = filter;
        return (Returner) this;
    }

}
