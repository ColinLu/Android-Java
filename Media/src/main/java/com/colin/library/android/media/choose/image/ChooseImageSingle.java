package com.colin.library.android.media.choose.image;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.media.activity.MediaActivity;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;


/**
 * 作者： ColinLu
 * 时间： 2020-05-22 09:57
 * <p>
 * 描述： 多媒体获取 图片 单选
 */
public final class ChooseImageSingle extends ChooseMediaImage<ChooseImageSingle, MediaFile, String> {


    public ChooseImageSingle() {
        super(MediaType.IMAGE, 1);
    }

    @Override
    public void start(@NonNull Context context) {
        MediaActivity.sResultSingle = mResult;
        MediaActivity.sCancel = mCancel;
        super.start(context);
    }


}
