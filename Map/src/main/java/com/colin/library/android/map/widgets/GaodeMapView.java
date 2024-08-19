package com.colin.library.android.map.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

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
        getMap().setMapType(AMap.MAP_TYPE_NORMAL);
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

    public void updateLocation(Location location) {
        // 移动地图到定位点
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // 添加定位标记
        getMap().addMarker(new MarkerOptions().position(latLng));
    }


}
