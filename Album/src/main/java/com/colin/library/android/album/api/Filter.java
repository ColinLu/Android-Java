package com.colin.library.android.album.api;

/**
 * 作者： ColinLu
 * 时间： 2020-07-14 21:41
 * <p>
 * 描述： 筛选过滤
 */
public interface Filter<T> {

    /*返回true 正常 */
    boolean filter(T filter);

}