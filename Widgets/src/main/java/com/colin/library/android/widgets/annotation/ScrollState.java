package com.colin.library.android.widgets.annotation;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2021-03-05 22:53
 * <p>
 * 描述： 滚动状态
 */
@RestrictTo(LIBRARY_GROUP_PREFIX)
@Retention(RetentionPolicy.SOURCE)
@IntDef({ScrollState.SCROLL_STATE_IDLE, ScrollState.SCROLL_STATE_DRAGGING, ScrollState.SCROLL_STATE_SETTLING})
public @interface ScrollState {
    int SCROLL_STATE_IDLE = RecyclerView.SCROLL_STATE_IDLE;
    int SCROLL_STATE_DRAGGING = RecyclerView.SCROLL_STATE_DRAGGING;
    int SCROLL_STATE_SETTLING = RecyclerView.SCROLL_STATE_SETTLING;

}