package com.colin.library.android.album.api.media;

import androidx.annotation.NonNull;

import com.colin.library.android.album.api.IMedia;
import com.colin.library.android.album.api.audio.ChooseAudio;
import com.colin.library.android.album.api.image.ChooseImage;
import com.colin.library.android.album.api.video.ChooseVideo;

/**
 * 作者： ColinLu
 * 时间： 2023-01-06 00:19
 * <p>
 * 描述： TODO
 */
public class ChooseMedia implements IMedia<ChooseImage, ChooseAudio, ChooseVideo> {
    public ChooseMedia() {
    }

    @NonNull
    @Override
    public ChooseImage image() {
        return new ChooseImage();
    }

    @NonNull
    @Override
    public ChooseAudio audio() {
        return new ChooseAudio();
    }

    @NonNull
    @Override
    public ChooseVideo video() {
        return new ChooseVideo();
    }
}
