package com.colin.library.android.album.api.filter;

import androidx.annotation.NonNull;

import com.colin.library.android.album.api.Filter;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 23:00
 * <p>
 * 描述： 多媒体筛选
 */
public interface IFilterMedia<Returner> {

    @NonNull
    Returner mimeType(@NonNull Filter<String> filter);
}
