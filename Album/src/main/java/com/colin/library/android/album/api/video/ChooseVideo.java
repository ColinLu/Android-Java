package com.colin.library.android.album.api.video;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.api.IChoose;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 01:36
 * <p>
 * 描述： 选择视频
 */
public class ChooseVideo implements IChoose<ChooseSingleVideo, ChooseMultipleVideo> {

    public ChooseVideo() {

    }

    @NonNull
    @Override
    public ChooseSingleVideo single() {
        return new ChooseSingleVideo();
    }

    @NonNull
    @Override
    public ChooseMultipleVideo multiple(@IntRange(from = 1) int limit) {
        return new ChooseMultipleVideo(limit);
    }
}