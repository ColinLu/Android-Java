package com.colin.library.android.utils.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-03-07 21:09
 * <p>
 * 描述： 网络类型
 */
@IntDef({NetType.NETWORK_NONE, NetType.NETWORK_MOBILE, NetType.NETWORK_WIFI, NetType.NETWORK_2G,
        NetType.NETWORK_3G, NetType.NETWORK_4G, NetType.NETWORK_5G, NetType.NETWORK_ETHERNET})
@Retention(RetentionPolicy.SOURCE)
public @interface NetType {
    int NETWORK_NONE = -1;          // 没有网络连接
    int NETWORK_MOBILE = 0;         // 手机流量
    int NETWORK_WIFI = 1;           // wifi连接
    int NETWORK_2G = 2;             // 2G
    int NETWORK_3G = 3;             // 3G
    int NETWORK_4G = 4;             // 4G
    int NETWORK_5G = 5;             // 5G
    int NETWORK_ETHERNET = 6;       // 以太网


}
