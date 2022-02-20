package com.colin.library.android.okHttp.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.request.IRequestBody;
import com.colin.library.android.utils.HttpUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2021-09-11 06:27
 * <p>
 * 描述： 上传字节数组
 */
public class ByteBody implements IRequestBody {
    @NonNull
    private final byte[] mBytes;
    @Nullable
    private final String mContentType;
    @Nullable
    private final String mEncode;
    private final int mOffset;
    private final int mCount;

    public ByteBody(@NonNull byte[] data, @Nullable String contentType, @Nullable String encode, int offset, int count) {
        this.mBytes = data;
        this.mContentType = contentType;
        this.mEncode = encode;
        this.mOffset = offset;
        this.mCount = count;
    }

    @Nullable
    @Override
    public MediaType getMediaType(@NonNull String encode) {
        return HttpUtil.getMediaType(mContentType, encode);
    }

    @NonNull
    @Override
    public RequestBody getRequestBody(@NonNull String encode) {
        return RequestBody.create(getMediaType(encode), mBytes, mOffset, mCount);
    }
}
