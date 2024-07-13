package com.colin.library.android.http.parse;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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

    public ParseFile(@Nullable File folder, @Nullable String fileName) {
        this.mFolder = folder;
        this.mFileName = fileName;
    }

    @Nullable
    @Override
    @WorkerThread
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public File parse(@NonNull final Response response, @Nullable String encode, @NonNull final IProgress progress) throws IOException {
        final ResponseBody body = response.body();
        if (body == null) return null;
        final long total = body.contentLength();
        if (total == 0) return null;
        final String fileName = getFileName(response, encode);
        final File folder = getFolder();
        final File file = new File(folder, fileName);
        final boolean exists = FileUtil.createFile(file, true);
        if (!exists) return null;
        LogUtil.d(fileName, file.getPath());
        InputStream is = null;
        final byte[] buffer = new byte[1024];
        int len;
        FileOutputStream out = null;
        try {
            is = body.byteStream();
            out = new FileOutputStream(fileName);
            long sum = 0;
            while ((len = is.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                final long pro = sum + len;
                ThreadHelper.getInstance().post(() -> progress.progress(total, pro));
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
        return PathUtil.getInternalCache();
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private String getFileName(@NonNull final Response response, @Nullable String encode) {
        if (!StringUtil.isEmpty(mFileName)) return mFileName;
        String name = Util.getFileName(response, encode);
        return StringUtil.isEmpty(name) ? "unknown" : name;
    }
}
