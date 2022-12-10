package com.colin.library.android.http.parse;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.helper.ThreadHelper;
import com.colin.library.android.http.OkHttp;
import com.colin.library.android.http.progress.IProgress;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.PathUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.TimeUtil;
import com.colin.library.android.utils.encrypt.EncodeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;


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
    private IProgress mProgress;

    public FileParseResponse() {
        this(null, null);
    }


    public FileParseResponse(@Nullable String fileName) {
        this(null, fileName);
    }


    public FileParseResponse(@Nullable File dir, @Nullable String fileName) {
        this.mDir = dir;
        this.mFileName = fileName;
    }

    public FileParseResponse setProgress(@Nullable IProgress progress) {
        this.mProgress = progress;
        return this;
    }

    @Nullable
    @Override
    @WorkerThread
    public File parse(@NonNull Response response) throws IOException {
        final ResponseBody body = response.body();
        final long total = body.contentLength();
        if (total == 0) return null;
        final String fileName = getFileName(response);
        final File file = FileUtil.getFile(getDir(), fileName);
        final boolean exists = FileUtil.createFile(file, true);
        if (!exists) return null;
        LogUtil.d(fileName, file.getAbsolutePath());
        InputStream is = null;
        final byte[] buffer = new byte[1024 * 8];
        int len;
        FileOutputStream out = null;
        try {
            is = body.byteStream();
            out = new FileOutputStream(file);
            long sum = 0;
            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                if (mProgress != null) {
                    final long progress = sum + len;
                    ThreadHelper.getInstance().post(() -> progress(total, progress));
                }

            }
            IOUtil.flush(out);
            return file;
        } finally {
            IOUtil.close(is, out);
        }
    }

    @NonNull
    private File getDir() {
        return FileUtil.isExists(mDir) ? mDir : PathUtil.getInternalCache();
    }

    @NonNull
    private String getFileName(@NonNull final Response response) {
        if (!StringUtil.isEmpty(mFileName)) return mFileName;
        String fileName = HttpUtil.head(response.header(OkHttp.HEAD_KEY_CONTENT_DISPOSITION), "filename", null);
        fileName = EncodeUtil.decode(fileName);
        if (!StringUtil.isEmpty(fileName)) return EncodeUtil.decode(fileName);
        fileName = HttpUtil.getFileName(response.request().url());
        fileName = EncodeUtil.decode(fileName);
        return StringUtil.isEmpty(fileName) ? TimeUtil.getTimeString() : fileName;
    }
}
