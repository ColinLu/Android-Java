package com.colin.library.android.okHttp.callback;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.parse.IParseResponse;
import com.colin.library.android.okHttp.parse.StringParseResponse;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:06
 * <p>
 * 描述： TODO
 */
public final class StringHttpCallback implements IHttpCallback<String> {
    @NonNull
    protected final IParseResponse<String> mParseResponse;

    public StringHttpCallback() {
        this(new StringParseResponse());
    }

    public StringHttpCallback(@NonNull IParseResponse<String> parseResponse) {
        this.mParseResponse = parseResponse;
    }
}
