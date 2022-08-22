package com.colin.library.android.widgets.edge;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2021-03-06 15:55
 * <p>
 * 描述： 注册者
 */
public interface EdgeWatcher {
    void start();

    void offset(@NonNull View view, int offset);

    void finish();
}
