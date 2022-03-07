package com.colin.library.android.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.colin.library.android.base.def.OnReceiverListener;

import java.lang.ref.SoftReference;

/**
 * 作者： ColinLu
 * 时间： 2022-03-07 20:41
 * <p>
 * 描述： 广播基类
 */
public abstract class BaseReceiver extends BroadcastReceiver implements LifecycleObserver {
    protected SoftReference<? extends OnReceiverListener> mOnBroadcastListenerRef;

    public BaseReceiver(OnReceiverListener listener) {
        mOnBroadcastListenerRef = new SoftReference<>(listener);
    }

    /*注册 广播*/
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void register() {
        final OnReceiverListener listener = null == mOnBroadcastListenerRef ? null : mOnBroadcastListenerRef.get();
        if (listener != null && listener.getContext() != null) {
            listener.getContext().registerReceiver(this, getIntentFilter());
        }
    }

    /*解绑 广播*/
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void unregister() {
        final OnReceiverListener listener = null == mOnBroadcastListenerRef ? null : mOnBroadcastListenerRef.get();
        if (listener != null && listener.getContext() != null) {
            listener.getContext().unregisterReceiver(this);
        }
    }

    /*配置广播事件*/
    @Nullable
    public abstract IntentFilter getIntentFilter();

}
