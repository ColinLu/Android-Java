package com.colin.library.android.map.location;

import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.colin.library.android.map.def.Constants;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
class BaiduLocationRepository implements ILocationProxy {
    public static final String TAG = "BaiduLocationRepository";
    private WeakReference<OnLocationListener> mListenerRef;

    @NonNull
    private final ActivityResultLauncher<String[]> mLauncher;
    private final ActivityResultContracts.RequestMultiplePermissions mPermissions = new ActivityResultContracts.RequestMultiplePermissions();
    private final ActivityResultCallback<Map<String, Boolean>> mCallback = result -> {
        AtomicBoolean isGranted = new AtomicBoolean(false);
        result.forEach((permission, granted) -> {
            Log.e(TAG, "permission:" + permission + "\t granted:" + granted);
            if (granted) isGranted.set(true);
            start(isGranted.get());
        });
    };

    public BaiduLocationRepository(@NonNull ActivityResultRegistry registry, @NonNull OnLocationListener listener) {
        this.mLauncher = registry.register(TAG, mPermissions, mCallback);
        this.mListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void start() {
        mLauncher.launch(Constants.PERMISSIONS_OF_LOCATION);
    }

    public void start(boolean granted) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
