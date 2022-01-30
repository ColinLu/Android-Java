package com.colin.library.android.base.def;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

/**
 * 作者： ColinLu
 * 时间： 2021-12-20 20:21
 * <p>
 * 描述： 界面实现定义
 */
public interface IView {
    @Nullable
    Context getContext();

    @NonNull
    Lifecycle getLifecycle();

    @LayoutRes
    int layoutRes();

    void initView(@Nullable Bundle bundle);

    void initData(@Nullable Bundle bundle);

    void loadData(boolean refresh);

}
