package com.colin.library.android.helper;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.AppUtil;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.OSUtil;
import com.colin.library.android.utils.StorageUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.TimeUtil;
import com.colin.library.android.utils.data.Constants;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2022-11-29 21:28
 * <p>
 * 描述： 崩溃辅助类
 */
public final class CrashHelper {
    private static volatile CrashHelper sHelper;

    private CrashHelper() {
    }


    public static CrashHelper getInstance() {
        if (sHelper == null) {
            synchronized (CrashHelper.class) {
                if (sHelper == null) sHelper = new CrashHelper();
            }
        }
        return sHelper;
    }

    public void init(@NonNull final OnCrashListener onCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(null, null, onCrashListener));
    }

    public void init(@NonNull String fileName, @NonNull final OnCrashListener onCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(null, fileName, onCrashListener));
    }

    public void init(@NonNull File folder, @NonNull String fileName, @NonNull final OnCrashListener onCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(folder, fileName, onCrashListener));
    }

    public File getFile() {
        return StorageUtil.getInternalCacheDir();
    }

    public String getFileName() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getPackageName() + "_" + TimeUtil.getTimeString(Constants.FORMAT_DAY_PATTERN) + ".txt";
    }

    private static class CrashHandler implements Thread.UncaughtExceptionHandler {
        private File mFolder;
        private String mFileName;
        private final OnCrashListener mOnCrashListener;

        public CrashHandler(@Nullable File folder, @Nullable String fileName, @Nullable OnCrashListener onCrashListener) {
            this.mFolder = folder;
            this.mFileName = fileName;
            this.mOnCrashListener = onCrashListener;
        }

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            final StringBuilder sb = new StringBuilder();
            sb.append("****************************** Log Start ******************************").append(Constants.LINE_SEP)
                    .append("Time Of Crash      : ").append(TimeUtil.getTimeString()).append(Constants.LINE_SEP)
                    .append("OS name            : ").append(OSUtil.getName()).append(Constants.LINE_SEP)
                    .append("OS version         : ").append(OSUtil.getVersion()).append(Constants.LINE_SEP)
                    .append("Device Manufacturer: ").append(Build.MANUFACTURER).append(Constants.LINE_SEP)
                    .append("Device Model       : ").append(Build.MODEL).append(Constants.LINE_SEP)
                    .append("Android Version    : ").append(Build.VERSION.RELEASE).append(Constants.LINE_SEP)
                    .append("Android SDK        : ").append(Build.VERSION.SDK_INT).append(Constants.LINE_SEP)
                    .append("App VersionName    : ").append(AppUtil.getVersionName()).append(Constants.LINE_SEP)
                    .append("App VersionCode    : ").append(AppUtil.getVersionCode()).append(Constants.LINE_SEP)
                    .append(Constants.LINE_SEP).append(LogUtil.format(e)).append(Constants.LINE_SEP)
                    .append("****************************** Log End ******************************").append(Constants.LINE_SEP);
            if (mOnCrashListener != null) mOnCrashListener.crash(e, sb.toString());
            if (FileUtil.isDir(mFolder)) mFolder = getInstance().getFile();
            if (StringUtil.isEmpty(mFileName)) mFileName = getInstance().getFileName();
            final File file = new File(mFolder, mFileName);
            LogUtil.e(file.getAbsolutePath());
            ThreadHelper.getInstance().doAsync(() -> FileUtil.toFile(file, sb, true));
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    public interface OnCrashListener {
        void crash(Throwable error, String info);
    }
}
