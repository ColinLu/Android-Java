package com.colin.library.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.colin.library.android.utils.data.Constants;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 00:29
 * <p>
 * 描述： Intent 工具类 跳转系统界面
 */
public final class IntentUtil {
    private IntentUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static void start(@Nullable Context context, @Nullable Bundle bundle) {

    }

    public static void requestImage(@Nullable Activity activity, @IntRange(from = 0, to = 65535) int request) {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(Constants.MIME_TYPE_IMAGE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        request(activity, intent, request);
    }

    public static void requestAudio(@Nullable Activity activity, @IntRange(from = 0, to = 65535) int request) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr"); //String AUDIO_AMR = "audio/amr";
        intent.setClassName("com.android.soundrecorder",
                "com.android.soundrecorder.SoundRecorder");
    }

    public static void requestVideo(@Nullable Activity activity, @Nullable Uri uri,
                                    @IntRange(from = 0, to = 1) final int quality,
                                    @IntRange(from = 1) final long duration,
                                    @IntRange(from = 1) final long limitBytes,
                                    @IntRange(from = 0, to = 65535) int request) {
        if (uri == null) return;
        final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, duration);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, limitBytes);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        request(activity, intent, request);
    }

    public static void request(@Nullable Activity activity, @Nullable Intent intent, @IntRange(from = 0, to = 65535) int request) {
        if (!isAvailable(activity, intent)) return;
        activity.startActivityForResult(intent, request);
        activity.overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    public static void request(@Nullable Fragment fragment, @Nullable Intent intent, @IntRange(from = 0, to = 65535) int request) {
        if (!isAvailable(fragment == null ? null : fragment.requireActivity(), intent)) return;
        fragment.startActivityForResult(intent, request);
    }

    /**
     * 判断要启动的intent是否可用
     *
     * @return boolean
     */
    @SuppressLint("QueryPermissionsNeeded")
    public static boolean isAvailable(@Nullable Context context, @Nullable Intent intent) {
        if (null == context || null == intent) return false;
        return context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    public static void toPermissionSettings(@Nullable Context context) {

    }
}
