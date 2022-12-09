package com.colin.library.android.http.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:09
 * <p>
 * 描述： 字符串解析
 */
public class StringParseResponse implements IParseResponse<String> {
    @Nullable
    @Override
    @WorkerThread
    public String parse(@NonNull Response response) throws IOException {
        return response.body().string();
    }
}
