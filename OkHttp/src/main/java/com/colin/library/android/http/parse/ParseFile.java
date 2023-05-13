package com.colin.library.android.http.parse;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.helper.ThreadHelper;
import com.colin.library.android.http.Util;
import com.colin.library.android.http.progress.IProgress;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.PathUtil;
import com.colin.library.android.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者： ColinLu
 * 时间： 2023-04-22 13:58
 * <p>
 * 描述： Result->File
 */
public class ParseFile implements IParse<File> {
    @Nullable
    private File mFolder;
    @Nullable
    private String mFileName;

    @Nullable
    private IProgress mProgress;

    public ParseFile(@Nullable File folder, @Nullable String fileName) {
        this.mFolder = folder;
        this.mFileName = fileName;
    }

    public ParseFile setProgress(@Nullable IProgress progress) {
        this.mProgress = progress;
        return this;
    }

    @Nullable
    @Override
    @WorkerThread
    public File parse(@NonNull final Response response) throws IOException {
        final ResponseBody body = response.body();
        final long total = body.contentLength();
        if (total == 0) return null;
        final String fileName = getFileName(response);
        final File folder = getFolder();
        final File file = new File(folder, fileName);
        final boolean exists = FileUtil.createFile(file);
        if (!exists) return null;
        LogUtil.d(fileName, folder.getAbsolutePath());
        InputStream is = null;
        final byte[] buffer = new byte[1024 * 8];
        int len;
        FileOutputStream out = null;
        try {
            is = body.byteStream();
            out = new FileOutputStream(folder);
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
    private File getFolder() {
        if (FileUtil.isDir(mFolder)) return mFolder;
        return PathUtil.getExternalAppFile(Environment.DIRECTORY_DOWNLOADS);
    }

    @NonNull
    private String getFileName(@NonNull final Response response) {
        if (!StringUtil.isEmpty(mFileName)) return mFileName;
        String name = Util.getFileName(response);
        return StringUtil.isEmpty(name) ? "unknown" : name;
    }
}
