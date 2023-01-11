package com.colin.library.android.widgets.def;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-02-03 20:21
 * <p>
 * 描述： TODO
 */
public abstract class OnAppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    @RestrictTo(LIBRARY_GROUP_PREFIX)
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({State.IDLE, State.COLLAPSED, State.EXPANDED})
    public @interface State {
        int IDLE = 0;
        int COLLAPSED = 1;
        int EXPANDED = 2;
    }

    @State
    private int mCurrentState = State.IDLE;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) onStateChanged(appBarLayout, State.EXPANDED, i);
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) onStateChanged(appBarLayout, State.COLLAPSED, i);
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) onStateChanged(appBarLayout, State.IDLE, i);
            mCurrentState = State.IDLE;
        }
    }


    public abstract void onStateChanged(AppBarLayout appBarLayout, @State int state, int offset);

}
