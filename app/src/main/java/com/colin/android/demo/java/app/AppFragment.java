package com.colin.android.demo.java.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;

import com.colin.library.android.annotation.NetType;
import com.colin.library.android.base.BaseFragment;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.NetUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * 作者： ColinLu
 * 时间： 2021-12-24 23:31
 * <p>
 * 描述： 构建App基本逻辑
 */
public abstract class AppFragment<VB extends ViewBinding> extends BaseFragment implements ScreenReceiver.OnScreenBroadcastListener, NetBroadReceiver.OnNetListener {
    protected VB mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = reflectViewBinding(inflater, container);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ScreenReceiver.bind(this);
        NetBroadReceiver.bind(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    protected void toNavigate(int action) {
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public void network(@NetType int type) {
        LogUtil.i(getClass().getSimpleName(), NetUtil.getNetType(type));
    }

    @Override
    public void screen(@NonNull String action) {
        LogUtil.i(action);
    }

    private VB reflectViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) throws IllegalStateException {
        try {
            final ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
            assert type != null;
            final Class<VB> cls = (Class<VB>) type.getActualTypeArguments()[0];
            final Method method = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            return (VB) method.invoke(null, inflater, container, false);
        } catch (Exception e) {
            LogUtil.log(e);
        }
        throw new IllegalStateException("reflectViewBinding fail");
    }
}
