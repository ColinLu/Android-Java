package com.colin.android.demo.java.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2020-12-01 12:58
 * <p>
 * 描述： 手机屏幕状态
 */
@IntDef({ScreenStatus.ON, ScreenStatus.OFF, ScreenStatus.PRESENT})
@Retention(RetentionPolicy.SOURCE)
public @interface ScreenStatus {
    int ON = 0;          //手机亮屏
    int OFF = 1;         //手机息屏
    int PRESENT = 2;     //亮屏打开APP
}
