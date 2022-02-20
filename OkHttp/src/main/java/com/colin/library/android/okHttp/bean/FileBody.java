package com.colin.library.android.okHttp.bean;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.request.IRequestBody;
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
    @NonNull
    private final String mKey;
    @Nullable
    private final String mEncode;

    public FileBody(@NonNull File file, @NonNull String key) {
        this(file, key, null);
    }

    public FileBody(@NonNull File file, @NonNull String key, @Nullable String encode) {
        this.mFile = file;
        this.mKey = key;
        this.mEncode = encode;
    }

    @NonNull
    public String getKey() {
        return mKey;
    }

    @NonNull
    public File getFile() {
        return mFile;
    }

    @NonNull
    public String getFileName() {
        return mFile.getName();
    }

    @Nullable
    @Override
    public MediaType getMediaType(@NonNull String encode) {
        return HttpUtil.getMediaType(HttpUtil.getMimeType(mFile.getName()), TextUtils.isEmpty(mEncode) ? encode : mEncode);
    }

    @NonNull
    @Override
    public RequestBody getRequestBody(@NonNull String encode) {
        return RequestBody.create(getMediaType(encode), mFile);
    }
}