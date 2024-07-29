package com.colin.library.android.http.parse;

import android.os.Build;
import android.os.Environment;

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
import com.colin.library.android.utils.StorageUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.data.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

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
        final long total = body == null ? Constants.ZERO : body.contentLength();
        if (total == Constants.ZERO) return null;
        final String fileName = getFileName(response, encode);
        final File folder = getFolder();
        final File file = new File(folder, fileName);
        final boolean exists = FileUtil.createFile(file, true);
        LogUtil.i(String.format(Locale.US, "name:%s path:%s isExists:%s", fileName, file.getPath(), exists));
        if (!exists) return null;
        InputStream is = null;
        final byte[] buffer = new byte[8192];
        FileOutputStream out = null;
        try {
            int len;
            long sum = Constants.ZERO;
            is = body.byteStream();
            out = new FileOutputStream(file);
            while ((len = is.read(buffer)) != Constants.INVALID) {
                out.write(buffer, Constants.ZERO, len);
                sum += len;
                final long progressValue = sum;
                ThreadHelper.getInstance().post(() -> progress.progress(total, progressValue));
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
        File dir = StorageUtil.getExternalDir(Environment.DIRECTORY_DOWNLOADS);
        if (FileUtil.isDir(dir)) return dir;
        return StorageUtil.getInternalDataDir();
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private String getFileName(@NonNull final Response response, @Nullable String encode) {
        if (!StringUtil.isEmpty(mFileName)) return mFileName;
        String name = Util.getFileName(response, encode);
        return StringUtil.isEmpty(name) ? "unknown" : name;
    }
}
