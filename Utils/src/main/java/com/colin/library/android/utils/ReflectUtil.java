package com.colin.library.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 作者： ColinLu
 * 时间： 2020-10-27 19:50
 * <p>
 * 描述： 常用反射
 */
public final class ReflectUtil {
    private ReflectUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*反射获取当前 Activity*/
    @Nullable
    public static Activity getActivity(@Nullable final Context context) {
        if (context != null && context.getClass().getName().equals("com.android.internal.policy.DecorContext")) {
            try {
                Field mActivityContextField = context.getClass().getDeclaredField("mActivityContext");
                mActivityContextField.setAccessible(true);
                //noinspection unchecked
                return ((WeakReference<Activity>) mActivityContextField.get(context)).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*获取Application*/
    @Nullable
    @SuppressLint("PrivateApi")
    private static Application getApplicationByReflect() {
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app instanceof Application) return (Application) app;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
