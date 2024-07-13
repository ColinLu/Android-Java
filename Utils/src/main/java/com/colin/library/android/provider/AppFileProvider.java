package com.colin.library.android.provider;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.colin.library.android.helper.UtilHelper;

import java.io.File;

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
    @NonNull
    public static Uri getUri(@NonNull File file) {
        return getUri(UtilHelper.getInstance().getUtilConfig().getApplication(), file);
    }

    /**
     * 文件 转化uri
     *
     * @param context 运用上下文
     * @param file    当前文档
     * @return 放回Uri
     */
    @NonNull
    public static Uri getUri(@NonNull Context context, @NonNull File file) {
        return getUriForFile(context, getAuthority(context), file);
    }

    @NonNull
    public static String getAuthority() {
        return getAuthority(UtilHelper.getInstance().getUtilConfig().getApplication());
    }

    @NonNull
    public static String getAuthority(@NonNull Context context) {
        return context.getPackageName() + ".provider";
    }
}