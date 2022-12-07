package com.colin.library.android.annotation;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2022-02-14 21:25
 * <p>
 * 描述： 加密方式
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({Algorithm.MD_2, Algorithm.MD_5, Algorithm.SHA_1, Algorithm.SHA_224, Algorithm.SHA_256,
        Algorithm.SHA_384, Algorithm.SHA_512, Algorithm.HMAC_MD_5, Algorithm.HMAC_SHA_1, Algorithm.HMAC_SHA_224
        , Algorithm.HMAC_SHA_256, Algorithm.HMAC_SHA_384, Algorithm.HMAC_SHA_512, Algorithm.DES, Algorithm.AES, Algorithm.RSA})
public @interface Algorithm {
    String MD_2 = "MD2";
    String MD_5 = "MD5";
    String SHA_1 = "SHA-1";
    String SHA_224 = "SHA-224";
    String SHA_256 = "SHA-256";
    String SHA_384 = "SHA-384";
    String SHA_512 = "SHA-512";
    String HMAC_MD_5 = "HmacMD5";
    String HMAC_SHA_1 = "HmacSHA1";
    String HMAC_SHA_224 = "HmacSHA224";
    String HMAC_SHA_256 = "HmacSHA256";
    String HMAC_SHA_384 = "HmacSHA384";
    String HMAC_SHA_512 = "HmacSHA512";
    String DES = "DES";
    String AES = "AES";
    String RSA = "RSA";
}
