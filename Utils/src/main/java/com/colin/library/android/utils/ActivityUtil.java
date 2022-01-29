package com.colin.library.android.utils;

import android.app.Activity;

import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:53
 * <p>
 * 描述： TODO
 */
public final class ActivityUtil {


    public static boolean isLive(@Nullable Activity activity) {
        return activity != null && !activity.isFinishing();
    }
}
