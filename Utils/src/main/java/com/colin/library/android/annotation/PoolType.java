package com.colin.library.android.annotation;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-02-14 21:25
 * <p>
 * 描述： 线程池类型
 */
@RestrictTo(LIBRARY_GROUP_PREFIX)
@IntDef({PoolType.CUSTOM, PoolType.SINGLE, PoolType.FIXED, PoolType.CACHED, PoolType.SCHEDULED, PoolType.IO, PoolType.CPU})
@Retention(RetentionPolicy.SOURCE)
public @interface PoolType {
    // 线程池类型  IO流网络请求  CPU复杂运算操作
    int CUSTOM = 0;
    int SINGLE = 1;
    int FIXED = 2;
    int CACHED = 3;
    int SCHEDULED = 4;
    int IO = 5;
    int CPU = 6;
}
