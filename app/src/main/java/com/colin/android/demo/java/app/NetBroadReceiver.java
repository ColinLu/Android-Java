package com.colin.android.demo.java.app;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LifecycleOwner;

import com.colin.library.android.annotation.NetType;
import com.colin.library.android.base.BaseReceiver;
import com.colin.library.android.base.def.OnReceiverListener;
import com.colin.library.android.utils.NetUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-03-07 20:51
 * <p>
 * 描述： 网络状态监听
 */
public final class NetBroadReceiver extends BaseReceiver {

    /*Activity 绑定广播*/
    public static void bind(@Nullable final OnNetListener listener) {
        if (null == listener) return;
        listener.getLifecycle().addObserver(new NetBroadReceiver(listener));
    }

    @NetType
    private int mCurrentNetType = NetType.NETWORK_NONE;

    public NetBroadReceiver(@NonNull OnReceiverListener listener) {
        super(listener);
    }


    @RequiresPermission(ACCESS_NETWORK_STATE)
    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        super.onStart(owner);
        network(NetUtil.getNetType());
    }

    /*接受到通知*/
    @Override
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            network(NetUtil.getNetType());
        }
    }

    @NonNull
    @Override
    public IntentFilter getIntentFilter() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        return filter;
    }

    private void network(@NetType int netType) {
        if (mCurrentNetType == netType) {
            return;
        }
        this.mCurrentNetType = netType;
        final OnNetListener listener = (OnNetListener) mOnBroadcastListenerRef.get();
        if (listener != null && listener.getContext() != null) {
            listener.network(netType);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 网络状态变化接口
    ///////////////////////////////////////////////////////////////////////////
    public interface OnNetListener extends OnReceiverListener {
        void network(@NetType int type);
    }
}
