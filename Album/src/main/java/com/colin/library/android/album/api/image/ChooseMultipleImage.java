package com.colin.library.android.album.api.image;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.album.activity.AlbumActivity;
import com.colin.library.android.album.api.IMultiple;
import com.colin.library.android.album.def.AlbumFile;
import com.colin.library.android.album.def.MediaType;

import java.util.List;

public class ChooseMultipleImage extends Image<ChooseMultipleImage, List<AlbumFile>, String>
        implements IMultiple<ChooseMultipleImage, List<AlbumFile>> {

    public ChooseMultipleImage(@IntRange(from = 1) int limit) {
        super(MediaType.IMAGE, limit);
    }

    @NonNull
    @Override
    public ChooseMultipleImage selected(@Nullable List<AlbumFile> list) {
        this.mList = list;
        return this;
    }

    @Override
    public void start(@NonNull Context context) {
        AlbumActivity.sResultMultiple = mResult;
        AlbumActivity.sCancel = mCancel;
        super.start(context);
    }
}
