package com.colin.library.android.album.api.audio;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.album.activity.AlbumActivity;
import com.colin.library.android.album.def.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:44
 * <p>
 * 描述： TODO
 */
public class ChooseSingleAudio extends Audio<ChooseSingleAudio, MeidaFile, String> {
    public ChooseSingleAudio() {
        super(MediaType.AUDIO, 1);
    }

    @Override
    public void start(@NonNull Context context) {
        AlbumActivity.sResultSingle = mResult;
        AlbumActivity.sCancel = mCancel;
        super.start(context);
    }
}
