package com.colin.library.android.okHttp.parse;


import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.colin.library.android.okHttp.OkHttp;
import com.colin.library.android.okHttp.bean.HttpException;
import com.colin.library.android.okHttp.progress.IProgress;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.PathUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.encrypt.EncodeUtil;
import com.colin.library.android.utils.thread.ThreadUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;

/**
 * 作者： ColinLu
 * 时间： 2020-01-01 18:36
 * <p>
 * 描述： 文件解析 不支持断点
 */
public class FileParseResponse implements IParseResponse<File> {
    @Nullable
    private final File mFolder;                       //目标文件存储的文件夹路径
    @Nullable
    private final String mFileName;                   //目标文件存储的文件名 eg : app.apk
    @Nullable
    private String mEncode;
    @Nullable
    private IProgress mProgress;

    public FileParseResponse() {
        this(null, null);
    }


    public FileParseResponse(@Nullable String fileName) {
        this(null, fileName);
    }


    public FileParseResponse(@Nullable File folder, @Nullable String fileName) {
        this.mFolder = folder;
        this.mFileName = fileName;
    }

    public FileParseResponse setEncode(@Nullable String encode) {
        this.mEncode = encode;
        return this;
    }

    public FileParseResponse setProgress(@Nullable IProgress progress) {
        this.mProgress = progress;
        return this;
    }

    @Override
    @Nullable
    public File parse(@NonNull Response response) throws Throwable {
        final ResponseBody body = response.body();
        final long total = null == body ? -1 : body.contentLength();
        final File file = getDownloadFile(response);
        InputStream bodyStream = null;
        final byte[] buffer = new byte[1024 * 8];
        int len;
        FileOutputStream fileOutputStream = null;
        try {
            bodyStream = body.byteStream();
            fileOutputStream = new FileOutputStream(file);
            long sum = 0;
            while ((len = bodyStream.read(buffer)) != -1) {
                sum += len;
                fileOutputStream.write(buffer, 0, len);
                progress(total, sum * 1.0D / total);
            }
            fileOutputStream.flush();
            return file;
        } finally {
            if (bodyStream != null) Util.closeQuietly(bodyStream);
            if (fileOutputStream != null) Util.closeQuietly(fileOutputStream);
        }
    }

    @Override
    public void progress(double total, double progress) {
        if (mProgress != null) ThreadUtil.runOnUiThread(() -> mProgress.progress(total, progress));
    }

    private File getDownloadFile(@NonNull final Response response) throws IOException {
        return FileUtil.createFile(getFolder(), getFileName(response), true);
    }

    @NonNull
    @RequiresPermission(READ_EXTERNAL_STORAGE)
    private File getFolder() {
        if (mFolder != null && mFolder.isDirectory()) return mFolder;
        return PathUtil.getExternalAppFolder(Environment.DIRECTORY_DOWNLOADS);
    }

    @NonNull
    private String getFileName(@NonNull final Response response) {
        if (!StringUtil.isEmpty(mFileName)) return mFileName;
        final String name = getFileNameByHeader(response);
        if (StringUtil.isEmpty(name)) return getFileNameByUrl(response.request().url());
        return name;
    }

    @Nullable
    private String getFileNameByHeader(@NonNull final Response response) {
        final String disposition = response.header(OkHttp.HEAD_KEY_CONTENT_DISPOSITION);
        String fileName = HttpUtil.value(disposition, "filename", null);
        fileName = EncodeUtil.decode(fileName, mEncode);
        if (!StringUtil.isEmpty(fileName) && fileName.startsWith("\"") && fileName.endsWith("\"")) {
            fileName = fileName.substring(1, fileName.length() - 1);
        }
        return fileName;
    }

    @NonNull
    private String getFileNameByUrl(@NonNull final HttpUrl url) {
        final URI uri = url.uri();
        final String path = uri.getPath();
        if (TextUtils.isEmpty(path)) {
            return Integer.toString(url.hashCode());
        } else {
            final String[] split = path.split("/");
            return split[split.length - 1];
        }
    }

}
