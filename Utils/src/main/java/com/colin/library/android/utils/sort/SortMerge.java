package com.colin.library.android.utils.sort;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

///////////////////////////////////////////////////////////////////////////
// 归并排序
// 基本思想：将两个（或两个以上）有序表合并成一个新的有序表，即把待排序序列分为若干个子序列，
//         每个子序列是有序的。然后再把有序子序列合并 为整体有序序列。
///////////////////////////////////////////////////////////////////////////
public final class SortMerge {
    private SortMerge() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Integer[] sort(@NonNull final Integer[] arrays, final int left, final int right) {
        if (left >= right) return arrays;
        final int center = (left + right) / 2;      // 找出中间索引
        sort(arrays, left, center);            // 对左边数组进行递归
        sort(arrays, center + 1, right); // 对右边数组进行递归
        sort(arrays, left, center, right);     // 合并
        return arrays;
    }

    @NonNull
    private static Integer[] sort(@NonNull final Integer[] arrays, int left, int center, int right) {
        int[] tempArray = new int[arrays.length];
        int mid = center + 1;
        int third = left;
        int temp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入中间数组
            if (arrays[left] <= arrays[mid]) tempArray[third++] = arrays[left++];
            else tempArray[third++] = arrays[mid++];
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) tempArray[third++] = arrays[mid++];
        while (left <= center) tempArray[third++] = arrays[left++];
        // 将中间数组中的内容复制回原数组
        while (temp <= right) arrays[temp] = tempArray[temp++];
        return arrays;
    }

    @Nullable
    public static Long[] sort(@Nullable final Long... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Long[] sort(@NonNull final Long[] arrays, final int left, final int right) {
        if (left >= right) return arrays;
        final int center = (left + right) / 2;      // 找出中间索引
        sort(arrays, left, center);            // 对左边数组进行递归
        sort(arrays, center + 1, right); // 对右边数组进行递归
        sort(arrays, left, center, right);     // 合并
        return arrays;
    }

    @NonNull
    private static Long[] sort(@NonNull final Long[] arrays, int left, int center, int right) {
        long[] tempArray = new long[arrays.length];
        int mid = center + 1;
        int third = left;
        int temp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入中间数组
            if (arrays[left] <= arrays[mid]) tempArray[third++] = arrays[left++];
            else tempArray[third++] = arrays[mid++];
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) tempArray[third++] = arrays[mid++];
        while (left <= center) tempArray[third++] = arrays[left++];
        // 将中间数组中的内容复制回原数组
        while (temp <= right) arrays[temp] = tempArray[temp++];
        return arrays;
    }

    @Nullable
    public static Float[] sort(@Nullable final Float... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Float[] sort(@NonNull final Float[] arrays, final int left, final int right) {
        if (left >= right) return arrays;
        final int center = (left + right) / 2;      // 找出中间索引
        sort(arrays, left, center);            // 对左边数组进行递归
        sort(arrays, center + 1, right); // 对右边数组进行递归
        sort(arrays, left, center, right);     // 合并
        return arrays;
    }

    @NonNull
    private static Float[] sort(@NonNull final Float[] arrays, int left, int center, int right) {
        float[] tempArray = new float[arrays.length];
        int mid = center + 1;
        int third = left;
        int temp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入中间数组
            if (arrays[left] <= arrays[mid]) tempArray[third++] = arrays[left++];
            else tempArray[third++] = arrays[mid++];
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) tempArray[third++] = arrays[mid++];
        while (left <= center) tempArray[third++] = arrays[left++];
        // 将中间数组中的内容复制回原数组
        while (temp <= right) arrays[temp] = tempArray[temp++];
        return arrays;
    }

    @Nullable
    public static Double[] sort(@Nullable final Double... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Double[] sort(@NonNull final Double[] arrays, final int left, final int right) {
        if (left >= right) return arrays;
        final int center = (left + right) / 2;      // 找出中间索引
        sort(arrays, left, center);            // 对左边数组进行递归
        sort(arrays, center + 1, right); // 对右边数组进行递归
        sort(arrays, left, center, right);     // 合并
        return arrays;
    }

    @NonNull
    private static Double[] sort(@NonNull final Double[] arrays, int left, int center, int right) {
        double[] tempArray = new double[arrays.length];
        int mid = center + 1;
        int third = left;
        int temp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入中间数组
            if (arrays[left] <= arrays[mid]) tempArray[third++] = arrays[left++];
            else tempArray[third++] = arrays[mid++];
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) tempArray[third++] = arrays[mid++];
        while (left <= center) tempArray[third++] = arrays[left++];
        // 将中间数组中的内容复制回原数组
        while (temp <= right) arrays[temp] = tempArray[temp++];
        return arrays;
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Byte[] sort(@NonNull final Byte[] arrays, final int left, final int right) {
        if (left >= right) return arrays;
        final int center = (left + right) / 2;      // 找出中间索引
        sort(arrays, left, center);            // 对左边数组进行递归
        sort(arrays, center + 1, right); // 对右边数组进行递归
        sort(arrays, left, center, right);     // 合并
        return arrays;
    }

    @NonNull
    private static Byte[] sort(@NonNull final Byte[] arrays, int left, int center, int right) {
        byte[] tempArray = new byte[arrays.length];
        int mid = center + 1;
        int third = left;
        int temp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入中间数组
            if (arrays[left] <= arrays[mid]) tempArray[third++] = arrays[left++];
            else tempArray[third++] = arrays[mid++];
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) tempArray[third++] = arrays[mid++];
        while (left <= center) tempArray[third++] = arrays[left++];
        // 将中间数组中的内容复制回原数组
        while (temp <= right) arrays[temp] = tempArray[temp++];
        return arrays;
    }

    @Nullable
    public static Short[] sort(@Nullable final Short... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Short[] sort(@NonNull final Short[] arrays, final int left, final int right) {
        if (left >= right) return arrays;
        final int center = (left + right) / 2;      // 找出中间索引
        sort(arrays, left, center);            // 对左边数组进行递归
        sort(arrays, center + 1, right); // 对右边数组进行递归
        sort(arrays, left, center, right);     // 合并
        return arrays;
    }

    @NonNull
    private static Short[] sort(@NonNull final Short[] arrays, int left, int center, int right) {
        short[] tempArray = new short[arrays.length];
        int mid = center + 1;
        int third = left;
        int temp = left;
        while (left <= center && mid <= right) {
            // 从两个数组中取出最小的放入中间数组
            if (arrays[left] <= arrays[mid]) tempArray[third++] = arrays[left++];
            else tempArray[third++] = arrays[mid++];
        }
        // 剩余部分依次放入中间数组
        while (mid <= right) tempArray[third++] = arrays[mid++];
        while (left <= center) tempArray[third++] = arrays[left++];
        // 将中间数组中的内容复制回原数组
        while (temp <= right) arrays[temp] = tempArray[temp++];
        return arrays;
    }

}
