package com.colin.library.android.annotation;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import androidx.annotation.RestrictTo;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-02-14 21:25
 * <p>
 * 描述： 编码方式
 */
@RestrictTo(LIBRARY_GROUP_PREFIX)
@Retention(RetentionPolicy.SOURCE)
@StringDef({Encode.UTF_8, Encode.UTF_16, Encode.UTF_32, Encode.UTF_16BE, Encode.UTF_16LE,
        Encode.GB_2312, Encode.GBK, Encode.UNICODE, Encode.ISO_8859_1, Encode.US_ASCII})
public @interface Encode {
    String UTF_8 = "UTF-8";
    String UTF_16 = "UTF-16";
    String UTF_32 = "UTF-32";
    String UTF_16BE = "UTF-16BE";
    String UTF_16LE = "UTF-16LE";
    String GB_2312 = "GB2312";
    String GBK = "GBK";
    String UNICODE = "Unicode";
    String US_ASCII = "US_ASCII";
    String ISO_8859_1 = "iso8859-1";
}