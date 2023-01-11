package com.colin.library.android.media.task;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.media.util.MediaUtil;
import com.colin.library.android.utils.HttpUtil;
import com.colin.library.android.utils.StringUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 20:11
 * <p>
 * 描述： 下载多媒体文件
 */
public class MediaDownTask extends AsyncTask<Void, Void, File> {
    private WeakReference<Context> mWeakReference;
    @MediaType
    private int mMediaType;
    private String mUrl;
    private int mDrawableRes;
    private OnTaskListener<File> mOnTaskListener;

    public MediaDownTask(@NonNull Context context) {
        this(context, MediaType.IMAGE);
    }

    public MediaDownTask(@NonNull Context context, @MediaType int mediaType) {
        this.mWeakReference = new WeakReference<>(context);
        this.mMediaType = mediaType;
    }

    /*下载地址*/
    public MediaDownTask setUrl(@Nullable String url) {
        this.mUrl = url;
        return this;
    }

    /*下载资源地址*/
    public MediaDownTask setRes(@DrawableRes int res) {
        this.mDrawableRes = res;
        return this;
    }

    /*接口回调*/
    public MediaDownTask setOnTaskListener(@Nullable OnTaskListener<File> onTaskListener) {
        this.mOnTaskListener = onTaskListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if (null != mOnTaskListener) mOnTaskListener.start();
    }

    @Override
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    protected File doInBackground(Void... voids) {
        if (null == mWeakReference || null == mWeakReference.get()) return null;
        if (HttpUtil.isUrl(mUrl)) {
            if (mMediaType == MediaType.IMAGE) return saveImage(mWeakReference.get(), mUrl);
            else return saveFile(mWeakReference.get(), mUrl, mMediaType);
        } else if (mDrawableRes != Resources.ID_NULL)
            return saveDrawable(mWeakReference.get(), mDrawableRes);
        return null;
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        clear();
    }


    @Override
    protected void onCancelled(@Nullable File result) {
        super.onCancelled(result);
        clear();
    }

    @Override
    protected void onPostExecute(@Nullable File result) {
        if (mOnTaskListener != null) mOnTaskListener.result(result);
        clear();
    }

    private void clear() {
        if (mWeakReference != null && mWeakReference.get() != null) mWeakReference.clear();
        mWeakReference = null;
        mOnTaskListener = null;
    }

    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    private File saveDrawable(Context context, int drawableRes) {
        if (null == context || drawableRes == Resources.ID_NULL) return null;
        final File file = MediaUtil.createFile(context, mMediaType);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        boolean down = saveBitmap(file, bitmap);
        return down ? file : null;
    }


    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    private File saveImage(Context context, String httpUrl) {
        if (null == context || StringUtil.isEmpty(httpUrl)) return null;
        final String fileName = HttpUtil.getFileName(httpUrl);
        File file = MediaUtil.createFile(context, fileName == null ? "unknown" : fileName, mMediaType);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(20 * 1000);
            conn.setConnectTimeout(20 * 1000);
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                boolean saveBitmap = saveBitmap(file, BitmapFactory.decodeStream(conn.getInputStream()));
                return saveBitmap ? file : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();  // 关闭连接
        }
        return null;
    }

    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public boolean saveBitmap(final File file, final Bitmap bitmap) {
        if (null == bitmap || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) return false;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            if (!bitmap.isRecycled()) bitmap.recycle();
            return compress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public File saveFile(@Nullable final Context context, @Nullable final String httpUrl, @MediaType final int mediaType) {
        if (null == context || TextUtils.isEmpty(httpUrl)) return null;
        String fileName = HttpUtil.getFileName(httpUrl);
        File file = MediaUtil.createFile(context, fileName == null ? "unknown" : fileName, mMediaType);
        HttpURLConnection conn = null;
        try {
            URL url = new URL(httpUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(20 * 1000);
            conn.setConnectTimeout(20 * 1000);
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                byte[] bytes = new byte[1024 * 2];
                int len;
                while ((len = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, len);
                }
                bos.flush();
                bos.close();
                bis.close();
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }
        return null;
    }
}

