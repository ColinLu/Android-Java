package com.colin.library.android.album.api;


import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2019-12-15 17:46
 * <p>
 * 描述： 操作接口回调
 */
public interface Action<T> {
    void onAction(@NonNull T t);
}
