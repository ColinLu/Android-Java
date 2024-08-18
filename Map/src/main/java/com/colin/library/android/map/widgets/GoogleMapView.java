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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-17
 * <p>
 * Des   :自定义Google地图
 */
public class GoogleMapView extends MapView implements LifecycleEventObserver, OnMapReadyCallback {
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_MAP_DATA = "map_data";
    private GoogleMap mMap;
    private LatLng mLatLng;

    public GoogleMapView(Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public GoogleMapView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, Resources.ID_NULL);
    }

    public GoogleMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void init(@NonNull Lifecycle lifecycle, Bundle bundle) {
        lifecycle.addObserver(this);
        onCreate(bundle);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable state = super.onSaveInstanceState();
        if (state instanceof Bundle) return state;
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, state);
        return bundle;
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
            case ON_CREATE:
                this.getMapAsync(this);
                break;
            case ON_START:
                this.onStart();
                break;
            case ON_RESUME:
                this.onResume();
                break;
            case ON_STOP:
                this.onStop();
                break;
            case ON_PAUSE:
                this.onPause();
                break;
            case ON_DESTROY:
                mMap = null;
                mLatLng = null;
                this.onDestroy();
                break;
            default:
                break;
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        updateLocation(mLatLng);
    }

    public void updateLocation(@Nullable LatLng latLng) {
        if (mMap == null || latLng == null) return;
        this.mLatLng = latLng;
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F));
    }
}
