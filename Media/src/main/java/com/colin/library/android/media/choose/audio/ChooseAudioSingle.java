package com.colin.library.android.media.choose.audio;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.media.activity.MediaActivity;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;


/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:44
 * <p>
 * 描述： 录音文件：单选
 */
public class ChooseAudioSingle extends ChooseMediaAudio<ChooseAudioSingle, MediaFile, String> {
    public ChooseAudioSingle() {
        super(MediaType.AUDIO, 1);
    }

    @Override
    public void start(@NonNull Context context) {
        MediaActivity.sResultSingle = mResult;
        MediaActivity.sCancel = mCancel;
        super.start(context);
    }
}
