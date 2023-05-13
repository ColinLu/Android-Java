package com.colin.library.android.http.request.body;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.Encode;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.StringUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2021-09-11 06:26
 * <p>
 * 描述： 上传文件
 */
public class FileBody implements IRequestBody {
    @NonNull
    private final File mFile;
    @Nullable
    private final String mKey;
    @Nullable
    private final String mCharset;

    public FileBody(@NonNull File file, @Nullable String key, @Nullable String charset) {
        this.mFile = file;
        this.mKey = key;
        this.mCharset = charset;
    }

    @NonNull
    public String getKey() {
        return StringUtil.isEmpty(mKey) ? mFile.getName() : mKey;
    }

    @NonNull
    public String getFileName() {
        return mFile.getName();
    }

    @Nullable
    @Override
    public MediaType getMediaType(@Nullable String charset) {
        return HttpUtil.getMediaType(HttpUtil.getMimeType(mFile.getName()), getCharset(charset));
    }

    @NonNull
    @Override
    public okhttp3.RequestBody toRequestBody(@Nullable String charset) {
        return RequestBody.Companion.create(mFile, getMediaType(charset));
    }

    @NonNull
    private String getCharset(@Nullable String charset) {
        return !StringUtil.isEmpty(mCharset) ? mCharset : !StringUtil.isEmpty(charset) ? charset : Encode.UTF_8;
    }
}