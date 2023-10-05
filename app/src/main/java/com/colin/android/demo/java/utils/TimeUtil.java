package com.colin.android.demo.java.utils;

import java.time.LocalDate;

public class TimeUtil {

    public static void main(String[] args) {
        System.out.println(formatTime(2002, 2, 28));
        System.out.println(formatTime(2002, 2, 29));
        System.out.println(formatTime(2002, 3, 1));
    }


    private static long formatTime(int... arrays) {
        return LocalDate.of(arrays[0], arrays[1], arrays[2])
                        .toEpochDay();
    }
}
