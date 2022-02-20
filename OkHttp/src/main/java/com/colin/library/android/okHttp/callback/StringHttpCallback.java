package com.colin.library.android.okHttp.callback;

import com.colin.library.android.okHttp.parse.IParseResponse;
import com.colin.library.android.okHttp.parse.StringParseResponse;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:06
 * <p>
 * 描述： TODO
 */
public class StringHttpCallback implements IHttpCallback<String> {
    protected final IParseResponse<String> mParseResponse;

    public StringHttpCallback() {
        this(new StringParseResponse());
    }

    public StringHttpCallback(IParseResponse<String> parseResponse) {
        mParseResponse = parseResponse;
    }
}
