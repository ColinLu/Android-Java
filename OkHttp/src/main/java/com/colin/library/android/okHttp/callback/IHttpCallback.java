package com.colin.library.android.okHttp.callback;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.bean.HttpException;
import com.colin.library.android.okHttp.parse.IParseResponse;
import com.colin.library.android.utils.LogUtil;

import okhttp3.Request;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:42
 * <p>
 * 描述： TODO
 */
public interface IHttpCallback<Result> extends IParseResponse<Result> {

    /*网络请求开始*/
    @MainThread
    default void start(@NonNull Request request) {
    }

    /*网络请求失败*/
    @MainThread
    default void fail(@NonNull HttpException exception) {
        LogUtil.e(exception.toString());
    }

    /*网络请求成功*/
    @MainThread
    default void success(@Nullable Result result) {
    }

    /*网络请求结束*/
    @MainThread
    default void finish(boolean success) {

    }
}
