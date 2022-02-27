package com.colin.library.android.okHttp.parse;


import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.WorkerThread;

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
    private final File mDir;                            //目标文件存储的文件夹路径
    @Nullable
    private final String mFileName;                     //目标文件存储的文件名 eg : app.apk
    @Nullable
    private final IProgress mProgress;

    public FileParseResponse() {
        this(null, null);
    }


    public FileParseResponse(@Nullable String fileName) {
        this(null, fileName, null);
    }


    public FileParseResponse(@Nullable File dir, @Nullable String fileName) {
        this(dir, fileName, null);
    }

    public FileParseResponse(@Nullable File dir, @Nullable String fileName, @Nullable IProgress progress) {
        this.mDir = dir;
        this.mFileName = fileName;
        this.mProgress = progress;
    }

    @Nullable
    @Override
    @WorkerThread
    public File parse(@NonNull Response response) throws Throwable {
        final ResponseBody body = response.body();
        final long total = null == body ? 0 : body.contentLength();
        if (total == 0) return null;
        final File file = FileUtil.getFile(getDir(), getFileName(response));
        final boolean exists = FileUtil.createFile(file, true);
        if (!exists) return null;
        InputStream is = null;
        final byte[] buffer = new byte[1024 * 8];
        int len;
        FileOutputStream out = null;
        try {
            is = body.byteStream();
            out = new FileOutputStream(file);
            long sum = 0;
            while ((len = is.read(buffer)) != -1) {
                sum += len;
                out.write(buffer, 0, len);
                progress(total, (sum * 1.0F / total));
            }
            out.flush();
            return file;
        } finally {
            if (is != null) Util.closeQuietly(is);
            if (out != null) Util.closeQuietly(out);
        }
    }

    @Override
    @WorkerThread
    public void progress(float total, float progress) {
        if (mProgress != null) ThreadUtil.runUI(() -> mProgress.progress(total, progress));
    }

    @NonNull
    private File getDir() {
        return mDir != null && mDir.isDirectory() ? mDir : PathUtil.getDownloadCache();
    }

    @NonNull
    private String getFileName(@NonNull final Response response) {
        if (!TextUtils.isEmpty(mFileName)) return mFileName;
        String fileName = HttpUtil.head(response.header(OkHttp.HEAD_KEY_CONTENT_DISPOSITION), "filename", null);
        if (!TextUtils.isEmpty(fileName)) return fileName;
        fileName = HttpUtil.getFileName(response.request().url());
        return TextUtils.isEmpty(fileName) ? FileUtil.getFileNameDef(null) : fileName;
    }
}
