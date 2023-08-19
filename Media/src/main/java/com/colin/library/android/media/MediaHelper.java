package com.colin.library.android.media;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.colin.library.android.Utils;
import com.colin.library.android.media.choose.audio.ChooseAudio;
import com.colin.library.android.media.choose.audio.ChooseAudioMultiple;
import com.colin.library.android.media.choose.audio.ChooseAudioSingle;
import com.colin.library.android.media.choose.camera.CameraAudio;
import com.colin.library.android.media.choose.camera.CameraImage;
import com.colin.library.android.media.choose.camera.CameraVideo;
import com.colin.library.android.media.choose.camera.ChooseCamera;
import com.colin.library.android.media.choose.IChoose;
import com.colin.library.android.media.def.MediaConfig;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.choose.image.ChooseImage;
import com.colin.library.android.media.choose.image.ChooseImageMultiple;
import com.colin.library.android.media.choose.image.ChooseImageSingle;
import com.colin.library.android.media.preview.ChoosePreview;
import com.colin.library.android.media.preview.PreviewAudio;
import com.colin.library.android.media.preview.PreviewImage;
import com.colin.library.android.media.preview.PreviewMedia;
import com.colin.library.android.media.preview.PreviewVideo;
import com.colin.library.android.media.choose.video.ChooseVideoMultiple;
import com.colin.library.android.media.choose.video.ChooseVideoSingle;
import com.colin.library.android.media.choose.video.ChooseVideo;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 01:45
 * <p>
 * 描述： 选择多媒体 入口
 */
public final class MediaHelper {
    private static volatile MediaHelper sHelper;
    private MediaConfig mMediaConfig;

    private MediaHelper() {
    }

    public static MediaHelper getInstance() {
        if (sHelper == null) {
            synchronized (MediaHelper.class) {
                if (sHelper == null) sHelper = new MediaHelper();
            }
        }
        return sHelper;
    }

    public void init(@NonNull MediaConfig config) {
        this.mMediaConfig = config;
    }

    @NonNull
    public MediaConfig getMediaConfig() {
        return Utils.notNull(mMediaConfig, "MediaConfig init first !");
    }

    protected void load(@NonNull ImageView imageView, @NonNull MediaFile mediaFile) {
        getMediaConfig().getMediaLoader().load(imageView, mediaFile);
    }

    public IChoose<ChooseImageSingle, ChooseImageMultiple> image() {
        return new ChooseImage();
    }

    public IMedia<ChooseImage, ChooseAudio, ChooseVideo, ChooseCamera, ChoosePreview> media() {
        return new ChooseMedia();
    }

    public IChoose<ChooseAudioSingle, ChooseAudioMultiple> audio() {
        return new ChooseAudio();
    }

    public IChoose<ChooseVideoSingle, ChooseVideoMultiple> video() {
        return new ChooseVideo();
    }

    public ICamera<CameraImage, CameraAudio, CameraVideo> camera() {
        return new ChooseCamera();
    }

    public IPreview<PreviewImage, PreviewAudio, PreviewVideo, PreviewMedia> preview() {
        return new ChoosePreview();
    }

    public File create() {
        return null;
    }
}
