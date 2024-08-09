package com.colin.library.android.map.location;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
class BaiduLocationRepository implements ILocationProxy {
    private WeakReference<ActivityResultRegistry> mRegistryRef;

    public BaiduLocationRepository(@NonNull ActivityResultRegistry registry) {
        mRegistryRef = new WeakReference<>(registry);
    }

    @Override
    public void start(boolean granted, @Nullable OnLocationListener listener) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
