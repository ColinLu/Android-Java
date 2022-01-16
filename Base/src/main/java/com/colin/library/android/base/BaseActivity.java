package com.colin.library.android.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.colin.library.android.base.def.IActivity;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:26
 * <p>
 * 描述： Activity 基类
 */
public abstract class BaseActivity extends AppCompatActivity implements IActivity {


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initView(null);
        initData(getIntent().getExtras());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initView(null);
        initData(getIntent().getExtras());
    }

    @Nullable
    @Override
    public Context getContext() {
        return this;
    }


}
