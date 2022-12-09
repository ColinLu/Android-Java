package com.colin.library.android.http.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.bean.HttpException;
import com.colin.library.android.http.parse.FileParseResponse;
import com.colin.library.android.http.parse.IParseResponse;
import com.colin.library.android.http.progress.IProgress;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:06
 * <p>
 * 描述： 下载文件，支持进度监听
 */
public final class FileHttpCallback implements IHttpCallback<File> {
    @NonNull
    protected final IParseResponse<File> mParseResponse;

    public FileHttpCallback() {
        this(null, null, null);
    }

    public FileHttpCallback(@Nullable String fileName) {
        this(null, fileName, null);
    }


    public FileHttpCallback(@Nullable File dir, @Nullable String fileName) {
        this(dir, fileName, null);
    }

    public FileHttpCallback(@Nullable File dir, @Nullable String fileName, @Nullable IProgress progress) {
        this(new FileParseResponse(dir, fileName, progress));
    }

    public FileHttpCallback(@NonNull IParseResponse<File> parseResponse) {
        this.mParseResponse = parseResponse;
    }

    @Nullable
    @Override
    public File parse(@NonNull Response response) throws IOException {
        return this.mParseResponse.parse(response);
    }
}
