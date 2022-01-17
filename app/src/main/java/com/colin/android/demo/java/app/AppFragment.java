package com.colin.android.demo.java.app;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;

import com.colin.library.android.base.BaseFragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 作者： ColinLu
 * 时间： 2021-12-24 23:31
 * <p>
 * 描述： TODO
 */
public abstract class AppFragment<Bind extends ViewBinding> extends BaseFragment {
    protected Bind mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        final Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            final Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            mBinding = (Bind) inflate.invoke(null, inflater, container, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(savedInstanceState);
        initData(getArguments());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public int layoutRes() {
        return Resources.ID_NULL;
    }

    protected void toNavigate(int action) {
        NavHostFragment.findNavController(this).navigate(action);
    }

}
