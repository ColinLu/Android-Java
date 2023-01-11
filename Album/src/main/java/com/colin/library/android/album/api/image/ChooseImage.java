package com.colin.library.android.album.api.image;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.album.api.IChoose;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 01:35
 * <p>
 * 描述： 选择图片
 */
public class ChooseImage implements IChoose<ChooseSingleImage, ChooseMultipleImage> {

    public ChooseImage() {
    }

    @NonNull
    @Override
    public ChooseSingleImage single() {
        return new ChooseSingleImage();
    }

    @NonNull
    @Override
    public ChooseMultipleImage multiple(@IntRange(from = 1) int limit) {
        return new ChooseMultipleImage(limit);
    }

}