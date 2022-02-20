package com.colin.library.android.utils.search;


import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;


///////////////////////////////////////////////////////////////////////////
// 顺序查找
// 平均时间复杂度 O（n）
///////////////////////////////////////////////////////////////////////////
public final class SearchOrder {
    private SearchOrder() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*返回数组下标，未查到 -1*/
    public static <ARRAY> int search(@Nullable final ARRAY[] arrays, @Nullable final ARRAY key) {
        if (null == arrays || arrays.length == 0) return Constants.INVALID;
        if (arrays.length == 1) return 0;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i] == key) return i;
        }
        return Constants.INVALID;
    }

    public static <ARRAY> int search(@Nullable final ARRAY key, @Nullable final ARRAY... arrays) {
        if (null == arrays || arrays.length == 0) return Constants.INVALID;
        if (arrays.length == 1) return 0;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i] == key) return i;
        }
        return Constants.INVALID;
    }

}
