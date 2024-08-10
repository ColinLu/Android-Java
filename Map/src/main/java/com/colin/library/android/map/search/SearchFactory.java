package com.colin.library.android.map.search;

import androidx.activity.result.ActivityResultRegistry;
import androidx.annotation.NonNull;

import com.colin.library.android.map.def.MapType;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
class SearchFactory {
    public static ISearchProxy getSearchRepository(@MapType int type, @NonNull ActivityResultRegistry registry, @NonNull OnSearchListener listener) {
        if (type == MapType.GaoDe) return new GaoDeSearchRepository(registry, listener);
//        if (type == MapType.Baidu) return new BaiduLocationRepository(registry,listener);
        throw new RuntimeException("not support:" + type);
    }
}
