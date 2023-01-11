package com.colin.library.android.album.api.video;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.album.activity.AlbumActivity;
import com.colin.library.android.album.def.AlbumFile;
import com.colin.library.android.album.def.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2019-12-15 08:34
 * <p>
 * 描述： 多媒体获取 视频
 */
public final class ChooseSingleVideo extends Video<ChooseSingleVideo, AlbumFile, String> {

    public ChooseSingleVideo() {
        super(MediaType.VIDEO,1);
    }


    @Override
    public void start(@NonNull Context context) {
        AlbumActivity.sResultSingle = mResult;
        AlbumActivity.sCancel = mCancel;
        super.start(context);
    }

}
