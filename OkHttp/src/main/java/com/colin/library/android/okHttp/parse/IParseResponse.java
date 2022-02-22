package com.colin.library.android.okHttp.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.okHttp.bean.HttpException;
import com.colin.library.android.okHttp.progress.IProgress;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 12:28
 * <p>
 * 描述： Response 数据解析 返回需要字段 子线程实现
 */
public interface IParseResponse<Result> extends IProgress {
    @Nullable
    @WorkerThread
    default Result parse(@NonNull Response response) throws Throwable {
        return (Result) null;
    }
}
