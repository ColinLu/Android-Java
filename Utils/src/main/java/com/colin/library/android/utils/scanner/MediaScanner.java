package com.colin.library.android.utils.scanner;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2020-04-27 23:33
 * <p>
 * 描述： 单个文件扫描 支持多次
 */
public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private final WeakReference<Context> mContextRef;
    private final WeakReference<MediaScannerConnection> mConnectionRef;
    private MediaScannerConnection.OnScanCompletedListener mListener;
    private int mScannerTimes = 1;
    private int mScannerCount = 0;
    private Uri mUri;
    private String mPath;
    private String mMimeType;
    private boolean mSendBroadcast;

    public MediaScanner(@NonNull Context context) {
        this.mContextRef = new WeakReference<>(context);
        this.mConnectionRef = new WeakReference<>(new MediaScannerConnection(context.getApplicationContext(), this));
    }

    /*扫描文件路径*/
    public MediaScanner setUri(@Nullable Uri uri) {
        this.mUri = uri;
        return this;
    }

    public MediaScanner setFile(@Nullable File file) {
        this.mPath = null == file ? null : file.getAbsolutePath();
        return this;
    }

    public MediaScanner setPath(@Nullable String path) {
        this.mPath = path;
        return this;
    }

    public MediaScanner setMimeType(@Nullable String mimeType) {
        this.mMimeType = mimeType;
        return this;
    }

    /*扫描之后发送广播*/
    public MediaScanner setSendBroadcast(boolean sendBroadcast) {
        this.mSendBroadcast = sendBroadcast;
        return this;
    }

    /*扫描次数*/
    public MediaScanner setScannerTimes(@IntRange(from = 0) int times) {
        this.mScannerTimes = times;
        return this;
    }

    /*扫描监听器*/
    public MediaScanner setOnScannerListener(MediaScannerConnection.OnScanCompletedListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 是否正在扫描
     *
     * @return
     */
    public boolean isRunning() {
        final MediaScannerConnection connection = null == mConnectionRef ? null : mConnectionRef.get();
        return connection != null && connection.isConnected();
    }

    public void start() {
        start(null);
    }

    /*开始扫描*/
    public void start(@Nullable MediaScannerConnection.OnScanCompletedListener onScannerListener) {
        final MediaScannerConnection connection = null == mConnectionRef ? null : mConnectionRef.get();
        if (connection != null) {
            mListener = onScannerListener;
            if (isRunning()) connection.disconnect();
            connection.connect();
        }
    }

    /*扫描开始 子线程*/
    @Override
    public void onMediaScannerConnected() {
        final MediaScannerConnection connection = null == mConnectionRef ? null : mConnectionRef.get();
        if (null == connection) return;
        sendBroadcast(null == mContextRef ? null : mContextRef.get(), mUri);
        connection.scanFile(mPath, TextUtils.isEmpty(mMimeType) ? FileUtil.getMimeType(mPath) : mMimeType);
        mScannerCount += 1;
    }

    //全局广播 刷新相册
    private void sendBroadcast(@Nullable final Context context, @Nullable final Uri uri) {
        if (!mSendBroadcast || null == context || null == uri) return;
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    /*扫描完成*/
    @Override
    public void onScanCompleted(final String path, final Uri uri) {
        final MediaScannerConnection connection = null == mConnectionRef ? null : mConnectionRef.get();
        if (null == connection) return;
        //跟新Uri
        if (mUri == null && uri != null) mUri = uri;
        //次数判断
        if (mScannerTimes > 1) {
            mScannerTimes -= 1;
            onMediaScannerConnected();
        } else completed(path, uri);//回调
    }

    private void completed(final String path, final Uri uri) {
        LogUtil.d(null == mUri ? "mUri" : mUri.toString(),
                null == mPath ? "mPath" : mPath,
                null == uri ? "uri" : uri.toString(),
                null == path ? "path" : path,
                "mScannerTimes:" + mScannerTimes,
                "mScannerCount:" + mScannerCount);
        Context context = null == mContextRef ? null : mContextRef.get();
        if (null == context || null == mListener) return;
        ContextCompat.getMainExecutor(context).execute(() -> mListener.onScanCompleted(path, uri));
    }

}