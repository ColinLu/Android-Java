package com.colin.library.android.map.location;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.colin.library.android.map.def.MapType;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :定位逻辑实现观察者
 */
public class MapLocationObserver implements LifecycleEventObserver {
    private final ILocationProxy mLocationProxy;

    public MapLocationObserver(@NonNull ActivityResultRegistry registry, @NonNull Lifecycle lifecycle, @NonNull OnLocationListener listener, @MapType int type) {
        lifecycle.addObserver(this);
        this.mLocationProxy = LocationFactory.getLocationRepository(type, registry, listener);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_START:
                mLocationProxy.start();
                break;
            case ON_PAUSE:
                mLocationProxy.pause();
                break;
            case ON_DESTROY:
                mLocationProxy.destroy();
                break;
            default:
                break;
        }
    }

}
