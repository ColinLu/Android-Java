package com.colin.library.android.album.api.audio;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.album.activity.AlbumActivity;
import com.colin.library.android.album.api.IMultiple;
import com.colin.library.android.album.api.image.ChooseMultipleImage;
import com.colin.library.android.album.def.AlbumFile;
import com.colin.library.android.album.def.MediaType;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:44
 * <p>
 * 描述： TODO
 */
public class ChooseMultipleAudio extends Audio<ChooseMultipleImage, List<AlbumFile>, String>
        implements IMultiple<ChooseMultipleAudio, List<AlbumFile>> {

    public ChooseMultipleAudio(@IntRange(from = 1) int limit) {
        super(MediaType.VIDEO, limit);
    }

    @NonNull
    @Override
    public ChooseMultipleAudio selected(@Nullable List<AlbumFile> list) {
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
