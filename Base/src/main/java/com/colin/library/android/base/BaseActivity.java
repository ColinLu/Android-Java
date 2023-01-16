package com.colin.library.android.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutRes() != Resources.ID_NULL) setContentView(layoutRes());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initView(savedInstanceState);
        initData(getIntent().getExtras());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
    }

    @Nullable
    @Override
    public Context getContext() {
        return this;
    }

    /*返回界面  onResume 刷新*/
    public void setRefresh(boolean refresh) {
        mRefresh = refresh;
    }
}
