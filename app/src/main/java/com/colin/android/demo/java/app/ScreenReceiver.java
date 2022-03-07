package com.colin.android.demo.java.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.def.ScreenStatus;
import com.colin.library.android.base.BaseReceiver;
import com.colin.library.android.base.def.OnReceiverListener;


/**
 * 作者： ColinLu
 * 时间： 2020-12-06 18:40
 * <p>
 * 描述： 屏幕广播 亮屏  息屏  重新打开
 */
public class ScreenReceiver extends BaseReceiver {
    /*Activity 绑定广播*/
    public static void bind(@Nullable final OnScreenBroadcastListener listener) {
        if (null == listener) return;
        listener.getLifecycle().addObserver(new ScreenReceiver(listener));
    }

    public interface OnScreenBroadcastListener extends OnReceiverListener {
        void screen(@NonNull String action);
    }

    public ScreenReceiver(@Nullable OnScreenBroadcastListener listener) {
        super(listener);
    }


    /*接受到通知*/
    @Override
    public void onReceive(Context context, Intent intent) {
        final OnScreenBroadcastListener listener = null == mOnBroadcastListenerRef ? null : (OnScreenBroadcastListener) mOnBroadcastListenerRef.get();
        final String action = intent.getAction();
        if (null == listener || null == listener.getContext() || TextUtils.isEmpty(action)) return;
        listener.screen(action);
    }

    @Nullable
    @Override
    public IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);      //开屏
        filter.addAction(Intent.ACTION_SCREEN_OFF);     //锁屏
        filter.addAction(Intent.ACTION_USER_PRESENT);   //解锁
        return filter;
    }
}
