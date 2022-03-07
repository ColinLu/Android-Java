package com.colin.library.android.base.def;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

/**
 * 作者： ColinLu
 * 时间： 2022-03-06 21:11
 * <p>
 * 描述： 生命周期
 */
public interface ILife extends LifecycleObserver {
    @Nullable
    Context getContext();

    @NonNull
    Lifecycle getLifecycle();
}
