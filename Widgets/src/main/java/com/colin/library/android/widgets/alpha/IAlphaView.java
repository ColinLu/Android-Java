package com.colin.library.android.widgets.alpha;

/**
 * 作者： ColinLu
 * 时间： 2022-05-02 21:31
 * <p>
 * 描述： View支持透明度
 */
public interface IAlphaView {
    /**
     * 设置是否要在 press 时改变透明度
     *
     * @param press 是否要在 press 时改变透明度
     */
    void changedWhenPress(boolean press);

    /**
     * 设置是否要在 disabled 时改变透明度
     *
     * @param disable 是否要在 disabled 时改变透明度
     */
    void changedWhenDisable(boolean disable);

}
