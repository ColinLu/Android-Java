package com.colin.library.android.media.choose.audio;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.choose.IChoose;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:43
 * <p>
 * 描述： 选择录音文件
 */
public class ChooseAudio implements IChoose<ChooseAudioSingle, ChooseAudioMultiple> {
    public ChooseAudio() {
    }

    @NonNull
    @Override
    public ChooseAudioSingle single() {
        return new ChooseAudioSingle();
    }

    @NonNull
    @Override
    public ChooseAudioMultiple multiple(@IntRange(from = 1) int limit) {
        return new ChooseAudioMultiple(limit);
    }
}
