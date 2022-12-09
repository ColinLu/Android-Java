package com.colin.library.android.http.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2021-09-11 06:27
 * <p>
 * 描述： json\xml\text
 */
public class ContentBody implements IRequestBody {
    @NonNull
    private final String mContent;
    @Nullable
    private final String mContentType;
    @Nullable
    private final String mCharset;

    public ContentBody(@NonNull String content, @NonNull String contentType, @NonNull String charset) {
        this.mContent = content;
        this.mContentType = contentType;
        this.mCharset = charset;
    }

    @Nullable
    @Override
    public MediaType getMediaType() {
        return MediaType.parse(String.format(Locale.US, "%s; charset=%s", mContentType, mCharset));
    }

    @NonNull
    @Override
    public RequestBody getRequestBody() {
        return RequestBody.Companion.create(mContent, getMediaType());
    }
}
