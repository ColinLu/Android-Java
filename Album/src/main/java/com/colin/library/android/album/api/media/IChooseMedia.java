package com.colin.library.android.album.api.media;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2020-05-22 07:08
 * <p>
 * 描述： 相册中的多媒体选择
 */
public interface IChooseMedia<Returner> {
    /*新建文件的 Uri*/
    @NonNull
    Returner uri(@NonNull Uri uri);

    /*设置列表展示列数*/
    @NonNull
    Returner column(@IntRange(from = 2, to = 5) int column);

    /*设置是否显示摄像头*/
    @NonNull
    Returner camera(boolean display);

    /*是否显示筛选失败的多媒体文件*/
    @NonNull
    Returner invalid(boolean display);
}
