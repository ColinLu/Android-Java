package com.colin.android.demo.java.app;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.colin.library.android.base.BaseActivity;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.NetUtil;
import com.colin.library.android.utils.annotation.NetType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 作者： ColinLu
 * 时间： 2021-12-24 23:11
 * <p>
 * 描述： 构建App基本逻辑
 */
public abstract class AppActivity<Bind extends ViewBinding> extends BaseActivity implements
        ScreenReceiver.OnScreenBroadcastListener, NetBroadReceiver.OnNetListener {
    protected Bind mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        final Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class);
            mBinding = (Bind) inflate.invoke(null, getLayoutInflater());
            setContentView(mBinding.getRoot());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        initView(savedInstanceState);
        initData(getIntent().getExtras());
        ScreenReceiver.bind(this);
        NetBroadReceiver.bind(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }

    @Override
    public int layoutRes() {
        return Resources.ID_NULL;
    }

    @Override
    public void network(@NetType int type) {
        LogUtil.i(NetUtil.getNetType(type));
    }

    @Override
    public void screen(@NonNull String action) {
        LogUtil.i(action);
    }
}
