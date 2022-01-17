package com.colin.library.android.base;

import android.content.Context;

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


    @Nullable
    @Override
    public Context getContext() {
        return this;
    }


}
