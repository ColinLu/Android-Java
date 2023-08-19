package com.colin.library.android.media.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.media.R;
import com.colin.library.android.media.def.Filter;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaFolder;
import com.colin.library.android.media.def.MediaResult;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.media.util.MediaUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.UriUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作者： ColinLu
 * 时间： 2020-05-23 14:36
 * <p>
 * 描述： 多媒体管理器
 */
final class MediaLoader {
    /*搜索排序*/
    private static final String ORDER = MediaStore.MediaColumns.DATE_ADDED + " DESC ";
    /*过滤文件*/
    private static final String SELECTED = MediaStore.MediaColumns.SIZE + " > 0 ";
    /*媒体文件*/
    private static final String[] VIDEO = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.WIDTH, MediaStore.MediaColumns.HEIGHT, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_ADDED, MediaStore.MediaColumns.DURATION};
    /*媒体文件*/
    private static final String[] IMAGE = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE, MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.WIDTH, MediaStore.MediaColumns.HEIGHT, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_ADDED};
    @MediaType
    private final int mMediaType;           //筛选文件类型 图片 视频 图片+视频
    private Filter<Long> mFilterSize;       //过滤大小
    private Filter<Long> mFilterDuration;   //视频过滤时长
    private Filter<String> mFilterMime;     //过滤文件类型
    private List<MediaFile> mSelectedList;  //相册已选中
    private boolean mDisplayInvalid;        //是否显示过滤掉的文件

    public MediaLoader(@MediaType int mediaType) {
        this.mMediaType = mediaType;
    }

    public MediaLoader setSelectedList(@Nullable List<MediaFile> selectedList) {
        this.mSelectedList = selectedList;
        return this;
    }

    public MediaLoader setFilterSize(@Nullable Filter<Long> filter) {
        this.mFilterSize = filter;
        return this;
    }

    public MediaLoader setFilterDuration(@Nullable Filter<Long> filter) {
        this.mFilterDuration = filter;
        return this;
    }

    public MediaLoader setFilterMime(@Nullable Filter<String> filter) {
        this.mFilterMime = filter;
        return this;
    }

    public MediaLoader setDisplayInvalid(boolean display) {
        this.mDisplayInvalid = display;
        return this;
    }

    @WorkerThread
    public synchronized MediaResult load(@NonNull final Context context) {
        final MediaFolder all = new MediaFolder(context.getString(R.string.all_file));
        final ConcurrentHashMap<String, MediaFolder> map = new ConcurrentHashMap<>();
        if (mMediaType == MediaType.IMAGE) {
            loadMediaImage(context.getApplicationContext(), MediaType.IMAGE, map, all);
        } else if (mMediaType == MediaType.VIDEO) {
            loadMediaVideo(context, MediaType.VIDEO, map, all);
        } else {
            loadMediaImage(context, MediaType.IMAGE, map, all);
            loadMediaVideo(context, MediaType.VIDEO, map, all);
        }
        //排序
        Collections.sort(all.mList);
        final ArrayList<MediaFolder> list = new ArrayList<>();
        list.add(all);

        final ArrayList<MediaFile> selected = new ArrayList<>();
        //选中
        MediaFile mediaFile;
        for (int i = 0; i < all.mList.size(); i++) {
            mediaFile = all.mList.get(i);
            if (mediaFile.mSelected) selected.add(mediaFile);
        }
        //排序加入
        for (Map.Entry<String, MediaFolder> entry : map.entrySet()) {
            final MediaFolder albumFolder = entry.getValue();
            Collections.sort(albumFolder.mList);
            list.add(albumFolder);
        }
        return new MediaResult(list, selected);
    }

    /*通过Uri转化*/
    @Nullable
    @WorkerThread
    public synchronized MediaFile convert(@NonNull final Context context, @NonNull final Uri uri) {
        if (mMediaType == MediaType.IMAGE) return convertImage(context, uri);
        else if (mMediaType == MediaType.VIDEO) return convertVideo(context, uri);
        return null;
    }


    private synchronized void loadMediaVideo(@NonNull final Context context, @MediaType final int mediaType,
            @NonNull final ConcurrentHashMap<String, MediaFolder> map, @NonNull final MediaFolder all) {
        final Uri uri = getMediaUri(mediaType);
        final Cursor cursor = query(context.getContentResolver(), uri, VIDEO);
        if (null == cursor) return;
        try {
            MediaFile albumFile = null;
            while (cursor.moveToNext()) {
                final int id = cursor.getInt(0);
                final String path = cursor.getString(1);
                final String mimeType = cursor.getString(2);
                final String folder = cursor.getString(3);
                final int width = cursor.getInt(4);
                final int height = cursor.getInt(5);
                final long size = cursor.getLong(6);
                final long date = cursor.getLong(7);
                final long duration = cursor.getLong(8);
                albumFile = new MediaFile(mediaType);
                albumFile.id = id;
                albumFile.mPath = path;
                albumFile.mUri = UriUtil.getUri(context, uri, id, path);
                albumFile.mFolder = folder;
                albumFile.mMimeType = mimeType;
                albumFile.mWidth = width;
                albumFile.mHeight = height;
                albumFile.mSize = size;
                albumFile.mDate = date;
                albumFile.mDuration = duration;
                albumFile.mInvalid = MediaUtil.filterFailed(albumFile, mFilterMime, mFilterSize, mFilterDuration);
                //无效并且不展示
                if (albumFile.mInvalid && !mDisplayInvalid) continue;
                albumFile.mSelected = !albumFile.mInvalid && mSelectedList != null && mSelectedList.contains(albumFile);
                all.add(albumFile);
                MediaFolder albumFolder = map.get(folder);
                if (albumFolder != null) albumFolder.add(albumFile);
                else {
                    albumFolder = new MediaFolder(folder);
                    albumFolder.add(albumFile);
                    map.put(folder, albumFolder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }

    private synchronized void loadMediaImage(@NonNull final Context context, @MediaType final int mediaType,
            @NonNull final ConcurrentHashMap<String, MediaFolder> map, @NonNull final MediaFolder all) {
        final Uri uri = getMediaUri(mediaType);
        final Cursor cursor = query(context.getContentResolver(), uri, IMAGE);
        if (null == cursor) return;
        try {
            MediaFile albumFile = null;
            while (cursor.moveToNext()) {
                final int id = cursor.getInt(0);
                final String path = cursor.getString(1);
                final String mimeType = cursor.getString(2);
                final String folder = cursor.getString(3);
                final int width = cursor.getInt(4);
                final int height = cursor.getInt(5);
                final long size = cursor.getLong(6);
                final long date = cursor.getLong(7);
                albumFile = new MediaFile(mediaType);
                albumFile.id = id;
                albumFile.mPath = path;
                albumFile.mUri = UriUtil.getUri(context, uri, id, path);
                albumFile.mFolder = folder;
                albumFile.mMimeType = mimeType;
                albumFile.mWidth = width;
                albumFile.mHeight = height;
                albumFile.mSize = size;
                albumFile.mDate = date;
                albumFile.mInvalid = MediaUtil.filterFailed(albumFile, mFilterMime, mFilterSize, null);
                if (albumFile.mInvalid && !mDisplayInvalid) continue;
                albumFile.mSelected = !albumFile.mInvalid && mSelectedList != null && mSelectedList.contains(albumFile);
                all.add(albumFile);
                MediaFolder albumFolder = map.get(folder);
                if (albumFolder != null) albumFolder.add(albumFile);
                else {
                    albumFolder = new MediaFolder(folder);
                    albumFolder.add(albumFile);
                    map.put(folder, albumFolder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }


    private MediaFile convertVideo(Context context, Uri uri) {
        LogUtil.d(uri.toString(), uri.getPath());
        Cursor cursor = query(context.getContentResolver(), uri, VIDEO);
        if (null == cursor) return null;
        MediaFile albumFile = null;
        try {
            cursor.moveToFirst();
            final Uri queryUri = getMediaUri(mMediaType);
            int id = cursor.getInt(0);
            String path = cursor.getString(1);
            String mimeType = cursor.getString(2);
            String folder = cursor.getString(3);
            int width = cursor.getInt(4);
            int height = cursor.getInt(5);
            long size = cursor.getLong(6);
            long date = cursor.getLong(7);
            long duration = cursor.getLong(8);
            albumFile = new MediaFile(mMediaType);
            albumFile.id = id;
            albumFile.mMimeType = mimeType;
            albumFile.mPath = path;
            albumFile.mUri = UriUtil.getUri(context, queryUri, id, path);
            albumFile.mFolder = folder;
            albumFile.mWidth = width;
            albumFile.mHeight = height;
            albumFile.mSize = size;
            albumFile.mDate = date;
            albumFile.mDuration = duration;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return albumFile;
    }

    private MediaFile convertImage(Context context, Uri uri) {
        final Cursor cursor = query(context.getContentResolver(), uri, IMAGE);
        if (null == cursor) return null;
        MediaFile albumFile = null;
        try {
            cursor.moveToFirst();
            final Uri queryUri = getMediaUri(mMediaType);
            final int id = cursor.getInt(0);
            final String path = cursor.getString(1);
            final String mimeType = cursor.getString(2);
            final String folder = cursor.getString(3);
            final int width = cursor.getInt(4);
            final int height = cursor.getInt(5);
            final long size = cursor.getLong(6);
            final long date = cursor.getLong(7);
            albumFile = new MediaFile(mMediaType);
            albumFile.id = id;
            albumFile.mMimeType = mimeType;
            albumFile.mPath = path;
            albumFile.mUri = UriUtil.getUri(context, queryUri, id, path);
            albumFile.mFolder = folder;
            albumFile.mWidth = width;
            albumFile.mHeight = height;
            albumFile.mSize = size;
            albumFile.mDate = date;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return albumFile;
    }


    @NonNull
    private Uri getMediaUri(@MediaType int mediaType) {
        switch (mediaType) {
            case MediaType.AUDIO:
                return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            case MediaType.VIDEO:
                return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            case MediaType.IMAGE:
            default:
                return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
    }


    @Nullable
    private Cursor query(@Nullable final ContentResolver resolver, @Nullable final Uri uri, @Nullable final String[] projection) {
        if (null == resolver || null == uri) return null;
        return resolver.query(uri, projection, SELECTED, null, ORDER);
    }


}
