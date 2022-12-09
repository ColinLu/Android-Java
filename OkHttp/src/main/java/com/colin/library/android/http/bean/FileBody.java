package com.colin.library.android.http.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.HttpUtil;

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
        return TextUtils.isEmpty(mKey) ? mFile.getName() : mKey;
    }

    @NonNull
    public String getFileName() {
        return mFile.getName();
    }

    @Nullable
    @Override
    public MediaType getMediaType() {
        return HttpUtil.getMediaType(HttpUtil.getMimeType(mFile.getName()), mCharset);
    }

    @NonNull
    @Override
    public RequestBody getRequestBody() {
        return RequestBody.Companion.create(mFile, getMediaType());
    }
}