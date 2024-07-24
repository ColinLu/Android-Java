package com.colin.android.demo.java.utils;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.ArrayRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.utils.ResourceUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-19 12:08
 * <p>
 * 描述： TODO
 */
public final class DemoUtils {

    @NonNull
    @MainThread
    public static <T extends ViewModel> T getViewModel(ViewModelStoreOwner owner, @NonNull Class<T> modelClass) {
        return new ViewModelProvider(owner).get(modelClass);
    }

    public static void toNavigate(Fragment fragment, int action) {
        toNavigate(fragment, action, null);
    }

    public static void toNavigate(Fragment fragment, int action, @Nullable Bundle bundle) {
        NavHostFragment.findNavController(fragment).navigate(action, bundle);
    }


    @NonNull
    public static List<String> getStringList(@ArrayRes int arrayRes) {
        if (arrayRes == Resources.ID_NULL) return Collections.emptyList();
        final String[] array = ResourceUtil.getResources().getStringArray(arrayRes);
        return Arrays.asList(array);
    }

    public static void initRecyclerView(@NonNull RecyclerView view, @NonNull RecyclerView.Adapter<?> adapter) {
        view.setLayoutManager(new LinearLayoutManager(view.getContext()));
        view.setHasFixedSize(true);
        view.setAdapter(adapter);
        view.setItemAnimator(new DefaultItemAnimator());
    }
}
