package com.colin.library.android.utils.sort;


import androidx.annotation.Nullable;

///////////////////////////////////////////////////////////////////////////
// 希尔排序（最小增量排序）
// 基本思想：算法先将要排序的一组数按某个增量 d（n/2,n为要排序数的个数）分成若 干组，每组中记录的下标相差d
// 对每组中全部元素进行直接插入排序，然后再用一个较小 的增量（d/2）对它进行分组，在每组中再进行直接插入排序。
// 当增量减到 1 时，进行直接插入排序后，排序完成。
///////////////////////////////////////////////////////////////////////////
public final class SortShell {
    private SortShell() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        int temp;
        double length = arrays.length;
        while (true) {
            length = Math.ceil(length / 2);
            final int d = (int) length;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < arrays.length; i += d) {
                    int j = i - d;
                    temp = arrays[i];
                    for (; j >= 0 && temp < arrays[j]; j -= d) {
                        arrays[j + d] = arrays[j];
                    }
                    arrays[j + d] = temp;
                }
            }
            if (d == 1) break;
        }
        return arrays;
    }

    @Nullable
    public static Long[] sort(@Nullable final Long... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        long temp;
        double length = arrays.length;
        while (true) {
            length = Math.ceil(length / 2);
            final int d = (int) length;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < arrays.length; i += d) {
                    int j = i - d;
                    temp = arrays[i];
                    for (; j >= 0 && temp < arrays[j]; j -= d) {
                        arrays[j + d] = arrays[j];
                    }
                    arrays[j + d] = temp;
                }
            }
            if (d == 1) break;
        }
        return arrays;
    }

    @Nullable
    public static Float[] sort(@Nullable final Float... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        float temp;
        double length = arrays.length;
        while (true) {
            length = Math.ceil(length / 2);
            final int d = (int) length;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < arrays.length; i += d) {
                    int j = i - d;
                    temp = arrays[i];
                    for (; j >= 0 && temp < arrays[j]; j -= d) {
                        arrays[j + d] = arrays[j];
                    }
                    arrays[j + d] = temp;
                }
            }
            if (d == 1) break;
        }
        return arrays;
    }

    @Nullable
    public static Double[] sort(@Nullable final Double... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        double temp;
        double length = arrays.length;
        while (true) {
            length = Math.ceil(length / 2);
            final int d = (int) length;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < arrays.length; i += d) {
                    int j = i - d;
                    temp = arrays[i];
                    for (; j >= 0 && temp < arrays[j]; j -= d) {
                        arrays[j + d] = arrays[j];
                    }
                    arrays[j + d] = temp;
                }
            }
            if (d == 1) break;
        }
        return arrays;
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        byte temp;
        double length = arrays.length;
        while (true) {
            length = Math.ceil(length / 2);
            final int d = (int) length;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < arrays.length; i += d) {
                    int j = i - d;
                    temp = arrays[i];
                    for (; j >= 0 && temp < arrays[j]; j -= d) {
                        arrays[j + d] = arrays[j];
                    }
                    arrays[j + d] = temp;
                }
            }
            if (d == 1) break;
        }
        return arrays;
    }

    @Nullable
    public static Short[] sort(@Nullable final Short... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        short temp;
        double length = arrays.length;
        while (true) {
            length = Math.ceil(length / 2);
            final int d = (int) length;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < arrays.length; i += d) {
                    int j = i - d;
                    temp = arrays[i];
                    for (; j >= 0 && temp < arrays[j]; j -= d) {
                        arrays[j + d] = arrays[j];
                    }
                    arrays[j + d] = temp;
                }
            }
            if (d == 1) break;
        }
        return arrays;
    }

}
