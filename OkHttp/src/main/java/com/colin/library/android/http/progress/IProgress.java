package com.colin.library.android.http.progress;

import androidx.annotation.MainThread;

/**
 * 作者： ColinLu
 * 时间： 2022-12-10 21:55
 * <p>
 * 描述： 进度处理
 */
public interface IProgress {
    /*网络请求 进度提示*/
    @MainThread
    default void progress(long total, long progress) {
    }
}
