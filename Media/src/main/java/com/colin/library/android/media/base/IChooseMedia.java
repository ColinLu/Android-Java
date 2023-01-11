package com.colin.library.android.media.base;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import com.colin.library.android.media.def.Facing;

/**
 * 作者： ColinLu
 * 时间： 2020-05-22 07:08
 * <p>
 * 描述： 选择多媒体
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

    ///////////////////////////////////////////////////////////////////////////
    // 选择多媒体：图片
    ///////////////////////////////////////////////////////////////////////////
    interface Image<Returner> {
        /*拍照使用前置 or 后置*/
        @NonNull
        Returner facing(@Facing int facing);

        /*是否需要剪切*/
        @NonNull
        Returner crop(boolean need);

    }

    ///////////////////////////////////////////////////////////////////////////
    // 选择多媒体：音频
    ///////////////////////////////////////////////////////////////////////////
    interface Audio<Returner> {

        /*多媒体音频大小*/
        @NonNull
        Returner size(@IntRange(from = 0) long limit);

        /*多媒体音频时长*/
        @NonNull
        Returner duration(@IntRange(from = 0) long limit);

    }

    ///////////////////////////////////////////////////////////////////////////
    // 选择多媒体：视频
    ///////////////////////////////////////////////////////////////////////////
    interface Video<Returner> {
        @NonNull
        Returner facing(@Facing int facing);

        /*多媒体视频质量*/
        @NonNull
        Returner quality(@IntRange(from = 0, to = 1) int limit);

        /*多媒体视频大小*/
        @NonNull
        Returner size(@IntRange(from = 0) long limit);

        /*多媒体视频时长*/
        @NonNull
        Returner duration(@IntRange(from = 0) long limit);

    }
}
