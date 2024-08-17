package com.colin.library.android.map.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.amap.api.maps.MapView;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-17
 * <p>
 * Des   :自定义高德地图
 */
public class GaodeMapView extends MapView implements LifecycleEventObserver {
    public GaodeMapView(Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public GaodeMapView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, Resources.ID_NULL);
    }

    public GaodeMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void init(@NonNull Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        if (parcelable instanceof Bundle) this.onSaveInstanceState((Bundle) parcelable);
        return parcelable;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (state instanceof Bundle) this.onCreate((Bundle) state);
        else this.onCreate(null);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_RESUME:
                this.onResume();
                break;
            case ON_PAUSE:
                this.onPause();
                break;
            case ON_DESTROY:
                this.onDestroy();
                break;
            default:
                break;
        }
    }


}
