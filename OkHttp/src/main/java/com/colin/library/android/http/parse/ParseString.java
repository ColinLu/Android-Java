package com.colin.library.android.http.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2023-04-22 13:58
 * <p>
 * 描述： Result->String
 */
public class ParseString implements IParse<String> {
    @Nullable
    @Override
    @WorkerThread
    public String parse(@NonNull Response response) throws IOException {
        return response.body().string();
    }
}
