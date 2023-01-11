package com.colin.library.android.album.api.audio;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.api.IChoose;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:43
 * <p>
 * 描述： TODO
 */
public class ChooseAudio implements IChoose<ChooseSingleAudio, ChooseMultipleAudio> {
    public ChooseAudio() {
    }

    @NonNull
    @Override
    public ChooseSingleAudio single() {
        return new ChooseSingleAudio();
    }

    @NonNull
    @Override
    public ChooseMultipleAudio multiple(@IntRange(from = 1) int limit) {
        return new ChooseMultipleAudio(limit);
    }
}
