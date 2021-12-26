package com.colin.android.demo.java.app;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.colin.android.demo.java.base.BaseActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 作者： ColinLu
 * 时间： 2021-12-24 23:11
 * <p>
 * 描述： TODO
 */
public abstract class AppActivity<Bind extends ViewBinding> extends BaseActivity<Bind> {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding = null;
    }
}
