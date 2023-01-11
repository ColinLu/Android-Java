package com.colin.library.android.media.preview;

import androidx.annotation.NonNull;

import com.colin.library.android.media.IPreview;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 02:52
 * <p>
 * 描述： TODO
 */
public class ChoosePreview implements IPreview<PreviewImage, PreviewAudio, PreviewVideo, PreviewMedia> {
    @NonNull
    @Override
    public PreviewImage image() {
        return new PreviewImage();
    }

    @NonNull
    @Override
    public PreviewAudio audio() {
        return new PreviewAudio();
    }

    @NonNull
    @Override
    public PreviewVideo video() {
        return new PreviewVideo();
    }

    @NonNull
    @Override
    public PreviewMedia media() {
        return new PreviewMedia();
    }
}
