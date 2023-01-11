package com.colin.library.android.media.video;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.media.activity.MediaActivity;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;

/**
 * 作者： ColinLu
 * 时间： 2019-12-15 08:34
 * <p>
 * 描述： 多媒体获取 视频
 */
public final class ChooseVideoSingle extends ChooseMediaVideo<ChooseVideoSingle, MediaFile, String> {

    public ChooseVideoSingle() {
        super(MediaType.VIDEO, 1);
    }


    @Override
    public void start(@NonNull Context context) {
        MediaActivity.sResultSingle = mResult;
        MediaActivity.sCancel = mCancel;
        super.start(context);
    }

}
