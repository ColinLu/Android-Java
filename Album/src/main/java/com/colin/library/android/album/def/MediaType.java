package com.colin.library.android.album.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 21:56
 * <p>
 * 描述： 多媒体类型
 */
@IntDef({MediaType.UNKNOWN, MediaType.IMAGE, MediaType.VIDEO, MediaType.AUDIO})
@Retention(RetentionPolicy.SOURCE)
public @interface MediaType {
    /*相机方式 拍照 录像*/
    int UNKNOWN = 0;
    int IMAGE = 1;
    int VIDEO = 2;
    int AUDIO = 3;
}
