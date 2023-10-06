package com.colin.library.android.http.policy;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.action.IAction;
import com.colin.library.android.http.def.IExecute;

/**
 * 作者： ColinLu
 * 时间： 2021-09-10 22:09
 * <p>
 * 描述： 请求动作定义
 */
public interface IPolicy extends IExecute {
    /*取消当前请求*/
    void cancel();

    /*是否已经取消 true，已经取消，false，没有取消*/
    boolean isCanceled();

    /*获取数据失败的回调*/
    @MainThread
    <Result> void fail(@NonNull IAction<Result> action, Throwable e);

    /*获取数据成功的回调*/
    @MainThread
    <Result> void success(@NonNull IAction<Result> action, @Nullable Result result);
}
