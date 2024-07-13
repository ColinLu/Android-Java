package com.colin.library.android.helper;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.LogUtil;

import java.util.Stack;

/**
 * 作者： ColinLu
 * 时间： 2022-11-28 19:17
 * <p>
 * 描述： Activity 辅助类
 */
public final class ActivityHelper {
    private final ActivityHelper.ActivityLifecycleImpl ACTIVITY_LIFECYCLE;
    private final Stack<Activity> ACTIVITY_STACK;                   //管理运行Activity
    private int mCount = 0;                                         //大于0 标示正在前台工作
    private static volatile ActivityHelper sHelper;

    private ActivityHelper() {
        ACTIVITY_LIFECYCLE = new ActivityLifecycleImpl();
        ACTIVITY_STACK = new Stack<>();
    }


    public static ActivityHelper getInstance() {
        if (sHelper == null) {
            synchronized (ActivityHelper.class) {
                if (sHelper == null) sHelper = new ActivityHelper();
            }
        }
        return sHelper;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 对外公开方法
    ///////////////////////////////////////////////////////////////////////////
    /*注册*/
    public void register(@NonNull final Application application) {
        // 先移除监听
        try {
            application.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } catch (Exception e) {
            LogUtil.log(e);
        }
        try {
            application.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } catch (Exception e) {
            LogUtil.log(e);
        }
    }

    /*解绑*/
    public void unregister(@NonNull final Application application) {
        // 先移除监听
        try {
            application.unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE);
        } catch (Exception e) {
            LogUtil.log(e);
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
        return ACTIVITY_STACK.isEmpty() ? null : ACTIVITY_STACK.lastElement();
    }

    /*获取指定的Activity*/
    @Nullable
    public Activity getActivity(@Nullable final Class<? extends Activity> cls) {
        if (null == cls || getActivitySize() == 0) return null;
        for (Activity activity : ACTIVITY_STACK) {
            if (activity.getClass().equals(cls)) return activity;
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
