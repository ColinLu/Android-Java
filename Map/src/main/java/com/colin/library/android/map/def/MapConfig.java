package com.colin.library.android.map.def;

import android.app.Application;

import androidx.annotation.NonNull;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :地图的配置信息
 */
public class MapConfig {

    @NonNull
    private final Application mApplication;
    @NonNull
    private final String mKey;
    @MapType
    private int mMapType;

    public MapConfig(@NonNull Builder builder) {
        this.mApplication = builder.mApplication;
        this.mKey = builder.mKey;
        this.mMapType = builder.mMapType;
    }


    @NonNull
    public Application getApplication() {
        return mApplication;
    }

    @NonNull
    public String getKey() {
        return mKey;
    }

    @MapType
    public int getMapType() {
        return mMapType;
    }

    public final static class Builder {
        @NonNull
        private final Application mApplication;
        @NonNull
        private final String mKey;
        @MapType
        private int mMapType = MapType.GaoDe;

        public Builder(@NonNull Application application, @NonNull String key) {
            this.mApplication = application;
            this.mKey = key;
        }

        public Builder setMapType(@MapType int mapType) {
            mMapType = mapType;
            return this;
        }

        public MapConfig build() {
            return new MapConfig(this);
        }

    }
}
