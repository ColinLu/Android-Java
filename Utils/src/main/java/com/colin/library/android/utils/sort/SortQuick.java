package com.colin.library.android.utils.sort;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

///////////////////////////////////////////////////////////////////////////
// 快速排序（快速排序使用分治法 Divide and conquer）
// 基本思想：把一个序列（list）分为两个子序列（sub-lists）。
// 步骤为: 1. 从数列中挑出一个元素，称为 "基准"（pivot），
//        2.重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。
//          在这个分割之后，该基准是它的最后位置。这个称为分割（partition）操作。
//        3.递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序。
//
// 递归的最底部情形，是数列的大小是零或一，也就是永远都已经被排序好了。虽然一直递归下去，但是这个算法总会结束，
// 因为在每次的迭代（iteration）中，它至少会把一个元素摆到它最后的位置去。
///////////////////////////////////////////////////////////////////////////
public final class SortQuick {
    private SortQuick() {
        throw new UnsupportedOperationException("don't instantiate");
    }


    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Integer[] sort(@NonNull final Integer[] arrays, final int low, final int high) {
        int i, j;
        int temp;
        if (low < high) {
            i = low;
            j = high;
            temp = arrays[i];
            while (i < j) {
                while (i < j && arrays[j] > temp) j--;
                if (i < j) {
                    arrays[i] = arrays[j];
                    i++;
                }
                while (i < j && arrays[i] < temp) i++;
                if (i < j) {
                    arrays[j] = arrays[i];
                    j--;
                }
            }
            arrays[i] = temp;
            sort(arrays, low, i - 1);
            sort(arrays, i + 1, high);
        }
        return arrays;
    }

    @Nullable
    public static Long[] sort(@Nullable final Long... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Long[] sort(@NonNull final Long[] arrays, final int low, final int high) {
        int i, j;
        long temp;
        if (low < high) {
            i = low;
            j = high;
            temp = arrays[i];
            while (i < j) {
                while (i < j && arrays[j] > temp) j--;
                if (i < j) {
                    arrays[i] = arrays[j];
                    i++;
                }
                while (i < j && arrays[i] < temp) i++;
                if (i < j) {
                    arrays[j] = arrays[i];
                    j--;
                }
            }
            arrays[i] = temp;
            sort(arrays, low, i - 1);
            sort(arrays, i + 1, high);
        }
        return arrays;
    }

    @Nullable
    public static Float[] sort(@Nullable final Float... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Float[] sort(@NonNull final Float[] arrays, final int low, final int high) {
        int i, j;
        float temp;
        if (low < high) {
            i = low;
            j = high;
            temp = arrays[i];
            while (i < j) {
                while (i < j && arrays[j] > temp) j--;
                if (i < j) {
                    arrays[i] = arrays[j];
                    i++;
                }
                while (i < j && arrays[i] < temp) i++;
                if (i < j) {
                    arrays[j] = arrays[i];
                    j--;
                }
            }
            arrays[i] = temp;
            sort(arrays, low, i - 1);
            sort(arrays, i + 1, high);
        }
        return arrays;
    }

    @Nullable
    public static Double[] sort(@Nullable final Double... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Double[] sort(@NonNull final Double[] arrays, final int low, final int high) {
        int i, j;
        double temp;
        if (low < high) {
            i = low;
            j = high;
            temp = arrays[i];
            while (i < j) {
                while (i < j && arrays[j] > temp) j--;
                if (i < j) {
                    arrays[i] = arrays[j];
                    i++;
                }
                while (i < j && arrays[i] < temp) i++;
                if (i < j) {
                    arrays[j] = arrays[i];
                    j--;
                }
            }
            arrays[i] = temp;
            sort(arrays, low, i - 1);
            sort(arrays, i + 1, high);
        }
        return arrays;
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Byte[] sort(@NonNull final Byte[] arrays, final int low, final int high) {
        int i, j;
        byte temp;
        if (low < high) {
            i = low;
            j = high;
            temp = arrays[i];
            while (i < j) {
                while (i < j && arrays[j] > temp) j--;
                if (i < j) {
                    arrays[i] = arrays[j];
                    i++;
                }
                while (i < j && arrays[i] < temp) i++;
                if (i < j) {
                    arrays[j] = arrays[i];
                    j--;
                }
            }
            arrays[i] = temp;
            sort(arrays, low, i - 1);
            sort(arrays, i + 1, high);
        }
        return arrays;
    }

    @Nullable
    public static Short[] sort(@Nullable final Short... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        return sort(arrays, 0, arrays.length - 1);
    }

    @NonNull
    public static Short[] sort(@NonNull final Short[] arrays, final int low, final int high) {
        int i, j;
        short temp;
        if (low < high) {
            i = low;
            j = high;
            temp = arrays[i];
            while (i < j) {
                while (i < j && arrays[j] > temp) j--;
                if (i < j) {
                    arrays[i] = arrays[j];
                    i++;
                }
                while (i < j && arrays[i] < temp) i++;
                if (i < j) {
                    arrays[j] = arrays[i];
                    j--;
                }
            }
            arrays[i] = temp;
            sort(arrays, low, i - 1);
            sort(arrays, i + 1, high);
        }
        return arrays;
    }

}
