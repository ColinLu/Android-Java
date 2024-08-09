package com.colin.library.android.map;

import android.app.Application;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.colin.library.android.map.def.MapConfig;
import com.colin.library.android.map.def.MapType;
import com.colin.library.android.map.location.MapLocation;
import com.colin.library.android.map.location.OnLocationListener;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :地图工具类
 */
public final class MapHelper {
    private static volatile MapHelper sHelper;
    private MapConfig mMapConfig;

    private MapHelper() {
    }

    public static MapHelper getInstance() {
        if (sHelper == null) {
            synchronized (MapHelper.class) {
                if (sHelper == null) sHelper = new MapHelper();
            }
        }
        return sHelper;
    }


    public void init(@NonNull Application application, @MapType int type, @NonNull String key) {
        mMapConfig = new MapConfig.Builder(application, key).setMapType(type).build();
    }

    public void init(@NonNull MapConfig config) {
        mMapConfig = config;
    }

    @NonNull
    public MapConfig getMapConfig() {
        if (mMapConfig == null) throw new NullPointerException("MapConfig init first !");
        return mMapConfig;
    }

    public MapLocation bindLocation(@NonNull ActivityResultRegistry registry, Lifecycle lifecycle, @NonNull OnLocationListener listener) {
        return new MapLocation(registry, lifecycle, listener, getMapConfig().getMapType());
    }
}
