package com.colin.library.android.media;

import androidx.annotation.NonNull;

import com.colin.library.android.media.audio.ChooseAudio;
import com.colin.library.android.media.camera.ChooseCamera;
import com.colin.library.android.media.image.ChooseImage;
import com.colin.library.android.media.preview.ChoosePreview;
import com.colin.library.android.media.video.ChooseVideo;


/**
 * 作者： ColinLu
 * 时间： 2023-01-06 00:19
 * <p>
 * 描述： 多媒体类型选择
 */
public class ChooseMedia implements IMedia<ChooseImage, ChooseAudio, ChooseVideo, ChooseCamera, ChoosePreview> {
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

    @NonNull
    @Override
    public ChooseCamera camera() {
        return new ChooseCamera();
    }

    @NonNull
    @Override
    public ChoosePreview preview() {
        return new ChoosePreview();
    }
}
