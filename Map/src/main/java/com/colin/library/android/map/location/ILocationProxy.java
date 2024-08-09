package com.colin.library.android.map.location;

import androidx.annotation.Nullable;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
interface ILocationProxy {
    void start(boolean granted, @Nullable OnLocationListener listener);

    void pause();

    void destroy();
}
