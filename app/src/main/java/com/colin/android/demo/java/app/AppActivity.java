package com.colin.android.demo.java.app;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.colin.library.android.annotation.NetType;
import com.colin.library.android.base.BaseActivity;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.NetUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 作者： ColinLu
 * 时间： 2021-12-24 23:11
 * <p>
 * 描述： 构建App基本逻辑
 */
public abstract class AppActivity<VB extends ViewBinding> extends BaseActivity implements ScreenReceiver.OnScreenBroadcastListener, NetBroadReceiver.OnNetListener {
    protected VB mBinding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = reflectViewBinding();
        setContentView(mBinding.getRoot(), savedInstanceState);
        ScreenReceiver.bind(this);
        NetBroadReceiver.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    @Override
    public void network(@NetType int type) {
        LogUtil.i(getClass().getSimpleName(), NetUtil.getNetType(type));
    }

    @Override
    public void screen(@NonNull String action) {
        LogUtil.i(action);
    }


    private VB reflectViewBinding() throws IllegalStateException {
        try {
            final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            final Class cls = (Class) type.getActualTypeArguments()[0];
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            return (VB) inflate.invoke(null, getLayoutInflater());
        } catch (Exception e) {
            LogUtil.log(e);
        }
        throw new IllegalStateException("reflectViewBinding fail");
    }
}
