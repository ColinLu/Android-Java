package com.colin.android.demo.java.base;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.colin.android.demo.java.base.def.IActivity;

/**
 * 作者： ColinLu
 * 时间： 2021-12-20 20:14
 * <p>
 * 描述： Activity 基类
 */
public abstract class BaseActivity<Bind extends ViewBinding> extends AppCompatActivity implements IActivity {
    protected Bind mBinding;

    @Override
    public Context getContext() {
        return this;
    }
}
