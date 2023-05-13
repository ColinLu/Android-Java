package com.colin.library.android.http.def;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.action.IAction;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2023-05-13 18:09
 * <p>
 * 描述： 网络请求执行
 */
public interface IExecute {

    /*同步请求获取数据*/
    @Nullable
    Response execute();

    /*异步请求获取数据*/
    <Result> void execute(@NonNull IAction<Result> action);


}
