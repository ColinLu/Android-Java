package com.colin.android.demo.java.utils;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * 作者： ColinLu
 * 时间： 2022-01-19 12:08
 * <p>
 * 描述： TODO
 */
public final class Utils {

    @NonNull
    @MainThread
    public static <T extends ViewModel> T getViewModel(ViewModelStoreOwner owner, @NonNull Class<T> modelClass) {
        return new ViewModelProvider(owner).get(modelClass);
    }
}
