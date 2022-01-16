package com.colin.library.android.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.UtilHelper;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2019-11-12 08:54
 * <p>
 * 描述： 异常收集 本地缓存 txt
 */
public class CrashUtil {
    private CrashUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @NonNull
    private static String getDefaultName(@NonNull String defaultName) {
        return getDefaultName(UtilHelper.getInstance().getContext(), defaultName);
    }

    @NonNull
    private static String getDefaultName(@Nullable Context context, @NonNull String defaultName) {
        if (null == context) return defaultName + ".txt";
        return context.getPackageName() + ".txt";
    }

    /**
     * Initialization.
     */
    public static void init() {
        init(null, null, null);
    }


    public static void init(@Nullable final OnCrashListener onCrashListener) {
        init(null, null, onCrashListener);
    }


    /**
     * Initialization
     * <p>Must hold
     * {@code <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />}</p>
     *
     * @param folder          The directory of saving crash information.
     * @param onCrashListener The crash listener.
     */
    public static void init(@Nullable final File folder, final OnCrashListener onCrashListener) {
        init(folder, null, onCrashListener);
    }


    public static void init(File folder, @Nullable String fileName, @Nullable final OnCrashListener onCrashListener) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(folder, fileName, onCrashListener));
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
            final String time = TimeUtil.getTimeString("yyyy-MM-dd_HH:mm:ss:SSS");
            final StringBuilder sb = new StringBuilder();
            final String head = "************* Log Head ****************" +
                    "\nTime Of Crash      : " + time +
                    "\nOS name            : " + OSUtil.getName() +
                    "\nOS version         : " + OSUtil.getVersion() +
                    "\nDevice Manufacturer: " + Build.MANUFACTURER +
                    "\nDevice Model       : " + Build.MODEL +
                    "\nAndroid Version    : " + Build.VERSION.RELEASE +
                    "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                    "\nApp VersionName    : " + AppUtil.getVersionName() +
                    "\nApp VersionCode    : " + AppUtil.getVersionCode() +
                    "\n************* Log Head ****************\n\n";
            sb.append(head).append(LogUtil.format(e));
            final String crashInfo = sb.toString();
            if (mOnCrashListener != null) mOnCrashListener.onCrash(crashInfo, e);
            if (null == mFolder) mFolder = PathUtil.getInternalCache();
            if (StringUtil.isEmpty(mFileName)) mFileName = getDefaultName(time);
            File file = new File(mFolder, mFileName);
            LogUtil.e(file.getAbsolutePath());
            FileIOUtil.toFile(file, crashInfo, true);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    public interface OnCrashListener {
        void onCrash(String crashInfo, Throwable e);
    }
}
