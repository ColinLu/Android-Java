package com.colin.library.android.widgets.edge;

import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2021-03-06 15:55
 * <p>
 * 描述： 注册者
 */
public interface EdgeWatcher {
    void start();

    void offset(@NonNull Edge edge, int offset);

    void finish();
}
