package com.colin.library.android.helper;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.AppUtil;
import com.colin.library.android.utils.CrashUtil;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.OSUtil;
import com.colin.library.android.utils.PathUtil;
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


    public void init(@NonNull File folder, @NonNull String fileName, @NonNull final CrashUtil.OnCrashListener onCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(folder, fileName, onCrashListener));
    }

    public String getFileName() {
        return UtilHelper.getInstance().getContext().getPackageName() + TimeUtil.getDateFormat(Constants.FORMAT_DAY_PATTERN) + ".txt";
    }

    private static class CrashHandler implements Thread.UncaughtExceptionHandler {
        private File mFolder;
        private String mFileName;
        private CrashUtil.OnCrashListener mOnCrashListener;

        public CrashHandler(@Nullable File folder, @Nullable String fileName, @Nullable CrashUtil.OnCrashListener onCrashListener) {
            this.mFolder = folder;
            this.mFileName = fileName;
            this.mOnCrashListener = onCrashListener;
        }

        @Override
        public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
            final String time = TimeUtil.getTimeString("yyyy-MM-dd_HH:mm:ss:SSS");
            final StringBuilder sb = new StringBuilder();
            final String head = "*************** Log Start ***************" +
                    "\nTime Of Crash      : " + time +
                    "\nOS name            : " + OSUtil.getName() +
                    "\nOS version         : " + OSUtil.getVersion() +
                    "\nDevice Manufacturer: " + Build.MANUFACTURER +
                    "\nDevice Model       : " + Build.MODEL +
                    "\nAndroid Version    : " + Build.VERSION.RELEASE +
                    "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                    "\nApp VersionName    : " + AppUtil.getVersionName() +
                    "\nApp VersionCode    : " + AppUtil.getVersionCode() +
                    "\n*************** Log End ***************\n\n";
            sb.append(head).append(LogUtil.format(e));
            if (mOnCrashListener != null) mOnCrashListener.onCrash(sb.toString(), e);
            if (null == mFolder) mFolder = PathUtil.getInternalCache();
            if (StringUtil.isEmpty(mFileName)) mFileName = getInstance().getFileName();
            final File file = new File(mFolder, mFileName);
            LogUtil.e(file.getAbsolutePath());
            FileUtil.toFile(sb, file, true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    public interface OnCrashListener {
        void onCrash(String crashInfo, Throwable e);
    }
}
