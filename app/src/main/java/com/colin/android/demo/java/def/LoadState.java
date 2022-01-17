package com.colin.android.demo.java.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:51
 * <p>
 * 描述： TODO
 */
@IntDef({LoadState.ING, LoadState.SUCCESS, LoadState.ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface LoadState {
    int ING = 0;
    int SUCCESS = 1;
    int ERROR = 2;
}
