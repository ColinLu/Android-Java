package com.colin.library.android.map.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :地图类型
 */
@IntDef({MapType.GaoDe, MapType.Baidu, MapType.GOOGLE, MapType.TOMTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface MapType {
    int GaoDe = 0;          //高德地图
    int Baidu = 1;          //百度地图
    int GOOGLE = 2;          //Google地图
    int TOMTOM = 3;          //TomTom地图
}