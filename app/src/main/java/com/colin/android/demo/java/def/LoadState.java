package com.colin.android.demo.java.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:51
 * <p>
 * 描述： 加载状态
 */
@IntDef({LoadState.ING, LoadState.MORE, LoadState.SUCCESS, LoadState.ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface LoadState {
    int ING = 0;
    int MORE = 1;
    int SUCCESS = 2;
    int ERROR = 3;
}
