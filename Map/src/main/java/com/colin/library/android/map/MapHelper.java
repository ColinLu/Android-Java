package com.colin.library.android.map;

import android.app.Application;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.amap.api.maps.MapsInitializer;
import com.colin.library.android.map.def.MapConfig;
import com.colin.library.android.map.def.MapType;
import com.colin.library.android.map.location.MapLocationObserver;
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
        init(new MapConfig.Builder(application, key).setMapType(type).build());
    }

    public void init(@NonNull MapConfig config) {
        mMapConfig = config;
        if (config.getMapType() == MapType.GaoDe) {
            MapsInitializer.setApiKey(config.getKey());
            MapsInitializer.updatePrivacyShow(config.getApplication(),true,true);
            MapsInitializer.updatePrivacyAgree(config.getApplication(),true);
        }
    }

    @NonNull
    public MapConfig getMapConfig() {
        if (mMapConfig == null) throw new NullPointerException("MapConfig init first !");
        return mMapConfig;
    }

    /**
     * @param registry  权限处理
     * @param lifecycle 生命周期感知对象
     * @param listener  定位监听器
     * @return 定位数据观察者
     */
    public MapLocationObserver location(@NonNull ActivityResultRegistry registry, @NonNull Lifecycle lifecycle, @NonNull OnLocationListener listener) {
        return new MapLocationObserver(registry, lifecycle, listener, getMapConfig().getMapType());
    }
}
