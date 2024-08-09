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
@IntDef({Status.Success, Status.Failed})
@Retention(RetentionPolicy.SOURCE)
public @interface Status {
    int Success = 0;
    int Failed = 1;
}