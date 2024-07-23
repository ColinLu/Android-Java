package com.colin.library.android.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.colin.library.android.base.def.IBase;
import com.colin.library.android.base.def.ILife;
import com.colin.library.android.utils.LogUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:26
 * <p>
 * 描述： Activity 基类
 * 实现刷新机制
 */
public abstract class BaseActivity extends AppCompatActivity implements IBase, ILife {

    protected boolean mRefresh = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.d(this.getClass().getSimpleName(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    public void setContentView(@LayoutRes int layoutRes, @Nullable Bundle savedInstanceState) {
        super.setContentView(layoutRes);
        initView(savedInstanceState);
        initData(getIntent().getExtras());
    }

    public void setContentView(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.setContentView(view);
        initView(savedInstanceState);
        initData(getIntent().getExtras());
    }

    @Override
    protected void onStart() {
        LogUtil.d(this.getClass().getSimpleName(), "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        LogUtil.d(this.getClass().getSimpleName(), "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        LogUtil.d(this.getClass().getSimpleName(), "onResume");
        super.onResume();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
    }

    @Override
    protected void onPause() {
        LogUtil.d(this.getClass().getSimpleName(), "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtil.d(this.getClass().getSimpleName(), "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
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
