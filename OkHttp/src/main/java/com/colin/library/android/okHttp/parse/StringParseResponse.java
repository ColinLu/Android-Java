package com.colin.library.android.okHttp.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import okhttp3.Response;
import okhttp3.ResponseBody;

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
    public String parse(@NonNull Response response) throws Throwable {
        final ResponseBody body = response.body();
        return body == null ? null : body.string();
    }
}
