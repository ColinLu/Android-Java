package com.colin.library.android.media.base;

import android.content.Context;

import androidx.annotation.NonNull;

import com.colin.library.android.media.def.Action;


/**
 * 作者： ColinLu
 * 时间： 2020-05-22 07:18
 * <p>
 * 描述： 多媒体选择基类
 */
public interface IBase<Returner, Result, Cancel> {
    @NonNull
    Returner title(@NonNull String title);

    @NonNull
    Returner result(@NonNull Action<Result> result);

    @NonNull
    Returner cancel(@NonNull Action<Cancel> cancel);

    void start(@NonNull Context context);

}
