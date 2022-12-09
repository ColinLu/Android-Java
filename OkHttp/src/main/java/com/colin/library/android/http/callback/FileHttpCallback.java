package com.colin.library.android.http.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.parse.FileParseResponse;
import com.colin.library.android.http.parse.IParseResponse;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:06
 * <p>
 * 描述： 下载文件，支持进度监听
 */
public class FileHttpCallback implements IHttpCallback<File> {
    @NonNull
    protected final IParseResponse<File> mParseResponse;

    public FileHttpCallback() {
        this(new FileParseResponse(null, null));
    }

    public FileHttpCallback(@Nullable String fileName) {
        this(new FileParseResponse(null, fileName));
    }


    public FileHttpCallback(@Nullable File dir, @Nullable String fileName) {
        this(new FileParseResponse(dir, fileName));
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
