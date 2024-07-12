package com.colin.library.android.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;

import com.colin.library.android.utils.data.Constants;

import java.io.File;

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

    ///////////////////////////////////////////////////////////////////////////
    // startActivity
    ///////////////////////////////////////////////////////////////////////////
    public static void start(@NonNull Context context, @Nullable Class<?> cls, @Nullable Bundle bundle) {
        start(context, cls, bundle, false);
    }

    public static void start(@NonNull Context context, @Nullable Class<?> cls, @Nullable Bundle bundle, boolean close) {
        final Intent intent = new Intent(context, cls);
        if (bundle != null) intent.putExtras(bundle);
        start(context, intent, close);
    }

    public static void start(@NonNull Context context, @NonNull Intent intent) {
        start(context, intent, false);
    }

    public static void start(@NonNull Context context, @NonNull Intent intent, boolean close) {
        if (context instanceof Activity) start((Activity) context, intent, close);
        else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void start(@NonNull Activity activity, @NonNull Intent intent) {
        activity.startActivity(intent);
    }

    public static void start(@NonNull Activity activity, @NonNull Intent intent, boolean close) {
        activity.startActivity(intent);
        if (close) activity.finish();
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////
    public static void requestImage(@Nullable Activity activity, @IntRange(from = 0, to = 65535) int request) {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(Constants.MIME_TYPE_IMAGE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        request(activity, intent, request);
    }

    public static void requestImage(@Nullable Fragment fragment, @IntRange(from = 0, to = 65535) int request) {
        final Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(Constants.MIME_TYPE_IMAGE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        request(fragment, intent, request);
    }

    public static void requestVideo(@Nullable Activity activity, @Nullable Uri uri, @IntRange(from = 0, to = 1) final int quality, @IntRange(from = 1) final long duration, @IntRange(from = 1) final long limitBytes, @IntRange(from = 0, to = 65535) int request) {
        if (uri == null) return;
        final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, duration);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, limitBytes);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        request(activity, intent, request);
    }

    public static void requestVideo(@Nullable Fragment fragment, @Nullable Uri uri, @IntRange(from = 0, to = 1) final int quality, @IntRange(from = 1) final long duration, @IntRange(from = 1) final long limitBytes, @IntRange(from = 0, to = 65535) int request) {
        if (uri == null) return;
        final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, duration);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, limitBytes);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        request(fragment, intent, request);
    }

    public static void requestAudio(@Nullable Activity activity, @IntRange(from = 0, to = 65535) int request) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr"); //String AUDIO_AMR = "audio/amr";
        intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.SoundRecorder");
        request(activity, intent, request);
    }

    public static void requestAudio(@Nullable Fragment fragment, @IntRange(from = 0, to = 65535) int request) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr"); //String AUDIO_AMR = "audio/amr";
        intent.setClassName("com.android.soundrecorder", "com.android.soundrecorder.SoundRecorder");
        request(fragment, intent, request);
    }

    public static void request(@Nullable Activity activity, @Nullable Intent intent, @IntRange(from = 0, to = 65535) int request) {
        if (isAvailable(activity, intent)) activity.startActivityForResult(intent, request);
    }

    public static void request(@Nullable Fragment fragment, @Nullable Intent intent, @IntRange(from = 0, to = 65535) int request) {
        if (!isAvailable(fragment == null ? null : fragment.requireActivity(), intent)) return;
        fragment.startActivityForResult(intent, request);
    }

    /**
     * 重新启动 app
     *
     * @param context
     */
    public static void relaunchApp(@Nullable final Context context) {
        relaunchApp(context, false);
    }

    /**
     * 重新启动 app
     *
     * @param context
     * @param killProcess True to kill the process, false otherwise.
     */
    public static void relaunchApp(@Nullable final Context context, final boolean killProcess) {
        if (context == null) return;
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) return;
        start(context, intent);
        if (killProcess) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /*跳转系统设置界面*/
    public static void toSystemSettingView(@Nullable final Context context) {
        if (context != null) start(context, new Intent(Settings.ACTION_SETTINGS));
    }

    /*跳转拨号界面 自动拨号*/
    @RequiresPermission(Manifest.permission.CALL_PHONE)
    public static void toCallView(@Nullable final Context context, @Nullable final String mobile) {
        if (context != null && !StringUtil.isEmpty(mobile)) {
            start(context, new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile)));
        }
    }

    /*跳转拨号界面*/
    public static void toDialView(@Nullable final Context context, @Nullable final String mobile) {
        if (context != null && !StringUtil.isEmpty(mobile)) {
            start(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobile)));
        }
    }

    /*跳转短信编辑界面*/
    public static void toSMSView(@Nullable final Context context, @Nullable final String mobile) {
        toSMSView(context, mobile, null);
    }

    /*跳转短信编辑界面*/
    public static void toSMSView(@Nullable final Context context, @Nullable final String mobile, @Nullable final String body) {
        if (context == null || StringUtil.isEmpty(mobile)) return;
        final Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mobile));
        if (!StringUtil.isEmpty(body)) intent.putExtra("sms_body", body);
        start(context, intent);
    }

    /*跳转浏览器*/
    public static void toWebView(@Nullable final Context context, @Nullable final String url) {
        if (!TextUtils.isEmpty(url)) toWebView(context, Uri.parse(url));
    }

    /*跳转浏览器*/
    public static void toWebView(@Nullable final Context context, @Nullable final Uri uri) {
        if (null == context || null == uri) return;
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE);
        start(context, intent);
    }

    public static void toWifiView(@Nullable Context context) {
        if (context != null) start(context, new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    public static void toFileView(@Nullable Context context, @Nullable File file) {
        if (null == context || !FileUtil.isExists(file)) return;
        final Uri uri = UriUtil.getUri(context, file);
        final String type = context.getContentResolver().getType(uri);
        final Intent intent = new Intent(Intent.ACTION_VIEW).setDataAndType(uri, type);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        start(context, intent);
    }

    /*跳转权限设置界面*/
    public static void toPermissionView(@Nullable final Context context) {
        if (null == context) return;
        if (OSUtil.isMiui()) toMiUiPermissionView(context);
        else if (OSUtil.isFlyme()) toFlymePermissionView(context);
        else if (OSUtil.isEmui()) toEmuiPermissionView(context);
        else toAppSettingView(context);
    }

    /*跳转 APP详情设置界面*/
    public static void toAppSettingView(@Nullable final Context context) {
        if (null == context) return;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        if (isAvailable(context, intent)) start(context, intent);
        else toActionView(context, Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
    }

    /**
     * 蓝牙设置 Settings.ACTION_BLUETOOTH_SETTINGS
     * 移动网设置 Settings.ACTION_DATA_ROAMING_SETTINGS
     *
     * @param context
     * @param action
     */
    public static void toActionView(@Nullable final Context context, @NonNull final String action) {
        if (context != null) start(context, new Intent(action));
    }

    public static void toActionView(@Nullable final Context context, @NonNull final String action, @Nullable final Uri uri) {
        if (context != null) start(context, new Intent(action, uri));
    }

    public static void toActionView(@Nullable final Context context, @NonNull final String action, @Nullable final Uri uri, @Nullable final String type) {
        if (context != null) start(context, new Intent(action).setDataAndType(uri, type));
    }

    private static void toEmuiPermissionView(@NonNull final Context context) {
        final Intent intent = new Intent();
        final ComponentName name = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
        intent.setComponent(name);
        if (isAvailable(context, intent)) start(context, intent);
        else toAppSettingView(context);
    }

    private static void toFlymePermissionView(@NonNull final Context context) {
        final String action = "com.meizu.safe.security.SHOW_APPSEC";
        Intent intent = new Intent(action);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        if (isAvailable(context, intent)) start(context, intent);
        else toAppSettingView(context);
    }

    private static void toMiUiPermissionView(@NonNull final Context context) {
        final String action = "miui.intent.action.APP_PERM_EDITOR";
        Intent intent = new Intent(action);
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (isAvailable(context, intent)) {
            start(context, intent);
            return;
        }
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (isAvailable(context, intent)) start(context, intent);
        else toAppSettingView(context);
    }

    /**
     * 判断要启动的intent是否可用
     *
     * @return boolean
     */
    @SuppressLint("QueryPermissionsNeeded")
    public static boolean isAvailable(@Nullable Context context, @Nullable Intent intent) {
        if (null == context || null == intent) return false;
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
