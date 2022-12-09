package com.colin.library.android.http.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.http.progress.IProgress;

import java.io.IOException;

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
    default Result parse(@NonNull Response response) throws IOException {
        return null;
    }
}
