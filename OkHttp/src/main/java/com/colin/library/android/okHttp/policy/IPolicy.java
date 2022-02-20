package com.colin.library.android.okHttp.policy;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.bean.HttpException;
import com.colin.library.android.okHttp.callback.IHttpCallback;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2021-09-10 22:09
 * <p>
 * 描述： TODO
 */
public interface IPolicy<Result> {
    /*同步请求获取数据*/
    @Nullable
    Response execute();

    /*异步请求获取数据*/
    void execute(@NonNull IHttpCallback<Result> callback);

    /*当前请求是否已经执行 true，已经执行， false，没有执行*/
    boolean isExecuted();

    /*是否已经取消 true，已经取消，false，没有取消*/
    boolean isCanceled();

    /*取消当前请求*/
    void cancel();

    /*获取数据成功的回调*/
    @MainThread
    void success(@NonNull IHttpCallback<Result> callback, @Nullable Result result);

    /*获取数据失败的回调*/
    @MainThread
    void fail(@NonNull IHttpCallback<Result> callback, @NonNull HttpException exception);


}
