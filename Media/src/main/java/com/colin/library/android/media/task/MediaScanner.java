package com.colin.library.android.media.task;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.media.def.OnScannerListener;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2020-04-27 23:33
 * <p>
 * 描述： 单个文件扫描 支持多次
 */
public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    @NonNull
    private final WeakReference<Context> mWeakReference;
    private final MediaScannerConnection mMediaScanConn;
    @Nullable
    private String mFilePath;
    private OnScannerListener mListener;
    private int mScannerTimes = 1;
    private int mScannerCount = 0;
    private Uri mUri;

    public MediaScanner(@NonNull Context context) {
        this.mWeakReference = new WeakReference<>(context);
        this.mMediaScanConn = new MediaScannerConnection(context.getApplicationContext(), this);
    }

    /*扫描文件路径*/
    public MediaScanner setPath(@Nullable String path) {
        this.mFilePath = path;
        return this;
    }

    /*扫描文件路径*/
    public MediaScanner setUri(@Nullable Uri uri) {
        this.mUri = uri;
        return this;
    }

    /*扫描次数*/
    public MediaScanner setScannerTimes(int times) {
        this.mScannerTimes = times;
        return this;
    }

    /*扫描监听器*/
    public MediaScanner setOnScannerListener(@Nullable OnScannerListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 是否正在扫描
     *
     * @return
     */
    public boolean isRunning() {
        return mMediaScanConn.isConnected();
    }

    public void scan() {
        scan(null);
    }

    public void scan(@Nullable OnScannerListener listener) {
        if (listener != null) mListener = listener;
        if (isRunning()) mMediaScanConn.disconnect();
        this.mMediaScanConn.connect();
    }

    /*扫描开始*/
    @Override
    public void onMediaScannerConnected() {
        if (null != mListener && mScannerCount == 0) mListener.start(mFilePath);
        //通知广播
        if (mWeakReference.get() != null && mUri != null) {
            mWeakReference.get().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mUri));
        }
        mMediaScanConn.scanFile(mFilePath, FileUtil.getMimeType(mFilePath));
        mScannerCount += 1;
    }

    /*扫描完成*/
    @Override
    public void onScanCompleted(final String path, final Uri uri) {
        LogUtil.d(null == mUri ? "mUri is empty" : mUri.toString()
                , null == uri ? "uri" : uri.toString(),
                path, "mScannerTimes:" + mScannerTimes, "mScannerCount:" + mScannerCount);
        //如果扫描出来了  直接返回
        if (mListener != null && uri != null && !StringUtil.isSpace(path)) {
            mListener.finish(path, uri);
            return;
        }
        //继续扫描
        if (mMediaScanConn != null && mScannerTimes > 1) {
            mScannerTimes -= 1;
            onMediaScannerConnected();
        } else if (mListener != null) {
            mListener.finish(path, uri);
        }
    }


}