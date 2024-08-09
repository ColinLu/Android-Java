package com.colin.library.android.map.location;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :定位
 */
class LocationManager {
    private static volatile LocationManager sManager;


    private LocationManager() {
    }

    public static LocationManager getInstance() {
        if (sManager == null) {
            synchronized (LocationManager.class) {
                if (sManager == null) sManager = new LocationManager();
            }
        }
        return sManager;
    }
}
