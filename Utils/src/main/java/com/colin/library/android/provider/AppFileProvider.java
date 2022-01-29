package com.colin.library.android.provider;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2022-01-28 22:57
 * <p>
 * 描述： 适配Android 7
 */
///////////////////////////////////////////////////////////////////////////
//    manifest 配置
//     <provider
//            android:name="com.colin.library.android.provider.AppFileProvider"
//            android:authorities="${applicationId}.provider"
//            android:exported="false"
//            android:grantUriPermissions="true"
//            android:multiprocess="true">
//            <meta-data
//                android:name="android.support.FILE_PROVIDER_PATHS"
//                android:resource="@xml/app_provider" />
//        </provider>
///////////////////////////////////////////////////////////////////////////
public final class AppFileProvider extends FileProvider {

    /**
     * 文件 转化uri
     *
     * @param context
     * @param file
     * @return
     */
    @NonNull
    public static Uri getUri(@NonNull Context context, @NonNull File file) {
        return getUriForFile(context, getAuthority(context), file);
    }

    @NonNull
    public static String getAuthority(@NonNull Context context) {
        return context.getPackageName() + ".provider";
    }
}
