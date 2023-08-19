package com.colin.library.android.media.util;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.colin.library.android.media.def.Constants;
import com.colin.library.android.media.def.Filter;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.provider.AppFileProvider;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.PathUtil;
import com.colin.library.android.utils.TimeUtil;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 23:20
 * <p>
 * 描述： TODO
 */
public final class MediaUtil {

    /**
     * @param mediaType {@link MediaType}
     * @return eg:{@link Environment#DIRECTORY_DCIM}
     */
    @NonNull

    public static String getFileType(@MediaType final int mediaType) {
        switch (mediaType) {
            case MediaType.IMAGE:
                return Environment.DIRECTORY_PICTURES;
            case MediaType.AUDIO:
                return Environment.DIRECTORY_MUSIC;
            case MediaType.VIDEO:
                return Environment.DIRECTORY_MOVIES;
            case MediaType.UNKNOWN:
            default:
                return Environment.DIRECTORY_DOWNLOADS;
        }
    }

    @NonNull
    public static Uri insertUri(@MediaType final int mediaType, @NonNull String volumeName) {
        switch (mediaType) {
            case MediaType.AUDIO:
                return MediaStore.Audio.Media.getContentUri(volumeName);
            case MediaType.VIDEO:
                return MediaStore.Video.Media.getContentUri(volumeName);
            case MediaType.IMAGE:
            default:
                return MediaStore.Images.Media.getContentUri(volumeName);
        }
    }

    /*根据文件类型取名*/
    @NonNull
    public static String getFileName(@MediaType final int mediaType) {
        return getFileName(System.currentTimeMillis(), mediaType);
    }

    /*根据文件类型取名*/
    @NonNull
    public static String getFileName(long time, @MediaType final int mediaType) {
        switch (mediaType) {
            case MediaType.IMAGE:
                return TimeUtil.getTimeString(time) + ".png";
            case MediaType.AUDIO:
                return TimeUtil.getTimeString(time) + ".mp3";
            case MediaType.VIDEO:
                return TimeUtil.getTimeString(time) + ".mp4";
            case MediaType.UNKNOWN:
            default:
                return TimeUtil.getTimeString(time);
        }
    }

    public static String getMimeType(@MediaType int mediaType) {
        switch (mediaType) {
            case MediaType.VIDEO:
                return Constants.MEDIA_TYPE_VIDEO;
            case MediaType.AUDIO:
                return Constants.MEDIA_TYPE_AUDIO;
            case MediaType.IMAGE:
                return Constants.MEDIA_TYPE_IMAGE;
            default:
                return Constants.MEDIA_TYPE_ALL;
        }
    }

    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createFile(@NonNull Context context, @MediaType final int mediaType) {
        final File file = new File(getFolder(context, mediaType), getFileName(mediaType));
        FileUtil.createFile(file);
        return file;
    }

    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File createFile(@NonNull Context context, @NonNull final String fileName, @MediaType final int mediaType) {
        final File file = new File(getFolder(context, mediaType), fileName);
        FileUtil.createFile(file);
        return file;
    }

    /*获取指定文件夹 eg:  /storage/emulated/0/DCIM*/
    @RequiresPermission(READ_EXTERNAL_STORAGE)
    public static File getFolder(@NonNull final Context context, @MediaType final int mediaType) {
        return getFolder(context, getFileType(mediaType));
    }

    /*获取多媒体文件 eg: /storage/emulated/0/Movies/20200716_114253649.jpg */
    @NonNull
    @RequiresPermission(allOf = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE})
    public static File getFile(@NonNull final File parent, @NonNull final String fileName) {
        boolean createFolder = FileUtil.createOrExistsDir(parent);
        if (!createFolder) throw new NullPointerException("folder create fail");
        File file = new File(parent, fileName);
        boolean flag = true;
        if (file.exists()) flag = file.delete();
        if (!flag) throw new NullPointerException("file is exists  delete fail");
        return file;
    }


    /*获取指定文件夹 eg:  /storage/emulated/0/DCIM*/
    @RequiresPermission(READ_EXTERNAL_STORAGE)
    public static File getFolder(@NonNull final Context context, @Nullable final String type) {
        if (PathUtil.canWrite()) return Environment.getExternalStoragePublicDirectory(type);
        return context.getExternalFilesDir(type);
    }

    @NonNull
    public static Uri getUri(@NonNull final Context context, @NonNull final Uri uri, int id, @NonNull String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return uri.buildUpon()
                      .appendPath(String.valueOf(id))
                      .build();
        }
        return AppFileProvider.getUri(context, new File(path));
    }

    @NonNull
    public static Uri getUri(@NonNull final Context context, @NonNull final Uri uri, int id, @Nullable String authority, @NonNull String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return uri.buildUpon()
                      .appendPath(String.valueOf(id))
                      .build();
        }
        return AppFileProvider.getUri(context, new File(path));
    }

    /*File 转 Uri*/
    @NonNull
    public static Uri getUri(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return AppFileProvider.getUri(context, file);
        }
        return Uri.fromFile(file);
    }

    /*筛选*/
    public static boolean filterFailed(@NonNull final MediaFile albumFile, @Nullable final Filter<String> filterMime,
            @Nullable final Filter<Long> filterSize, @Nullable final Filter<Long> filterDuration) {
        if (filterSize != null && !filterSize.filter(albumFile.mSize)) return false;
        if (filterMime != null && !filterMime.filter(albumFile.mMimeType)) return false;
        return albumFile.mMediaType == MediaType.IMAGE || null == filterDuration || filterDuration.filter(albumFile.mDuration);
    }

    /*筛选*/
    public static boolean filter(@NonNull final MediaFile albumFile, @Nullable final Filter<String> filterMime,
            @Nullable final Filter<Long> filterSize, @Nullable final Filter<Long> filterDuration) {
        if (filterSize != null && !filterSize.filter(albumFile.mSize)) return false;
        if (filterMime != null && !filterMime.filter(albumFile.mMimeType)) return false;
        return albumFile.mMediaType == MediaType.IMAGE || null == filterDuration || filterDuration.filter(albumFile.mDuration);
    }

    @NonNull
    public static Uri createImageUri(@NonNull Context context) {
        return createUri(context, MediaType.IMAGE);
    }

    public static Uri createAudioUri(@NonNull Context context) {
        return createUri(context, MediaType.AUDIO);
    }

    public static Uri createVideoUri(@NonNull Context context) {
        return createUri(context, MediaType.VIDEO);
    }

    @NonNull
    public static Uri createUri(@NonNull Context context, @MediaType int mediaType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return createUriByQ(context, mediaType);
        }
        final long time = System.currentTimeMillis();

        if (PathUtil.canWrite()) {
            final ContentValues values = new ContentValues(4);
            values.put(MediaStore.MediaColumns.DATE_TAKEN, time);
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, getFileName(time, mediaType));
            values.put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(mediaType));
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, getFileType(mediaType));
            return context.getContentResolver()
                          .insert(insertUri(mediaType, "external"), values);
        } else {
            final ContentValues values = new ContentValues(3);
            values.put(MediaStore.MediaColumns.DATE_TAKEN, time);
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, getFileName(time, mediaType));
            values.put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(mediaType));
            return context.getContentResolver()
                          .insert(insertUri(mediaType, "internal"), values);
        }
    }

    @NonNull
    private static Uri createUriByQ(@NonNull Context context, @MediaType int mediaType) {
        final DisplayMetrics metrics = context.getResources()
                                              .getDisplayMetrics();
        final long time = System.currentTimeMillis();
        final ContentValues values = new ContentValues(4);
        values.put(MediaStore.MediaColumns.DATE_TAKEN, time);
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, getFileName(time, mediaType));
        values.put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(mediaType));
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, getFileType(mediaType));
        values.put(MediaStore.MediaColumns.SIZE, getFileType(mediaType));
        values.put(MediaStore.MediaColumns.WIDTH, metrics.widthPixels);
        values.put(MediaStore.MediaColumns.HEIGHT, metrics.heightPixels);
        return context.getContentResolver()
                      .insert(insertUri(mediaType, "external"), values);
    }
}
