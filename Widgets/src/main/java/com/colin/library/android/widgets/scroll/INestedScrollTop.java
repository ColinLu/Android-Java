package com.colin.library.android.widgets.scroll;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 21:07
 * <p>
 * 描述： 向上滚动
 */
public interface INestedScrollTop extends INestedScroll {
    /**
     * consume scroll
     *
     * @param dyUnconsumed the delta value to consume
     * @return the remain unconsumed value
     */
    int consumeScroll(int dyUnconsumed);

    int getCurrentScroll();

    int getScrollOffsetRange();
}
