package com.colin.library.android.utils.sort;


import androidx.annotation.Nullable;

import com.colin.library.android.utils.ArrayUtil;


///////////////////////////////////////////////////////////////////////////
// 直接选择排序法
// 基本思想：每一趟从待排序的数据元素中选出最小（或最大）的一个元素， 顺序放在已排好序的数列的最后，直到全部待排序的数据元素排完。
// 性能：比较次数O(n^2),n^2/2 交换次数O(n),n
// 交换次数比冒泡排序少多了，由于交换所需CPU时间比比较所需的CUP时间多，所以选择排序比冒泡排序快。
// 但是N比较大时，比较所需的CPU时间占主要地位，所以这时的性能和冒泡排序差不太多，但毫无疑问肯定要快些。
///////////////////////////////////////////////////////////////////////////
public final class SortSelect {
    private SortSelect() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i + 1; j < arrays.length; j++) {
                if (asc && arrays[i] > arrays[j]) ArrayUtil.swap(arrays, i, j);
                if (!asc && arrays[i] < arrays[j]) ArrayUtil.swap(arrays, i, j);
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
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i + 1; j < arrays.length; j++) {
                if (asc && arrays[i] > arrays[j]) ArrayUtil.swap(arrays, i, j);
                if (!asc && arrays[i] < arrays[j]) ArrayUtil.swap(arrays, i, j);
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
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i + 1; j < arrays.length; j++) {
                if (asc && arrays[i] > arrays[j]) ArrayUtil.swap(arrays, i, j);
                if (!asc && arrays[i] < arrays[j]) ArrayUtil.swap(arrays, i, j);
            }
        }
        return arrays;
    }

    @Nullable
    public static Double[] sortSelect(@Nullable final Double... arrays) {
        return sort(arrays, true);
    }

    @Nullable
    public static Double[] sort(@Nullable final Double[] arrays, final boolean asc) {
        if (null == arrays || arrays.length < 2) return arrays;
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i + 1; j < arrays.length; j++) {
                if (asc && arrays[i] > arrays[j]) ArrayUtil.swap(arrays, i, j);
                if (!asc && arrays[i] < arrays[j]) ArrayUtil.swap(arrays, i, j);
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
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i + 1; j < arrays.length; j++) {
                if (asc && arrays[i] > arrays[j]) ArrayUtil.swap(arrays, i, j);
                if (!asc && arrays[i] < arrays[j]) ArrayUtil.swap(arrays, i, j);
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
        for (int i = 0; i < arrays.length; i++) {
            for (int j = i + 1; j < arrays.length; j++) {
                if (asc && arrays[i] > arrays[j]) ArrayUtil.swap(arrays, i, j);
                if (!asc && arrays[i] < arrays[j]) ArrayUtil.swap(arrays, i, j);
            }
        }
        return arrays;
    }
}
