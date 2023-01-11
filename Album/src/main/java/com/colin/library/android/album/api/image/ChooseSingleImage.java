package com.colin.library.android.album.api.image;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.album.activity.AlbumActivity;
import com.colin.library.android.album.def.AlbumFile;
import com.colin.library.android.album.def.MediaType;


/**
 * 作者： ColinLu
 * 时间： 2020-05-22 09:57
 * <p>
 * 描述： 多媒体获取 图片 单选
 */
public final class ChooseSingleImage extends Image<ChooseSingleImage, AlbumFile, String> {


    public ChooseSingleImage() {
        super(MediaType.IMAGE, 1);
    }

    @Override
    public void start(@NonNull Context context) {
        AlbumActivity.sResultSingle = mResult;
        AlbumActivity.sCancel = mCancel;
        super.start(context);
    }


}
