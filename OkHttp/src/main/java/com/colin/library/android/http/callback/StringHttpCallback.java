package com.colin.library.android.http.callback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.bean.HttpException;
import com.colin.library.android.http.parse.IParseResponse;
import com.colin.library.android.http.parse.StringParseResponse;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:06
 * <p>
 * 描述： String
 */
public class StringHttpCallback implements IHttpCallback<String> {
    @NonNull
    protected final IParseResponse<String> mParseResponse;

    public StringHttpCallback() {
        this(new StringParseResponse());
    }

    public StringHttpCallback(@NonNull IParseResponse<String> parseResponse) {
        this.mParseResponse = parseResponse;
    }

    @Nullable
    @Override
    public String parse(@NonNull Response response) throws IOException {
        return this.mParseResponse.parse(response);
    }
}
