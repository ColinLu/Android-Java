package com.colin.library.android.http.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.parse.IParse;
import com.colin.library.android.http.parse.ParseString;
import com.colin.library.android.http.progress.IProgress;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2023-04-23 21:00
 * <p>
 * 描述： Callback Result->String
 */
public class ActionString implements IAction<String> {
    @NonNull
    protected final IParse<String> mParseResult;

    public ActionString() {
        this(new ParseString());
    }

    public ActionString(@NonNull IParse<String> parse) {
        this.mParseResult = parse;
    }

    @Nullable
    @Override
    public String parse(@NonNull Response response, @Nullable String encode, @NonNull IProgress progress) throws IOException {
        return this.mParseResult.parse(response, encode, progress);
    }
}
