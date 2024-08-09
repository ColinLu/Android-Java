package com.colin.library.android.map.location;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.colin.library.android.map.def.MapType;

import java.lang.ref.WeakReference;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
public class MapLocation implements LifecycleEventObserver {
    private final WeakReference<OnLocationListener> mListenerRef;
    private final ILocationProxy mLocationProxy;


    public MapLocation(@NonNull ActivityResultRegistry registry, @NonNull Lifecycle lifecycle, @NonNull OnLocationListener listener, @MapType int type) {
        lifecycle.addObserver(this);
        this.mListenerRef = new WeakReference<>(listener);
        this.mLocationProxy = LocationFactory.getLocationRepository(type, registry);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_START:
                mLocationProxy.start(false, mListenerRef == null ? null : mListenerRef.get());
                break;
            case ON_RESUME:

                break;
            case ON_PAUSE:
                mLocationProxy.pause();
                break;
            case ON_DESTROY:
                mLocationProxy.destroy();
                break;
        }
    }

}
