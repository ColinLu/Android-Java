package com.colin.library.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2023-01-08 22:40
 * <p>
 * 描述： 全先申请
 */
public final class PermissionUtil {

    @NonNull
    private static final List<String> APP_PERMISSION_LIST = new ArrayList<>(10);

    ///////////////////////////////////////////////////////////////////////////
    // 危险权限数组
    ///////////////////////////////////////////////////////////////////////////
    public interface Group {
        /*日历*/
        String[] CALENDAR = new String[]{Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR};
        /*相机*/
        String[] CAMERA = new String[]{Manifest.permission.CAMERA};
        /*联系人*/
        String[] CONTACTS = new String[]{Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
        /*定位*/
        String[] LOCATION = new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        /*录音*/
        String[] RECORD_AUDIO = new String[]{Manifest.permission.RECORD_AUDIO};

        /*电话号码*/
        String[] PHONE = new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG, Manifest.permission.ADD_VOICEMAIL,
                Manifest.permission.USE_SIP, Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.ANSWER_PHONE_CALLS};

        /*身体传感器*/
        String[] SENSORS = new String[]{Manifest.permission.BODY_SENSORS};

        /*短信*/
        String[] SMS = new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission.RECEIVE_MMS};
        /*存储*/
        String[] STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};


        /*拍照*/
        String[] TAKE_PICTURE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    }

    /**
     * 判断权限是否同意 ，检查配置清单权限 不需要回调
     *
     * @param context
     * @param permissions
     * @return
     */
    public static boolean request(@NonNull Context context, @NonNull @Size(min = 1) String... permissions) {
        if (context instanceof Activity) return request((Activity) context, permissions);
        else if (context instanceof ContextWrapper)
            return request(((ContextWrapper) context).getBaseContext(), permissions);
        isExists(context, permissions);
        return isGranted(context, permissions);
    }

    public static boolean request(@NonNull Fragment fragment, @NonNull @Size(min = 1) String... permissions) {
        return request(fragment.requireActivity(), permissions);
    }

    public static boolean request(@NonNull Activity activity, @NonNull @Size(min = 1) String... permissions) {
        isExists(activity, permissions);
        return isGranted(activity, permissions);
    }


    /**
     * Activity 请求权限 回调
     *
     * @param activity
     * @param request
     * @param permissions
     * @return
     */
    public static boolean request(@NonNull Activity activity, @IntRange(from = 0, to = 65535) int request,
                                  @NonNull @Size(min = 1) String... permissions) {
        final boolean granted = isGranted(activity, permissions);
        if (!granted) ActivityCompat.requestPermissions(activity, permissions, request);
        return granted;
    }

    /**
     * Activity 请求权限 回调
     *
     * @param fragment    上下文
     * @param request     权限请求码
     * @param permissions 请求的权限
     * @return
     */
    public static boolean request(@NonNull Fragment fragment, @IntRange(from = 0, to = 65535) int request, String... permissions) {
        if (null == fragment.getContext() || null == fragment.getActivity()) {
            throw new RuntimeException("fragment.getContext()-->>null");
        }
        final boolean granted = isGranted(fragment.requireActivity(), permissions);
        if (!granted) fragment.requestPermissions(permissions, request);
        return granted;
    }


    /*判断权限是否同意 不检查配置清单*/
    public static boolean isGranted(@NonNull Context context, @NonNull String... permissions) {
        for (String permission : permissions) if (!isGranted(context, permission)) return false;
        return true;
    }

    /**
     * 具体判断权限 规则 适配版本判断
     * targetSDKVersion >= 23时 需要使用ContextCompat.checkSelfPermissions || Context.checkSelfPermission
     * PermissionChecker.checkSelfPermission也会无效，
     * <p>
     * targetSDKVersion< 23 需要使用PermissionChecker.checkSelfPermissions
     * ContextCompat.checkSelfPermissions and Context.checkSelfPermission将会不起作用
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean isGranted(@NonNull Context context, @NonNull String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        else
            return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;

    }

    /**
     * 检查权限是否全部同意
     *
     * @param results
     * @return
     */
    public static boolean isGranted(@NonNull @Size(min = 1) final int[] results) {
        for (int result : results) if (result != PackageManager.PERMISSION_GRANTED) return false;
        return true;
    }

    /**
     * 判断当前权限是否点击过 “不再询问” 操作
     *
     * @param activity
     * @param permissions
     * @return
     */
    @NonNull
    public static List<String> getSetting(@NonNull Activity activity, @NonNull @Size(min = 1) String... permissions) {
        final List<String> list = new ArrayList<>(1);
        for (String permission : permissions) {
            if (isSetting(activity, permission)) list.add(permission);
        }
        return list;
    }

    private static boolean isSetting(@NonNull Activity activity, @NonNull String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        return !activity.shouldShowRequestPermissionRationale(permission);
    }

    /**
     * @return 如果允许存在于清单:true
     * 不允许存在于清单:IllegalStateException
     */
    public static boolean isExists(@NonNull Context context, @NonNull String... permissions) {
        final List<String> list = getManifestPermissions(context);
        if (permissions.length == 0) {
            throw new IllegalArgumentException("Please enter at least one permission.");
        }

        for (String p : permissions) {
            if (!list.contains(p)) {
                throw new IllegalStateException(String.format("The permission %1$s is not registered in manifest.xml", p));
            }
        }
        return true;
    }


    /**
     * Get a list of permissions in the manifest.
     */
    @NonNull
    public static List<String> getManifestPermissions(@NonNull Context context) {
        if (APP_PERMISSION_LIST.size() > 0) return APP_PERMISSION_LIST;
        try {
            final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            final String[] permissions = packageInfo.requestedPermissions;
            if (permissions == null || permissions.length == 0)
                throw new IllegalStateException("You did not register any permissions in the manifest.xml.");
            APP_PERMISSION_LIST.addAll(Collections.unmodifiableList(Arrays.asList(permissions)));
            return APP_PERMISSION_LIST;
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Package name cannot be found.");
        }
    }


}
