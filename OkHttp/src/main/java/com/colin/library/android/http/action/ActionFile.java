package com.colin.library.android.http.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.parse.IParse;
import com.colin.library.android.http.parse.ParseFile;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2023-04-23 21:00
 * <p>
 * 描述： Callback Result->File
 */
public class ActionFile implements IAction<File> {
    @NonNull
    protected final IParse<File> mParseResult;

    public ActionFile() {
        this(null, null);
    }

    public ActionFile(@Nullable String fileName) {
        this(null, fileName);
    }


    public ActionFile(@Nullable File dir, @Nullable String fileName) {
        this.mParseResult = new ParseFile(dir, fileName).setProgress(this);
    }

    public ActionFile(@NonNull IParse<File> parseResponse) {
        this.mParseResult = parseResponse;
    }

    @Nullable
    @Override
    public File parse(@NonNull Response response) throws IOException {
        return mParseResult.parse(response);
    }
}
