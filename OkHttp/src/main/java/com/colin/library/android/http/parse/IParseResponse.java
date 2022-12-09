package com.colin.library.android.http.parse;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.http.bean.HttpException;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 12:28
 * <p>
 * 描述： Response 数据解析 返回需要字段 子线程实现
 */
public interface IParseResponse<Result> {


    @Nullable
    @WorkerThread
    default Result parse(@NonNull Response response) throws IOException {
        return null;
    }

    /*网络请求 进度提示*/
    default void progress(float total, float progress) {
    }
}
