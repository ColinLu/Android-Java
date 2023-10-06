package com.colin.library.android.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.colin.library.android.base.def.IInitView;
import com.colin.library.android.base.def.ILife;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:26
 * <p>
 * 描述： Activity 基类
 * 实现刷新机制
 */
public abstract class BaseActivity extends AppCompatActivity implements IInitView, ILife {

    protected boolean mRefresh = true;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (layoutRes() != Resources.ID_NULL) setContentView(layoutRes());
        initView(savedInstanceState);
        initData(getIntent().getExtras());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
    }

    @NonNull
    @Override
    public Context getContext() {
        return this;
    }

    /*返回界面  onResume 刷新*/
    public void setRefresh(boolean refresh) {
        mRefresh = refresh;
    }

}
