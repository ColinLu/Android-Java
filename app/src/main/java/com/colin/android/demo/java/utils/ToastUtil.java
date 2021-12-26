package com.colin.android.demo.java.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.colin.android.demo.java.utils.data.UtilHelper;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 13:43
 * <p>
 * 描述： Toast
 */
public final class ToastUtil {
    private static long sEndTime = 0;

    private ToastUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }


    public static void show(@StringRes final int res) {
        show(UtilHelper.getInstance().getContext(), res, Toast.LENGTH_SHORT);
    }

    public static void show(@StringRes final int res, final int duration) {
        show(UtilHelper.getInstance().getContext(), res, duration);
    }

    public static void show(@Nullable final Context context, @StringRes final int res, final int duration) {
        if (null == context || res == Resources.ID_NULL) return;
        show(context, ResourceUtil.getString(context, res), duration);
    }

    public static void show(@Nullable final CharSequence msg) {
        show(UtilHelper.getInstance().getContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void show(@Nullable Context context, @Nullable CharSequence msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }


    public static void show(@Nullable Context context, @Nullable CharSequence msg, final int duration) {
        if (null == context || TextUtils.isEmpty(msg)) return;
        long startTime = System.currentTimeMillis();
        if (sEndTime > 0 && startTime - sEndTime <= 1000) return;
        sEndTime = startTime;
        Toast.makeText(context, msg, duration).show();
    }
}
