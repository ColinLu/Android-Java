package com.colin.library.android.widgets.scroll;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import com.colin.library.android.widgets.annotation.ScrollState;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 21:04
 * <p>
 * 描述： TODO
 */
public interface INestedScroll {

    void injectScrollNotifier(@Nullable OnScrollNotify notify);

    interface OnScrollNotify {
        default void notify(@Px int offset, @Px int range) {
        }

        default void onScrollStateChanged(@NonNull View view, @ScrollState int scrollState) {
        }
    }
}
