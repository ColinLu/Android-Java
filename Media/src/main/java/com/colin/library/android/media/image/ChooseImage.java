package com.colin.library.android.media.image;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.IChoose;


/**
 * 作者： ColinLu
 * 时间： 2023-01-05 01:35
 * <p>
 * 描述： 选择图片
 */
public class ChooseImage implements IChoose<ChooseImageSingle, ChooseImageMultiple> {

    public ChooseImage() {
    }

    @NonNull
    @Override
    public ChooseImageSingle single() {
        return new ChooseImageSingle();
    }

    @NonNull
    @Override
    public ChooseImageMultiple multiple(@IntRange(from = 1) int limit) {
        return new ChooseImageMultiple(limit);
    }

}