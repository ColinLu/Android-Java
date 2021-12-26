package com.colin.android.demo.java.utils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 作者： ColinLu
 * 时间： 2019-10-07 17:38
 * <p>
 * 描述： Array Util
 */
public final class ArrayUtil {
    private ArrayUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }


    /**
     * 判断数组是否为空
     *
     * @param arrays
     * @return
     */
    public static <ARRAY> boolean isEmpty(@Nullable final ARRAY... arrays) {
        return null == arrays || arrays.length == 0;
    }

    /**
     * 数组交换值
     *
     * @param arrays 需要进行交换操作的数组
     * @param x      数组中的位置1
     * @param y      数组中的位置2
     * @return 交换后的数组
     */
    @Nullable
    public static <ARRAY> ARRAY[] swap(@Nullable final ARRAY[] arrays, final int x, final int y) {
        if (isEmpty(arrays) || x < 0 || y < 0 || x == y) return arrays;
        //数组下标越界
        if (arrays.length <= x || arrays.length <= y) return arrays;
        final ARRAY temp = arrays[x];
        arrays[x] = arrays[y];
        arrays[y] = temp;
        return arrays;
    }

    /**
     * 获取数组对应索引数据
     *
     * @param arrays   数组
     * @param position 索引
     * @return 数组指定索引的值
     */
    public static <ARRAY> ARRAY get(final ARRAY[] arrays, final int position) {
        if (arrays != null && arrays.length > position) return arrays[position];
        return null;
    }

    public static <ARRAY> ARRAY get(final int position, final ARRAY... arrays) {
        if (arrays != null && arrays.length > position) return arrays[position];
        return null;
    }

    /**
     * 数组反转
     *
     * @param arrays
     * @return
     */
    @Nullable
    public static <ARRAY> ARRAY[] reverse(@Nullable final ARRAY... arrays) {
        if (isEmpty(arrays)) return arrays;
        final int length = arrays.length;
        ARRAY temp;
        for (int i = 0; i < length >> 1; i++) {
            temp = arrays[i];
            arrays[i] = arrays[length - 1 - i];
            arrays[length - 1 - i] = temp;
        }
        return arrays;
    }

    /**
     * 在当前位置插入一个元素,数组中原有元素向后移动; 如果插入位置超出原数组，则抛IllegalArgumentException异常
     *
     * @param arrays
     * @param index
     * @param value
     * @return
     */
    public static Object[] insert(Object[] arrays, int index, Object value) {
        if (isEmpty(arrays)) throw new IllegalArgumentException();
        if (index - 1 > arrays.length || index <= 0) throw new IllegalArgumentException();

        Object[] dest = new Object[arrays.length + 1];
        System.arraycopy(arrays, 0, dest, 0, index - 1);
        dest[index - 1] = value;
        System.arraycopy(arrays, index - 1, dest, index, dest.length - index);
        return dest;
    }

    /**
     * 使用原生方式，拆分数组，添加到List
     *
     * @param arrays
     * @param <ARRAY>
     * @return
     */
    @NonNull
    @Size(min = 0)
    public static <ARRAY> ArrayList<ARRAY> addList(@Nullable @Size(min = 0) final ARRAY... arrays) {
        if (isEmpty(arrays)) return new ArrayList<>(0);
        ArrayList<ARRAY> list = new ArrayList<>();
        for (ARRAY array : arrays) {
            list.add(array);
        }
        return list;
    }

    /**
     * 在遍历操作时（注意如果删除或添加集合元素，千万不要用for遍历集合时进行删除操作，
     * 否则出现ConcurrentModificationException并发修改异常）；
     *
     * @param arrays
     * @param <ARRAY>
     * @return
     */
    @NonNull
    @Size(min = 0)
    public static <ARRAY> ArrayList<ARRAY> asList(@Nullable @Size(min = 0) final ARRAY... arrays) {
        if (isEmpty(arrays)) return new ArrayList<>(0);
        return new ArrayList<>(Arrays.asList(arrays));
    }

    /**
     * 数组 转字符转
     *
     * @param separator
     * @param array
     * @return
     */
    public static String toString(@NonNull final String separator, @Nullable final Object... array) {
        final int length = null == array ? 0 : array.length;
        if (length == 0) return "";
        final StringBuilder sb = new StringBuilder();
        sb.append(array[0]);
        for (int i = 1; i < length; i++) {
            sb.append(separator);
            sb.append(array[i]);
        }
        return sb.toString();
    }


}
