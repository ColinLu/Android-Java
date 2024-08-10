package com.colin.library.android.map.search;

import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.colin.library.android.map.def.Constants;
import com.colin.library.android.map.def.Status;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
class GaoDeSearchRepository implements ISearchProxy, ActivityResultCallback<Map<String, Boolean>> {
    public static final String TAG = "GaoDeSearchRepository";
    private ActivityResultLauncher<String[]> mLauncher;
    private WeakReference<OnSearchListener> mListenerRef;

    public GaoDeSearchRepository(@NonNull ActivityResultRegistry registry, @NonNull OnSearchListener listener) {
        this.mLauncher = registry.register(TAG, new ActivityResultContracts.RequestMultiplePermissions(), this);
        this.mListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void onActivityResult(Map<String, Boolean> result) {
        AtomicBoolean isGranted = new AtomicBoolean(false);
        result.forEach((permission, granted) -> {
            Log.i(TAG, "permission:" + permission + "\t granted:" + granted);
            if (granted) isGranted.set(true);
            start(isGranted.get());
        });
    }


    @Override
    public void start() {
        mLauncher.launch(Constants.PERMISSIONS_OF_LOCATION);
    }

    private void start(boolean granted) {
        if (!granted) notify(Status.Failed, new ArrayList<Result>(0));
    }


    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    private void notify(@Status int status, List<Result> results) {
        OnSearchListener listener = mListenerRef == null ? null : mListenerRef.get();
        if (listener != null) listener.change(status, results);
    }

}
