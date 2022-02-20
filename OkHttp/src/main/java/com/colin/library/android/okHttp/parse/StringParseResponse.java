package com.colin.library.android.okHttp.parse;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.bean.HttpException;

import java.io.IOException;

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
    public String parse(@NonNull Response response) throws HttpException {
        final ResponseBody body = response.body();
        if (body == null) return null;
        try {
            return body.string();
        } catch (IOException e) {
            throw new HttpException(HttpException.CODE_HTTP_PARSE, "parse", e);
        }
    }
}
