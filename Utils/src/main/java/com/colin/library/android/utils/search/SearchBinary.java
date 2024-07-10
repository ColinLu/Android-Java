package com.colin.library.android.utils.search;


import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;


///////////////////////////////////////////////////////////////////////////
// 二分法查找(查找线性表必须是有序列表)
// 二分查找又称折半查找，它是一种效率较高的查找方法。
// 要求：1.必须采用顺序存储结构，2.必须按关键字大小有序排列。
///////////////////////////////////////////////////////////////////////////
public final class SearchBinary {
    private SearchBinary() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static int search(final int key, @Nullable final int... arrays) {
        return search(arrays, key);
    }

    public static int search(@Nullable final int[] arrays, final int key) {
        if (null == arrays || arrays.length == 0) return Constants.INVALID;
        if (arrays.length == 1) return 0;
        int low = 0, high = arrays.length - 1, mid;
        while (low <= high) {
            mid = (low + high) >>> 1;
            if (key == arrays[mid]) return mid;
            if (key < arrays[mid]) high = mid - 1;
            else low = mid + 1;
        }
        return Constants.INVALID;
    }

    public static long search(final long key, @Nullable final long... arrays) {
        return search(arrays, key);
    }

    public static long search(@Nullable final long[] arrays, final long key) {
        if (null == arrays || arrays.length == 0) return Constants.INVALID;
        if (arrays.length == 1) return 0;
        int low = 0, high = arrays.length - 1, mid;
        while (low <= high) {
            mid = (low + high) >>> 1;
            if (key == arrays[mid]) return mid;
            if (key < arrays[mid]) high = mid - 1;
            else low = mid + 1;
        }
        return Constants.INVALID;
    }

    public static float search(final float key, @Nullable final float... arrays) {
        return search(arrays, key);
    }

    public static float search(@Nullable final float[] arrays, final float key) {
        if (null == arrays || arrays.length == 0) return Constants.INVALID;
        if (arrays.length == 1) return 0;
        int low = 0, high = arrays.length - 1, mid;
        while (low <= high) {
            mid = (low + high) >>> 1;
            if (key == arrays[mid]) return mid;
            if (key < arrays[mid]) high = mid - 1;
            else low = mid + 1;
        }
        return Constants.INVALID;
    }

    public static double search(final double key, @Nullable final double... arrays) {
        return search(arrays, key);
    }

    public static double search(@Nullable final double[] arrays, final double key) {
        if (null == arrays || arrays.length == 0) return Constants.INVALID;
        if (arrays.length == 1) return 0;
        int low = 0, high = arrays.length - 1, mid;
        while (low <= high) {
            mid = (low + high) >>> 1;
            if (key == arrays[mid]) return mid;
            if (key < arrays[mid]) high = mid - 1;
            else low = mid + 1;
        }
        return Constants.INVALID;
    }
}
