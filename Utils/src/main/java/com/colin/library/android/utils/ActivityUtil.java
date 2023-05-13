package com.colin.library.android.utils;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import java.util.Locale;

/**
 * 作者： ColinLu
 * 时间： 2020-10-27 19:41
 * <p>
 * 描述： Activity工具类
 */
public final class ActivityUtil {
    private ActivityUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }


    ///////////////////////////////////////////////////////////////////////////
    // 工具类
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 获取 View context 所属的 Activity
     *
     * @param view {@link View}
     * @return {@link Activity}
     */
    @Nullable
    public static Activity getActivity(@Nullable final View view) {
        return null == view ? null : getActivity(view.getContext());
    }

    /**
     * 获取 当前 context 所属的Activity
     *
     * @param context 当前上下文
     * @return {@link Activity}
     */
    public static Activity getActivity(@Nullable Context context) {
        if (null == context) return null;
        if (context instanceof Activity) return (Activity) context;
        Activity activity = ReflectUtil.getActivity(context);
        if (activity != null) return activity;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) return (Activity) context;
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    /**
     * 判断当前 Activity 是否存活
     *
     * @param context
     * @return {@link true :存活 ,false 获取失败或者销毁}
     */
    public static boolean isAlive(@Nullable final Context context) {
        return isAlive(getActivity(context));
    }

    /*判断当前 Activity 是否存活*/
    public static boolean isAlive(@Nullable final Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }

    @Nullable
    public static Context getContext(@Nullable LifecycleOwner owner) {
        if (null == owner) return null;
        if (owner instanceof Activity) {
            return (Activity) owner;
        }
        if (owner instanceof DialogFragment) {
            ((DialogFragment) owner).getActivity();
        }
        if (owner instanceof androidx.fragment.app.DialogFragment) {
            ((androidx.fragment.app.DialogFragment) owner).getActivity();
        }
        if (owner instanceof Fragment) {
            ((Fragment) owner).getActivity();
        }
        if (owner instanceof android.app.Fragment) {
            ((android.app.Fragment) owner).getActivity();
        }
        return null;
    }

    /*语言本地化处理*/
    @NonNull
    public static void initLocale(@NonNull final Context context, @NonNull final Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            final Configuration configuration = context.getResources().getConfiguration();
            configuration.setLocale(locale);
            context.createConfigurationContext(configuration);
        } else {
            final Resources resources = context.getResources();
            final Configuration configuration = resources.getConfiguration();
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
    }
}
