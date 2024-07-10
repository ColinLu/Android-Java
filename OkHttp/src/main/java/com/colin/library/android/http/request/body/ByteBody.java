package com.colin.library.android.http.request.body;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.Encode;
import com.colin.library.android.utils.StringUtil;

import java.util.Locale;

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
    @NonNull
    private final String mContentType;
    @Nullable
    private final String mCharset;
    private final int mOffset;
    private final int mCount;

    public ByteBody(@NonNull byte[] data, @NonNull String contentType, @Nullable String charset, int offset, int count) {
        this.mBytes = data;
        this.mContentType = contentType;
        this.mCharset = charset;
        this.mOffset = offset;
        this.mCount = count;
    }

    @Nullable
    @Override
    public MediaType getMediaType(@Nullable String charset) {
        return MediaType.parse(format(getCharset(charset)));
    }

    @NonNull
    @Override
    public RequestBody toRequestBody(@Nullable String charset) {
        return RequestBody.Companion.create(mBytes, getMediaType(charset), mOffset, mCount);
    }

    @NonNull
    private String format(@NonNull String charset) {
        return String.format(Locale.US, "%s; charset=%s", mContentType, charset);
    }

    @NonNull
    private String getCharset(@Nullable String charset) {
        return !StringUtil.isEmpty(mCharset) ? mCharset : !StringUtil.isEmpty(charset) ? charset : Encode.UTF_8;
    }
}
