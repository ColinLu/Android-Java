package com.colin.android.demo.java.app;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.colin.library.android.base.BaseDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 作者： ColinLu
 * 时间： 2022-01-20 15:15
 * <p>
 * 描述： TODO
 */
public class AppDialog<Bind extends ViewBinding> extends BaseDialog<AppDialog<Bind>> {
    protected Bind mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        final Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            final Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, Boolean.class);
            mBinding = (Bind) inflate.invoke(null, inflater, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBinding.getRoot();
    }

    @LayoutRes
    @Override
    public int layoutRes() {
        return Resources.ID_NULL;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {

    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}
