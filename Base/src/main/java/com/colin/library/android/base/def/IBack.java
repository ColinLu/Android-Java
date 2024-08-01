package com.colin.library.android.base.def;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-07-29
 * <p>
 * Des   :是否需要返回
 */
public interface IBack {
    default boolean onBack() {
        return false;
    }
}
