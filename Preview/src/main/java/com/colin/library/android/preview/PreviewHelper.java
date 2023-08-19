package com.colin.library.android.preview;

/**
 * 作者： ColinLu
 * 时间： 2023-07-20 21:07
 * <p>
 * 描述： TODO
 */
public class PreviewHelper {
    private static volatile PreviewHelper sHelper;

    private PreviewHelper() {
    }

    public static PreviewHelper getInstance() {
        if (sHelper == null) {
            synchronized (PreviewHelper.class) {
                if (sHelper == null) sHelper = new PreviewHelper();
            }
        }
        return sHelper;
    }
}
