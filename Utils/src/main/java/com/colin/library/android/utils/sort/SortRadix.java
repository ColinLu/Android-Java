package com.colin.library.android.utils.sort;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

///////////////////////////////////////////////////////////////////////////
// 基数排序
// 基本思想：将所有待比较数值（正整数）统一为同样的数位长度，数位较短的数前面 补零。然后，从最低位开始，依次进行一次排序。
//         这样从最低位排序一直到最高位排序完成以后,数列就变成一个有序序列。
///////////////////////////////////////////////////////////////////////////
public final class SortRadix {
    private SortRadix() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    @Nullable
    public static Integer[] sort(@Nullable final Integer... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        // 首先确定排序的趟数;
        int max = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[i] > max) max = arrays[i];
        }
        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList<Integer>> queue = new ArrayList<>();
        for (int i = 0; i < 10; i++) queue.add(new ArrayList<>());

        for (int i = 0; i < time; i++) {// 进行time 次分配和收集;
            for (int j = 0; j < arrays.length; j++) {// 分配数组元素;
                // 得到数字的第time+1 位数;
                final int x = arrays[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
                ArrayList<Integer> list = queue.get(x);
                list.add(arrays[j]);
                queue.set(x, list);
            }
            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Integer> queue3 = queue.get(k);
                    arrays[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }

        }
        return arrays;
    }

    @Nullable
    public static Long[] sort(@Nullable final Long... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        // 首先确定排序的趟数;
        long max = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[i] > max) max = arrays[i];
        }
        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList> queue = new ArrayList<>();
        for (int i = 0; i < 10; i++) queue.add(new ArrayList<Long>());

        for (int i = 0; i < time; i++) {// 进行time 次分配和收集;
            for (int j = 0; j < arrays.length; j++) {// 分配数组元素;
                // 得到数字的第time+1 位数;
                final int x = (int) (arrays[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i));
                ArrayList<Long> list = queue.get(x);
                list.add(arrays[j]);
                queue.set(x, list);
            }
            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Long> queue3 = queue.get(k);
                    arrays[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }

        }
        return arrays;
    }

    @Nullable
    public static Float[] sort(@Nullable final Float... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        // 首先确定排序的趟数;
        float max = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[i] > max) max = arrays[i];
        }
        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList> queue = new ArrayList<>();
        for (int i = 0; i < 10; i++) queue.add(new ArrayList<Float>());

        for (int i = 0; i < time; i++) {// 进行time 次分配和收集;
            for (int j = 0; j < arrays.length; j++) {// 分配数组元素;
                // 得到数字的第time+1 位数;
                final int x = (int) (arrays[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i));
                ArrayList<Float> list = queue.get(x);
                list.add(arrays[j]);
                queue.set(x, list);
            }
            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Float> queue3 = queue.get(k);
                    arrays[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }

        }
        return arrays;
    }

    @Nullable
    public static Double[] sort(@Nullable final Double... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        // 首先确定排序的趟数;
        double max = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[i] > max) max = arrays[i];
        }
        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList> queue = new ArrayList<>();
        for (int i = 0; i < 10; i++) queue.add(new ArrayList<Double>());

        for (int i = 0; i < time; i++) {// 进行time 次分配和收集;
            for (int j = 0; j < arrays.length; j++) {// 分配数组元素;
                // 得到数字的第time+1 位数;
                final int x = (int) (arrays[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i));
                ArrayList<Double> list = queue.get(x);
                list.add(arrays[j]);
                queue.set(x, list);
            }
            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Double> queue3 = queue.get(k);
                    arrays[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }

        }
        return arrays;
    }

    @Nullable
    public static Byte[] sort(@Nullable final Byte... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        // 首先确定排序的趟数;
        byte max = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[i] > max) max = arrays[i];
        }
        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList> queue = new ArrayList<>();
        for (int i = 0; i < 10; i++) queue.add(new ArrayList<Byte>());

        for (int i = 0; i < time; i++) {// 进行time 次分配和收集;
            for (int j = 0; j < arrays.length; j++) {// 分配数组元素;
                // 得到数字的第time+1 位数;
                final int x = arrays[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
                ArrayList<Byte> list = queue.get(x);
                list.add(arrays[j]);
                queue.set(x, list);
            }
            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Byte> queue3 = queue.get(k);
                    arrays[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }

        }
        return arrays;
    }

    @Nullable
    public static Short[] sort(@Nullable final Short... arrays) {
        if (null == arrays || arrays.length < 2) return arrays;
        // 首先确定排序的趟数;
        int max = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            if (arrays[i] > max) max = arrays[i];
        }
        int time = 0;
        // 判断位数;
        while (max > 0) {
            max /= 10;
            time++;
        }

        // 建立10个队列;
        List<ArrayList> queue = new ArrayList<>();
        for (int i = 0; i < 10; i++) queue.add(new ArrayList<Short>());

        for (int i = 0; i < time; i++) {            // 进行time 次分配和收集;
            for (int j = 0; j < arrays.length; j++) {// 分配数组元素;
                // 得到数字的第time+1 位数;
                final int x = arrays[j] % (int) Math.pow(10, i + 1) / (int) Math.pow(10, i);
                ArrayList<Short> list = queue.get(x);
                list.add(arrays[j]);
                queue.set(x, list);
            }
            int count = 0;// 元素计数器;
            // 收集队列元素;
            for (int k = 0; k < 10; k++) {
                while (queue.get(k).size() > 0) {
                    ArrayList<Short> queue3 = queue.get(k);
                    arrays[count] = queue3.get(0);
                    queue3.remove(0);
                    count++;
                }
            }

        }
        return arrays;
    }

}
