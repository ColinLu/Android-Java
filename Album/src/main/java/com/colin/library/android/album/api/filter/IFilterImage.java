package com.colin.library.android.album.api.filter;

import androidx.annotation.NonNull;

import com.colin.library.android.album.api.Filter;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 22:59
 * <p>
 * 描述： 过滤多媒体:图片
 */
public interface IFilterImage<Returner> extends IFilterMedia<Returner> {
    @NonNull
    Returner size(@NonNull Filter<Long> filter);
}
