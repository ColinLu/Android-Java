package com.colin.library.android.media.video;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.media.IMultiple;
import com.colin.library.android.media.activity.MediaActivity;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;

import java.util.List;

public final class ChooseVideoMultiple extends ChooseMediaVideo<ChooseVideoMultiple, List<MediaFile>, String> implements IMultiple<ChooseVideoMultiple, List<MediaFile>> {

    public ChooseVideoMultiple(@IntRange(from = 1) int limit) {
        super(MediaType.VIDEO, limit);
    }

    @NonNull
    @Override
    public ChooseVideoMultiple selected(@Nullable List<MediaFile> list) {
        this.mList = list;
        return this;
    }

    @Override
    public void start(@NonNull Context context) {
        MediaActivity.sResultMultiple = mResult;
        MediaActivity.sCancel = mCancel;
        super.start(context);
    }
}
