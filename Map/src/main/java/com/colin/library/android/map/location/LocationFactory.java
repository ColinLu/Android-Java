package com.colin.library.android.map.location;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;

import com.colin.library.android.map.def.MapType;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
public class LocationFactory {

    public static ILocationProxy getLocationRepository(@MapType int type, @NonNull ActivityResultRegistry registry) {
        if (type == MapType.GaoDe) return new GaoDeLocationRepository(registry);
        if (type == MapType.Baidu) return new BaiduLocationRepository(registry);
        throw new RuntimeException("not support:" + type);
    }
}
