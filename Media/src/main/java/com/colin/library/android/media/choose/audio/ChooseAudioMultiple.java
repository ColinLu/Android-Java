package com.colin.library.android.media.choose.audio;

import android.content.Context;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.media.IMultiple;
import com.colin.library.android.media.activity.MediaActivity;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:44
 * <p>
 * 描述： 录音文件：多选
 */
public class ChooseAudioMultiple extends ChooseMediaAudio<ChooseAudioMultiple, List<MediaFile>, String>
        implements IMultiple<ChooseAudioMultiple, List<MediaFile>> {

    public ChooseAudioMultiple(@IntRange(from = 1) int limit) {
        super(MediaType.AUDIO, limit);
    }

    @NonNull
    @Override
    public ChooseAudioMultiple selected(@Nullable List<MediaFile> list) {
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
