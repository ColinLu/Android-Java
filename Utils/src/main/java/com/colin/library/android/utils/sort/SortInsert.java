package com.colin.library.android.utils.sort;


import androidx.annotation.Nullable;

import com.colin.library.android.utils.ArrayUtil;


///////////////////////////////////////////////////////////////////////////
//  插入排序 长度必须大于1
//  将一个记录插入到已排好序的有序表（有可能是空表）中,从而得到一个新的记录数增1的有序表。
//  性能：比较次数O(n^2),n^2/4;复制次数O(n),n^2/4
//  比较次数是前两者的一般，而复制所需的CPU时间较交换少，所以性能上比冒泡排序提高一倍多，而比选择排序也要快。
///////////////////////////////////////////////////////////////////////////
public final class SortInsert {
    private SortInsert() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 1; i < arrays.length; i++) {
            for (int j = i; (j > 0) && (arrays[j] < arrays[j - 1]); j--) {
                ArrayUtil.swap(arrays, j, j - 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Long[] sort(@Nullable final Long... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i; (j > 0) && (arrays[j] < arrays[j - 1]); j--) {
                ArrayUtil.swap(arrays, j, j - 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Float[] sort(@Nullable final Float... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i; (j > 0) && (arrays[j] < arrays[j - 1]); j--) {
                ArrayUtil.swap(arrays, j, j - 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Double[] sort(@Nullable final Double... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i; (j > 0) && (arrays[j] < arrays[j - 1]); j--) {
                ArrayUtil.swap(arrays, j, j - 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i; (j > 0) && (arrays[j] < arrays[j - 1]); j--) {
                ArrayUtil.swap(arrays, j, j - 1);
            }
        }
        return arrays;
    }

    @Nullable
    public static Short[] sort(@Nullable final Short... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i; (j > 0) && (arrays[j] < arrays[j - 1]); j--) {
                ArrayUtil.swap(arrays, j, j - 1);
            }
        }
        return arrays;
    }
}
