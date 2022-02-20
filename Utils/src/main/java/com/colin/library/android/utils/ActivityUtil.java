package com.colin.library.android.utils;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;


import java.util.Stack;

/**
 * 作者： ColinLu
 * 时间： 2020-10-27 19:41
 * <p>
 * 描述： Activity工具类
 */
public final class ActivityUtil {
    private final ActivityLifecycleImpl ACTIVITY_LIFECYCLE;
    private final Stack<Activity> ACTIVITY_STACK;                   //管理运行Activity
    private int mCount = 0;                                         //大于0 标示正在前台工作


    private ActivityUtil() {
        ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();
        ACTIVITY_STACK = new Stack<>();
    }


    private static class Holder {
        private static final ActivityUtil instance = new ActivityUtil();
    }

    public static ActivityUtil getInstance() {
        return ActivityUtil.Holder.instance;
    }

    /*注册*/
    public void register(@NonNull final Application application) {
        // 先移除监听
        try {
            application.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } catch (Exception e) {
            LogUtil.e(e);
        }
        try {
            application.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } catch (Exception e) {
            LogUtil.e(e);
        }
    }

    public void startActivity() {
        mCount++;
    }

    public void stopActivity() {
        mCount--;
    }

    /*大于0 标示APP有Activity活动 前台进程*/
    public int getCount() {
        return mCount;
    }


    /*管理栈中有多少个Activity*/
    public int getActivitySize() {
        return ACTIVITY_STACK.size();
    }

    /*管理栈Activity*/
    public Stack<Activity> getActivityList() {
        return ACTIVITY_STACK;
    }

    /*添加入管理栈*/
    public void add(@NonNull final Activity activity) {
        ACTIVITY_STACK.addElement(activity);
    }

    /*从栈中获取最近加入的Activity 也就是栈顶Activity*/
    @Nullable
    public Activity getCurrentActivity() {
        return ACTIVITY_STACK.size() == 0 ? null : ACTIVITY_STACK.lastElement();
    }

    /*获取指定的Activity*/
    @Nullable
    public Activity getActivity(@Nullable final Class<? extends Activity> cls) {
        if (null == cls || getActivitySize() == 0) return null;
        for (Activity activity : ACTIVITY_STACK) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /*移除栈*/
    public void remove(@Nullable final Activity activity) {
        if (null != activity && getActivitySize() > 0) ACTIVITY_STACK.removeElement(activity);
    }

    /*关闭指定Activity*/
    public void finish(@Nullable final Activity activity) {
        if (null == activity || activity.isFinishing()) return;
        ACTIVITY_STACK.remove(activity);
        activity.finish();
    }


    /*关闭指定Activity*/
    public void finish(@Nullable final Class<? extends Activity> cls) {
        if (null == cls || getActivitySize() == 0) return;
        for (Activity activity : ACTIVITY_STACK) {
            if (activity.getClass().equals(cls)) {
                finish(activity);
                break;
            }
        }
    }


    /*结束所有Activity*/
    public void finishAll() {
        if (getActivitySize() == 0) return;
        for (int i = ACTIVITY_STACK.size() - 1; i > -1; i--) {
            Activity activity = ACTIVITY_STACK.get(i);
            remove(activity);
            activity.finish();
            i = ACTIVITY_STACK.size();
        }
    }

    /*清除剩下一个Activity*/
    public void finishToTop() {
        if (getActivitySize() < 1) return;
        for (int i = ACTIVITY_STACK.size() - 2; i > -1; i--) {
            Activity activity = ACTIVITY_STACK.get(i);
            remove(activity);
            activity.finish();
            i = ACTIVITY_STACK.size() - 1;
        }
    }

    /*退出应用程序*/
    public void exitApp() {
        try {
            //关闭所有Activity
            finishAll();
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            //Java方式退出
            System.exit(0);
        } catch (Throwable e) {
            LogUtil.log(e);
        }
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

    ///////////////////////////////////////////////////////////////////////////
    // ActivityLifecycleImpl
    ///////////////////////////////////////////////////////////////////////////
    private static class ActivityLifecycleImpl implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            getInstance().add(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            getInstance().startActivity();
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            getInstance().stopActivity();
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            getInstance().remove(activity);
        }
    }

}
