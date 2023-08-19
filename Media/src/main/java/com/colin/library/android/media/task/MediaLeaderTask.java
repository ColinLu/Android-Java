package com.colin.library.android.media.task;

import android.app.Activity;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.media.def.Filter;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaResult;
import com.colin.library.android.media.def.MediaType;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2020-05-24 21:02
 * <p>
 * 描述： 加载多媒体资源 多线程
 */
public class MediaLeaderTask extends AsyncTask<Void, Void, MediaResult> {
    private WeakReference<Activity> mActivityRef;
    @MediaType
    private final int mMediaType;
    private List<MediaFile> mSelectedList;
    private Filter<Long> mFilterSize;                        //筛选条件 大小
    private Filter<Long> mFilterDuration;                    //筛选条件 时长（视频）
    private Filter<String> mFilterMimeType;                  //筛选条件 多媒体类型类型
    private boolean mDisplayInvalid = true;                  //是否筛选要求不符的显示 默认显示
    private OnTaskListener<MediaResult> mOnTaskListener;


    public MediaLeaderTask(@NonNull Activity activity, @MediaType int mediaType) {
        this.mActivityRef = new WeakReference<>(activity);
        this.mMediaType = mediaType;
    }

    public MediaLeaderTask setSelectedList(@Nullable List<MediaFile> list) {
        this.mSelectedList = list;
        return this;
    }

    public MediaLeaderTask setFilterSize(@Nullable Filter<Long> filter) {
        this.mFilterSize = filter;
        return this;
    }

    public MediaLeaderTask setFilterDuration(@Nullable Filter<Long> filter) {
        this.mFilterDuration = filter;
        return this;
    }

    public MediaLeaderTask setFilterMimeType(@Nullable Filter<String> filter) {
        this.mFilterMimeType = filter;
        return this;
    }

    public MediaLeaderTask setDisplayInvalid(boolean display) {
        this.mDisplayInvalid = display;
        return this;
    }

    public MediaLeaderTask setOnTaskListener(@Nullable OnTaskListener<MediaResult> onTaskListener) {
        this.mOnTaskListener = onTaskListener;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if (null != mOnTaskListener) mOnTaskListener.start();
    }

    @Override
    protected MediaResult doInBackground(Void... voids) {
        if (null == mActivityRef || null == mActivityRef.get()) return null;
        return new MediaLoader(mMediaType)
                .setSelectedList(mSelectedList)
                .setFilterSize(mFilterSize)
                .setFilterMime(mFilterMimeType)
                .setFilterDuration(mFilterDuration)
                .setDisplayInvalid(mDisplayInvalid)
                .load(mActivityRef.get());
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        clear();
    }


    @Override
    protected void onCancelled(MediaResult result) {
        super.onCancelled(result);
        clear();
    }

    @Override
    protected void onPostExecute(MediaResult result) {
        if (mOnTaskListener != null) mOnTaskListener.result(result);
        clear();
    }

    private void clear() {
        if (mActivityRef != null && mActivityRef.get() != null) mActivityRef.clear();
        mActivityRef = null;
        mOnTaskListener = null;
    }
}
