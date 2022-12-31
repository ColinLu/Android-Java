package com.colin.library.android.widgets.scroll;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 21:04
 * <p>
 * 描述： TODO
 */
public interface INestedScroll {
    int SCROLL_STATE_IDLE = RecyclerView.SCROLL_STATE_IDLE;
    int SCROLL_STATE_DRAGGING = RecyclerView.SCROLL_STATE_DRAGGING;
    int SCROLL_STATE_SETTLING = RecyclerView.SCROLL_STATE_SETTLING;

    void saveScrollInfo(@NonNull Bundle bundle);

    void restoreScrollInfo(@NonNull Bundle bundle);

    void injectScrollNotifier(@Nullable OnScrollNotify notify);

    public interface OnScrollNotify {
        void notify(int innerOffset, int innerRange);

        void onScrollStateChange(@NonNull View view, int scrollState);
    }
}
