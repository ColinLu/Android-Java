package com.colin.library.android.media.filter;

import androidx.annotation.NonNull;

import com.colin.library.android.media.def.Filter;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:00
 * <p>
 * 描述： 多媒体筛选
 */
public interface IFilter<Returner> {
    /*筛选多媒体：文件大小*/
    @NonNull
    Returner size(@NonNull Filter<Long> filter);

    /*筛选多媒体：文件类型*/
    @NonNull
    Returner mimeType(@NonNull Filter<String> filter);

    ///////////////////////////////////////////////////////////////////////////
    // 筛选图片
    ///////////////////////////////////////////////////////////////////////////
    interface Image<Returner> extends IFilter<Returner> {

    }

    ///////////////////////////////////////////////////////////////////////////
    // 筛选音频
    ///////////////////////////////////////////////////////////////////////////
    interface Audio<Returner> extends IFilter<Returner> {
        /*筛选音频：时长*/
        @NonNull
        Returner duration(@NonNull Filter<Long> filter);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 筛选视频
    ///////////////////////////////////////////////////////////////////////////
    interface Video<Returner> extends IFilter<Returner> {
        /*筛选视频：时长*/
        @NonNull
        Returner duration(@NonNull Filter<Long> filter);
    }

}
