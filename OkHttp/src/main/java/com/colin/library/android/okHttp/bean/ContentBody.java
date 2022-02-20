package com.colin.library.android.okHttp.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.request.IRequestBody;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.StringUtil;

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
    private final String mEncode;

    public ContentBody(@NonNull String content, @Nullable String contentType, @Nullable String encode) {
        this.mContent = content;
        this.mContentType = contentType;
        this.mEncode = encode;
    }

    @Nullable
    @Override
    public MediaType getMediaType(@NonNull String encode) {
        if (StringUtil.isEmpty(mContentType)) return null;
        return HttpUtil.getMediaType(mContentType, StringUtil.isEmpty(mEncode) ? encode : mEncode);
    }

    @NonNull
    @Override
    public RequestBody getRequestBody(@NonNull String encode) {
        return RequestBody.create(getMediaType(encode), mContent);
    }
}
