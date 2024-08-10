package com.colin.library.android.map.search;

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
 * Des   :TODO
 */
public class MapSearchObserver implements LifecycleEventObserver {
    private final ISearchProxy mSearchProxy;

    public MapSearchObserver(@NonNull ActivityResultRegistry registry, @NonNull Lifecycle lifecycle, @NonNull OnSearchListener listener, @MapType int type) {
        lifecycle.addObserver(this);
        this.mSearchProxy = SearchFactory.getSearchRepository(type, registry, listener);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_START:

                break;
            case ON_PAUSE:
                mSearchProxy.pause();
                break;
            case ON_DESTROY:
                mSearchProxy.destroy();
                break;
            default:
                break;
        }
    }
}
