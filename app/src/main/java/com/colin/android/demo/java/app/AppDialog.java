package com.colin.android.demo.java.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.colin.library.android.base.BaseDialog;
import com.colin.library.android.utils.LogUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 作者： ColinLu
 * 时间： 2022-01-20 15:15
 * <p>
 * 描述： TODO
 */
public abstract class AppDialog<VB extends ViewBinding> extends BaseDialog<AppDialog<VB>> {
    protected VB mBinding;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = reflectViewBinding(inflater, container);
        return mBinding.getRoot();
    }

    @Override
    public void loadData(boolean refresh) {

    }

    private VB reflectViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) throws IllegalStateException {
        try {
            final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            final Class cls = (Class) type.getActualTypeArguments()[0];
            final Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            return (VB) inflate.invoke(null, inflater, container, false);
        } catch (Exception e) {
            LogUtil.log(e);
        }
        throw new IllegalStateException("reflectViewBinding fail");
    }

}
