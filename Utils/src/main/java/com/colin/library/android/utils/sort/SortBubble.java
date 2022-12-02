package com.colin.library.android.utils.sort;


import androidx.annotation.Nullable;

import com.colin.library.android.utils.ArrayUtil;


///////////////////////////////////////////////////////////////////////////
// 冒泡排序 asc true 升序，false 降序
// 基本思想：相邻两元素进行比较 性能：比较次数O(n^2),n^2/2；交换次数O(n^2),n^2/4
// only for  Integer Long Short Double Float
///////////////////////////////////////////////////////////////////////////
public final class SortBubble {
    private SortBubble() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*升序*/
    public static Double[] sortAsc(@Nullable final Double[] arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (arrays[j] > arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

    /*降序*/
    public static Double[] sortDesc(@Nullable final Double[] arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (arrays[j] < arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (!asc && arrays[j] < arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
                if (asc && arrays[j] > arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Long[] sort(@Nullable final Long... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Long[] sort(@Nullable final Long[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (!asc && arrays[j] < arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
                if (asc && arrays[j] > arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Float[] sort(@Nullable final Float... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Float[] sort(@Nullable final Float[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (!asc && arrays[j] < arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
                if (asc && arrays[j] > arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Double[] sort(@Nullable final Double... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Double[] sort(@Nullable final Double[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (!asc && arrays[j] < arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
                if (asc && arrays[j] > arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (!asc && arrays[j] < arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
                if (asc && arrays[j] > arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Short[] sort(@Nullable final Short... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Short[] sort(@Nullable final Short[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length - 1; i++) {
            for (int j = 0; j < arrays.length - 1 - i; j++) {
                if (!asc && arrays[j] < arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
                if (asc && arrays[j] > arrays[j + 1]) ArrayUtil.swap(arrays, j, j + 1);
            }
        }
        return arrays;
    }

}
