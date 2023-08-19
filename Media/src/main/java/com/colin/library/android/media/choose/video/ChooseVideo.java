package com.colin.library.android.media.choose.video;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.choose.IChoose;


/**
 * 作者： ColinLu
 * 时间： 2023-01-05 01:36
 * <p>
 * 描述： 选择视频
 */
public class ChooseVideo implements IChoose<ChooseVideoSingle, ChooseVideoMultiple> {

    public ChooseVideo() {

    }

    @NonNull
    @Override
    public ChooseVideoSingle single() {
        return new ChooseVideoSingle();
    }

    @NonNull
    @Override
    public ChooseVideoMultiple multiple(@IntRange(from = 1) int limit) {
        return new ChooseVideoMultiple(limit);
    }
}