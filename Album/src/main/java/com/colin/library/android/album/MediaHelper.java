package com.colin.library.android.album;

import androidx.annotation.NonNull;

import com.colin.library.android.Utils;
import com.colin.library.android.album.api.audio.ChooseAudio;
import com.colin.library.android.album.api.audio.ChooseMultipleAudio;
import com.colin.library.android.album.api.audio.ChooseSingleAudio;
import com.colin.library.android.album.api.camera.CameraAudio;
import com.colin.library.android.album.api.camera.CameraImage;
import com.colin.library.android.album.api.camera.CameraVideo;
import com.colin.library.android.album.api.camera.ChooseCamera;
import com.colin.library.android.album.api.image.ChooseImage;
import com.colin.library.android.album.api.image.ChooseMultipleImage;
import com.colin.library.android.album.api.image.ChooseSingleImage;
import com.colin.library.android.album.api.media.ChooseMedia;
import com.colin.library.android.album.api.video.ChooseMultipleVideo;
import com.colin.library.android.album.api.video.ChooseSingleVideo;
import com.colin.library.android.album.api.video.ChooseVideo;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 21:20
 * <p>
 * 描述： TODO
 */
public class MediaHelper {
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
    public MediaConfig getAlbumConfig() {
        return Utils.notNull(mMediaConfig, "AlbumConfig init first !");
    }

    public IChoose<ChooseSingleImage, ChooseMultipleImage> image() {
        return new ChooseImage();
    }

    public IChoose<ChooseSingleAudio, ChooseMultipleAudio> audio() {
        return new ChooseAudio();
    }

    public IChoose<ChooseSingleVideo, ChooseMultipleVideo> video() {
        return new ChooseVideo();
    }

    public IMedia<ChooseImage, ChooseAudio, ChooseVideo> media() {
        return new ChooseMedia();
    }

    public ICamera<CameraImage, CameraAudio, CameraVideo> camera() {
        return new ChooseCamera();
    }


}
