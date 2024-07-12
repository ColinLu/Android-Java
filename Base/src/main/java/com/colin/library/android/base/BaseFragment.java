package com.colin.library.android.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.colin.library.android.Utils;
import com.colin.library.android.base.def.IBase;
import com.colin.library.android.base.def.ILife;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:27
 * <p>
 * 描述： Fragment 基类
 */
public abstract class BaseFragment extends Fragment implements IBase, ILife {
    protected boolean mRefresh = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(savedInstanceState);
        initData(getArguments());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
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
