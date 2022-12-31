package com.colin.library.android.widgets.scroll;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 21:07
 * <p>
 * 描述： TODO
 */
public interface INestedScrollBottom extends INestedScroll {
    int HEIGHT_IS_ENOUGH_TO_SCROLL = -1;

    /**
     * consume scroll
     *
     * @param dyUnconsumed the delta value to consume
     */
    void consumeScroll(int dyUnconsumed);

    void smoothScrollYBy(int dy, int duration);

    void stopScroll();

    /**
     * sometimes the content of BottomView is not enough to scroll,
     * so BottomView should tell the this info to  {@link NestedScrollCoordinatorLayout}
     *
     * @return {@link #HEIGHT_IS_ENOUGH_TO_SCROLL} if can scroll, or content height.
     */
    int getContentHeight();

    int getCurrentScroll();

    int getScrollOffsetRange();
}
