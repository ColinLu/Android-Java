package com.colin.library.android.widgets.web.bridge;

import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2018-11-26 11:21
 * <p>
 * 描述： Android端返回数据 给  js
 */
public interface CallBackFunction {

    void onCallBack(@Nullable String data);

}
