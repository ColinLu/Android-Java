package com.colin.library.android.http.action;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.parse.IParse;
import com.colin.library.android.utils.LogUtil;

import okhttp3.Request;

/**
 * 作者： ColinLu
 * 时间： 2023-04-22 10:13
 * <p>
 * 描述： 网络请求动作定义
 */
public interface IAction<Result> extends IParse<Result> {

    /*网络请求开始*/
    @MainThread
    default void start(@NonNull Request request) {
        LogUtil.d(request.toString());
    }

    /*网络请求失败*/
    @MainThread
    default void fail(@NonNull Throwable e) {
        LogUtil.e(e);
    }

    /*网络请求成功*/
    @MainThread
    default void success(@Nullable Result result) {
    }

    /*网络请求结束*/
    @MainThread
    default void finish(@NonNull Request request) {
        LogUtil.d(request.toString());
    }
}
