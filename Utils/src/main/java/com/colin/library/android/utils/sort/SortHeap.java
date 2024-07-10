package com.colin.library.android.utils.sort;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.ArrayUtil;


///////////////////////////////////////////////////////////////////////////
// 堆排序
// 基本思想：是一种树形选择排序，是对直接选择排序的有效改进。
//         具有n个元素的序列（h1,h2,...,hn),当且仅当满足（hi>=h2i,hi>=2i+1）
//         或 hi<=h2i,hi<=2i+1）(i=1,2,...,n/2)时称之为堆。
///////////////////////////////////////////////////////////////////////////
public final class SortHeap {
    private SortHeap() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        int arrayLength = arrays.length;
        // 循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            createMaxHeap(arrays, arrayLength - 1 - i);// 建堆
            ArrayUtil.swap(arrays, 0, arrayLength - 1 - i);// 交换堆顶和最后一个元素
        }
        return arrays;
    }

    @NonNull
    private static Integer[] createMaxHeap(@NonNull final Integer[] arrays, final int lastIndex) {
        // 从lastIndex 处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            int k = i;// k 保存正在判断的节点
            while (k * 2 + 1 <= lastIndex) {// 如果当前k节点的子节点存在
                int biggerIndex = 2 * k + 1;// k 节点的左子节点的索引
                // 如果biggerIndex 小于lastIndex，即biggerIndex+1 代表的k 节点的右子节点存在
                // 并且右子节点的值较大，biggerIndex 总是记录较大子节点的索引
                if (biggerIndex < lastIndex && arrays[biggerIndex] < arrays[biggerIndex + 1])
                    biggerIndex++;
                // 如果k节点的值小于其较大的子节点的值
                if (arrays[k] < arrays[biggerIndex]) {
                    ArrayUtil.swap(arrays, k, biggerIndex);// 交换他们
                    k = biggerIndex; // 将biggerIndex 赋予k，开始while 循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                } else {
                    break;
                }
            }
        }
        return arrays;
    }


    @Nullable
    public static Long[] sort(@Nullable final Long... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        int arrayLength = arrays.length;
        // 循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            createMaxHeap(arrays, arrayLength - 1 - i);// 建堆
            ArrayUtil.swap(arrays, 0, arrayLength - 1 - i);// 交换堆顶和最后一个元素
        }
        return arrays;
    }

    @NonNull
    private static Long[] createMaxHeap(@NonNull final Long[] arrays, final int lastIndex) {
        // 从lastIndex 处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            int k = i;// k 保存正在判断的节点
            while (k * 2 + 1 <= lastIndex) {// 如果当前k节点的子节点存在
                int biggerIndex = 2 * k + 1;// k 节点的左子节点的索引
                // 如果biggerIndex 小于lastIndex，即biggerIndex+1 代表的k 节点的右子节点存在
                // 并且右子节点的值较大，biggerIndex 总是记录较大子节点的索引
                if (biggerIndex < lastIndex && arrays[biggerIndex] < arrays[biggerIndex + 1])
                    biggerIndex++;
                // 如果k节点的值小于其较大的子节点的值
                if (arrays[k] < arrays[biggerIndex]) {
                    ArrayUtil.swap(arrays, k, biggerIndex);// 交换他们
                    k = biggerIndex; // 将biggerIndex 赋予k，开始while 循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                } else {
                    break;
                }
            }
        }
        return arrays;
    }

    @Nullable
    public static Float[] sort(@Nullable final Float... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        int arrayLength = arrays.length;
        // 循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            createMaxHeap(arrays, arrayLength - 1 - i);// 建堆
            ArrayUtil.swap(arrays, 0, arrayLength - 1 - i);// 交换堆顶和最后一个元素
        }
        return arrays;
    }

    @NonNull
    private static Float[] createMaxHeap(@NonNull final Float[] arrays, final int lastIndex) {
        // 从lastIndex 处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            int k = i;// k 保存正在判断的节点
            while (k * 2 + 1 <= lastIndex) {// 如果当前k节点的子节点存在
                int biggerIndex = 2 * k + 1;// k 节点的左子节点的索引
                // 如果biggerIndex 小于lastIndex，即biggerIndex+1 代表的k 节点的右子节点存在
                // 并且右子节点的值较大，biggerIndex 总是记录较大子节点的索引
                if (biggerIndex < lastIndex && arrays[biggerIndex] < arrays[biggerIndex + 1])
                    biggerIndex++;
                // 如果k节点的值小于其较大的子节点的值
                if (arrays[k] < arrays[biggerIndex]) {
                    ArrayUtil.swap(arrays, k, biggerIndex);// 交换他们
                    k = biggerIndex; // 将biggerIndex 赋予k，开始while 循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                } else {
                    break;
                }
            }
        }
        return arrays;
    }

    @Nullable
    public static Double[] sort(@Nullable final Double... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        int arrayLength = arrays.length;
        // 循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            createMaxHeap(arrays, arrayLength - 1 - i);// 建堆
            ArrayUtil.swap(arrays, 0, arrayLength - 1 - i);// 交换堆顶和最后一个元素
        }
        return arrays;
    }

    @NonNull
    private static Double[] createMaxHeap(@NonNull final Double[] arrays, final int lastIndex) {
        // 从lastIndex 处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            int k = i;// k 保存正在判断的节点
            while (k * 2 + 1 <= lastIndex) {// 如果当前k节点的子节点存在
                int biggerIndex = 2 * k + 1;// k 节点的左子节点的索引
                // 如果biggerIndex 小于lastIndex，即biggerIndex+1 代表的k 节点的右子节点存在
                // 并且右子节点的值较大，biggerIndex 总是记录较大子节点的索引
                if (biggerIndex < lastIndex && arrays[biggerIndex] < arrays[biggerIndex + 1])
                    biggerIndex++;
                // 如果k节点的值小于其较大的子节点的值
                if (arrays[k] < arrays[biggerIndex]) {
                    ArrayUtil.swap(arrays, k, biggerIndex);// 交换他们
                    k = biggerIndex; // 将biggerIndex 赋予k，开始while 循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                } else {
                    break;
                }
            }
        }
        return arrays;
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        int arrayLength = arrays.length;
        // 循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            createMaxHeap(arrays, arrayLength - 1 - i);// 建堆
            ArrayUtil.swap(arrays, 0, arrayLength - 1 - i);// 交换堆顶和最后一个元素
        }
        return arrays;
    }

    @NonNull
    private static Byte[] createMaxHeap(@NonNull final Byte[] arrays, final int lastIndex) {
        // 从lastIndex 处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            int k = i;// k 保存正在判断的节点
            while (k * 2 + 1 <= lastIndex) {// 如果当前k节点的子节点存在
                int biggerIndex = 2 * k + 1;// k 节点的左子节点的索引
                // 如果biggerIndex 小于lastIndex，即biggerIndex+1 代表的k 节点的右子节点存在
                // 并且右子节点的值较大，biggerIndex 总是记录较大子节点的索引
                if (biggerIndex < lastIndex && arrays[biggerIndex] < arrays[biggerIndex + 1])
                    biggerIndex++;
                // 如果k节点的值小于其较大的子节点的值
                if (arrays[k] < arrays[biggerIndex]) {
                    ArrayUtil.swap(arrays, k, biggerIndex);// 交换他们
                    k = biggerIndex; // 将biggerIndex 赋予k，开始while 循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                } else {
                    break;
                }
            }
        }
        return arrays;
    }

    @Nullable
    public static Short[] sort(@Nullable final Short... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        int arrayLength = arrays.length;
        // 循环建堆
        for (int i = 0; i < arrayLength - 1; i++) {
            createMaxHeap(arrays, arrayLength - 1 - i);// 建堆
            ArrayUtil.swap(arrays, 0, arrayLength - 1 - i);// 交换堆顶和最后一个元素
        }
        return arrays;
    }

    @NonNull
    private static Short[] createMaxHeap(@NonNull final Short[] arrays, final int lastIndex) {
        // 从lastIndex 处节点（最后一个节点）的父节点开始
        for (int i = (lastIndex - 1) / 2; i >= 0; i--) {
            int k = i;// k 保存正在判断的节点
            while (k * 2 + 1 <= lastIndex) {// 如果当前k节点的子节点存在
                int biggerIndex = 2 * k + 1;// k 节点的左子节点的索引
                // 如果biggerIndex 小于lastIndex，即biggerIndex+1 代表的k 节点的右子节点存在
                // 并且右子节点的值较大，biggerIndex 总是记录较大子节点的索引
                if (biggerIndex < lastIndex && arrays[biggerIndex] < arrays[biggerIndex + 1])
                    biggerIndex++;
                // 如果k节点的值小于其较大的子节点的值
                if (arrays[k] < arrays[biggerIndex]) {
                    ArrayUtil.swap(arrays, k, biggerIndex);// 交换他们
                    k = biggerIndex; // 将biggerIndex 赋予k，开始while 循环的下一次循环，重新保证k节点的值大于其左右子节点的值
                } else {
                    break;
                }
            }
        }
        return arrays;
    }
}
