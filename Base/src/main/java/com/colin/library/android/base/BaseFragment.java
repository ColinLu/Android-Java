package com.colin.library.android.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.colin.library.android.Utils;
import com.colin.library.android.base.def.IBase;
import com.colin.library.android.base.def.ILife;
import com.colin.library.android.utils.LogUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:27
 * <p>
 * 描述： Fragment 基类
 */
public abstract class BaseFragment extends Fragment implements IBase, ILife {
    protected boolean mRefresh = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(this.getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(this.getClass().getSimpleName(), "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LogUtil.d(this.getClass().getSimpleName(), "onViewCreated");
        initView(savedInstanceState);
        initData(getArguments());
    }

    @Override
    public void onStart() {
        LogUtil.d(this.getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        LogUtil.d(this.getClass().getSimpleName(), "onResume");
        super.onResume();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
    }

    @Override
    public void onPause() {
        LogUtil.d(this.getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        LogUtil.d(this.getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        LogUtil.d(this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

    @NonNull
    @Override
    public Context getContext() {
        return Utils.notNull(super.getContext(), "Fragment " + this + " not attached to a context.");
    }

    @NonNull
    public <T extends View> T findViewById(@IdRes int id) {
        return Utils.notNull(getView(), "fragment root view is null").findViewById(id);
    }

    /*返回界面  onResume 刷新*/
    public void setRefresh(boolean refresh) {
        mRefresh = refresh;
    }
}
