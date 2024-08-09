package com.colin.library.android.map.location;

import android.location.Location;

import androidx.annotation.NonNull;

import com.colin.library.android.map.def.Status;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
public interface OnLocationListener {
    void change(@Status int status, @NonNull Location location);
}
