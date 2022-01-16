package com.colin.library.android.utils.encrypt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.ArrayUtil;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.data.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * 作者： ColinLu
 * 时间： 2019-10-09 10:00
 * <p>
 * 描述： Android 常用加密算法
 */
public final class EncryptUtil {

    ///////////////////////////////////////////////////////////////////////////
    // MD2加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteMD2(@Nullable final String data) {
        return digest(StringUtil.getBytes(data), Constants.ALGORITHM_MD_2);
    }

    @Nullable
    public static byte[] getByteMD2(@Nullable final byte[] data) {
        return digest(data, Constants.ALGORITHM_MD_2);
    }

    @Nullable
    public static byte[] getByteMD2(@Nullable final File file) {
        return digest(file, Constants.ALGORITHM_MD_2);
    }

    @Nullable
    public static String getStringMD2(@Nullable final String data) {
        return HexUtil.getString(digest(StringUtil.getBytes(data), Constants.ALGORITHM_MD_2));
    }

    @Nullable
    public static String getStringMD2(@Nullable final byte[] data) {
        return HexUtil.getString(digest(data, Constants.ALGORITHM_MD_2));
    }

    @Nullable
    public static String getStringMD2(@Nullable final File file) {
        return HexUtil.getString(digest(file, Constants.ALGORITHM_MD_2));
    }


    ///////////////////////////////////////////////////////////////////////////
    // MD5加密 信息摘要算法
    //1:压缩性:任意长度的数据，算出的md5值长度都是固定的
    //2:容易计算:从原数据计算出md5值很容易
    //3:抗修改性:对原数据进行任何的修改，哪怕只修改1个字节，所得到md5值都是固定的
    //4：强抗碰撞:已知原数据和其md5值，想找到一个具有相同md5值得数据(即伪造数据)是非常困难的
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteMD5(@Nullable final String data) {
        return digest(StringUtil.getBytes(data), Constants.ALGORITHM_MD_5);
    }

    @Nullable
    public static byte[] getByteMD5(@Nullable final byte[] data) {
        return digest(data, Constants.ALGORITHM_MD_5);
    }

    @Nullable
    public static byte[] getByteMD5(@Nullable final File file) {
        return digest(file, Constants.ALGORITHM_MD_5);
    }

    @Nullable
    public static String getStringMD5(@Nullable final String data) {
        return HexUtil.getString(digest(StringUtil.getBytes(data), Constants.ALGORITHM_MD_5));
    }

    @Nullable
    public static String getStringMD5(@Nullable final byte[] data) {
        return HexUtil.getString(digest(data, Constants.ALGORITHM_MD_5));
    }

    @Nullable
    public static String getStringMD5(@Nullable final File file) {
        return HexUtil.getString(digest(file, Constants.ALGORITHM_MD_5));
    }

    @Nullable
    public static byte[] getByteMD5(@Nullable final String data, @Nullable final String salt) {
        if (StringUtil.isEmpty(data) && StringUtil.isEmpty(salt)) return null;
        if (StringUtil.isEmpty(salt)) return getByteMD5(data);
        if (StringUtil.isEmpty(data)) return getByteMD5(salt);
        return getByteMD5(data + salt);
    }

    @Nullable
    public static byte[] getByteMD5(@Nullable final byte[] data, @Nullable final byte[] salt) {
        if (ArrayUtil.isEmpty(data) && ArrayUtil.isEmpty(salt)) return null;
        if (ArrayUtil.isEmpty(salt)) return getByteMD5(data);
        if (ArrayUtil.isEmpty(data)) return getByteMD5(salt);
        assert data != null;
        assert salt != null;
        byte[] dataSalt = new byte[data.length + salt.length];
        System.arraycopy(data, 0, dataSalt, 0, data.length);
        System.arraycopy(salt, 0, dataSalt, data.length, salt.length);
        return getByteMD5(dataSalt);
    }

    @Nullable
    public static String getStringMD5(@Nullable final String data, @Nullable final String salt) {
        if (StringUtil.isEmpty(data) && StringUtil.isEmpty(salt)) return null;
        if (StringUtil.isEmpty(salt)) return getStringMD5(data);
        if (StringUtil.isEmpty(data)) return getStringMD5(salt);
        return getStringMD5(data + salt);
    }

    @Nullable
    public static String getStringMD5(@Nullable final byte[] data, @Nullable final byte[] salt) {
        if (ArrayUtil.isEmpty(data) && ArrayUtil.isEmpty(salt)) return null;
        if (ArrayUtil.isEmpty(salt)) return getStringMD5(data);
        if (ArrayUtil.isEmpty(data)) return getStringMD5(salt);
        assert data != null;
        assert salt != null;
        byte[] dataSalt = new byte[data.length + salt.length];
        System.arraycopy(data, 0, dataSalt, 0, data.length);
        System.arraycopy(salt, 0, dataSalt, data.length, salt.length);
        return getStringMD5(dataSalt);
    }

    ///////////////////////////////////////////////////////////////////////////
    // SHA1加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteSHA1(@Nullable final String data) {
        return digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_1);
    }

    @Nullable
    public static byte[] getByteSHA1(@Nullable final byte[] data) {
        return digest(data, Constants.ALGORITHM_SHA_1);
    }

    @Nullable
    public static byte[] getByteSHA1(@Nullable final File file) {
        return digest(file, Constants.ALGORITHM_SHA_1);
    }

    @Nullable
    public static String getStringSHA1(@Nullable final String data) {
        return HexUtil.getString(digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_1));
    }

    @Nullable
    public static String getStringSHA1(@Nullable final byte[] data) {
        return HexUtil.getString(digest(data, Constants.ALGORITHM_SHA_1));
    }

    @Nullable
    public static String getStringSHA1(@Nullable final File file) {
        return HexUtil.getString(digest(file, Constants.ALGORITHM_SHA_1));
    }

    ///////////////////////////////////////////////////////////////////////////
    // SHA-224 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteSHA224(@Nullable final String data) {
        return digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_224);
    }

    @Nullable
    public static byte[] getByteSHA224(@Nullable final byte[] data) {
        return digest(data, Constants.ALGORITHM_SHA_224);
    }

    @Nullable
    public static byte[] getByteSHA224(@Nullable final File file) {
        return digest(file, Constants.ALGORITHM_SHA_224);
    }

    @Nullable
    public static String getStringSHA224(@Nullable final String data) {
        return HexUtil.getString(digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_224));
    }

    @Nullable
    public static String getStringSHA224(@Nullable final byte[] data) {
        return HexUtil.getString(digest(data, Constants.ALGORITHM_SHA_224));
    }

    @Nullable
    public static String getStringSHA224(@Nullable final File file) {
        return HexUtil.getString(digest(file, Constants.ALGORITHM_SHA_224));
    }

    ///////////////////////////////////////////////////////////////////////////
    // SHA-256 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteSHA256(@Nullable final String data) {
        return digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_256);
    }

    @Nullable
    public static byte[] getByteSHA256(@Nullable final byte[] data) {
        return digest(data, Constants.ALGORITHM_SHA_256);
    }

    @Nullable
    public static byte[] getByteSHA256(@Nullable final File file) {
        return digest(file, Constants.ALGORITHM_SHA_256);
    }

    @Nullable
    public static String getStringSHA256(@Nullable final String data) {
        return HexUtil.getString(digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_256));
    }

    @Nullable
    public static String getStringSHA256(@Nullable final byte[] data) {
        return HexUtil.getString(digest(data, Constants.ALGORITHM_SHA_256));
    }

    @Nullable
    public static String getStringSHA256(@Nullable final File file) {
        return HexUtil.getString(digest(file, Constants.ALGORITHM_SHA_256));
    }

    ///////////////////////////////////////////////////////////////////////////
    // SHA-384 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteSHA384(@Nullable final String data) {
        return digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_384);
    }

    @Nullable
    public static byte[] getByteSHA384(@Nullable final byte[] data) {
        return digest(data, Constants.ALGORITHM_SHA_384);
    }

    @Nullable
    public static byte[] getByteSHA384(@Nullable final File file) {
        return digest(file, Constants.ALGORITHM_SHA_384);
    }

    @Nullable
    public static String getStringSHA384(@Nullable final String data) {
        return HexUtil.getString(digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_384));
    }

    @Nullable
    public static String getStringSHA384(@Nullable final byte[] data) {
        return HexUtil.getString(digest(data, Constants.ALGORITHM_SHA_384));
    }

    @Nullable
    public static String getStringSHA384(@Nullable final File file) {
        return HexUtil.getString(digest(file, Constants.ALGORITHM_SHA_384));
    }

    ///////////////////////////////////////////////////////////////////////////
    // SHA-512 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteSHA512(@Nullable final String data) {
        return digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_512);
    }

    @Nullable
    public static byte[] getByteSHA512(@Nullable final byte[] data) {
        return digest(data, Constants.ALGORITHM_SHA_512);
    }

    @Nullable
    public static byte[] getByteSHA512(@Nullable final File file) {
        return digest(file, Constants.ALGORITHM_SHA_512);
    }

    @Nullable
    public static String getStringSHA512(@Nullable final String data) {
        return HexUtil.getString(digest(StringUtil.getBytes(data), Constants.ALGORITHM_SHA_512));
    }

    @Nullable
    public static String getStringSHA512(@Nullable final byte[] data) {
        return HexUtil.getString(digest(data, Constants.ALGORITHM_SHA_512));
    }

    @Nullable
    public static String getStringSHA512(@Nullable final File file) {
        return HexUtil.getString(digest(file, Constants.ALGORITHM_SHA_512));
    }


    ///////////////////////////////////////////////////////////////////////////
    // hmac md5 加密 信息摘要算法
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteHMacMD5(@Nullable final String data, @Nullable final String key) {
        return macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_MD_5);
    }

    @Nullable
    public static byte[] getByteHMacMD5(@Nullable final byte[] data, @Nullable final byte[] key) {
        return macTemplate(data, key, Constants.ALGORITHM_HMAC_MD_5);
    }

    @Nullable
    public static String getStringHMacMD5(@Nullable final String data, @Nullable final String key) {
        return HexUtil.getString(macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_MD_5));
    }

    @Nullable
    public static String getStringHMacMD5(@Nullable final byte[] data, @Nullable final byte[] key) {
        return HexUtil.getString(macTemplate(data, key, Constants.ALGORITHM_HMAC_MD_5));
    }

    ///////////////////////////////////////////////////////////////////////////
    // hmac sha1 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteHMacSHA1(@Nullable final String data, @Nullable final String key) {
        return macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_1);
    }

    @Nullable
    public static byte[] getByteHMacSHA1(@Nullable final byte[] data, @Nullable final byte[] key) {
        return macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_1);
    }

    @Nullable
    public static String getStringHMacSHA1(@Nullable final String data, @Nullable final String key) {
        return HexUtil.getString(macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_1));
    }

    @Nullable
    public static String getStringHMacSHA1(@Nullable final byte[] data, @Nullable final byte[] key) {
        return HexUtil.getString(macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_1));
    }

    ///////////////////////////////////////////////////////////////////////////
    // hmac sha224 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteHMacSHA224(@Nullable final String data, @Nullable final String key) {
        return macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_224);
    }

    @Nullable
    public static byte[] getByteHMacSHA224(@Nullable final byte[] data, @Nullable final byte[] key) {
        return macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_224);
    }

    @Nullable
    public static String getStringHMacSHA224(@Nullable final String data, @Nullable final String key) {
        return HexUtil.getString(macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_224));
    }

    @Nullable
    public static String getStringHMacSHA224(@Nullable final byte[] data, @Nullable final byte[] key) {
        return HexUtil.getString(macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_224));
    }


    ///////////////////////////////////////////////////////////////////////////
    // hmac sha256 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteHMacSHA256(@Nullable final String data, @Nullable final String key) {
        return macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_256);
    }

    @Nullable
    public static byte[] getByteHMacSHA256(@Nullable final byte[] data, @Nullable final byte[] key) {
        return macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_256);
    }

    @Nullable
    public static String getStringHMacSHA256(@Nullable final String data, @Nullable final String key) {
        return HexUtil.getString(macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_256));
    }

    @Nullable
    public static String getStringHMacSHA256(@Nullable final byte[] data, @Nullable final byte[] key) {
        return HexUtil.getString(macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_256));
    }


    ///////////////////////////////////////////////////////////////////////////
    // hmac sha384 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteHMacSHA384(@Nullable final String data, @Nullable final String key) {
        return macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_384);
    }

    @Nullable
    public static byte[] getByteHMacSHA384(@Nullable final byte[] data, @Nullable final byte[] key) {
        return macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_384);
    }

    @Nullable
    public static String getStringHMacSHA384(@Nullable final String data, @Nullable final String key) {
        return HexUtil.getString(macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_384));
    }

    @Nullable
    public static String getStringHMacSHA384(@Nullable final byte[] data, @Nullable final byte[] key) {
        return HexUtil.getString(macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_384));
    }


    ///////////////////////////////////////////////////////////////////////////
    // hmac sha512 加密
    ///////////////////////////////////////////////////////////////////////////
    @Nullable
    public static byte[] getByteHMacSHA512(@Nullable final String data, @Nullable final String key) {
        return macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_512);
    }

    @Nullable
    public static byte[] getByteHMacSHA512(@Nullable final byte[] data, @Nullable final byte[] key) {
        return macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_512);
    }

    @Nullable
    public static String getStringHMacSHA512(@Nullable final String data, @Nullable final String key) {
        return HexUtil.getString(macTemplate(StringUtil.getBytes(data), StringUtil.getBytes(key), Constants.ALGORITHM_HMAC_SHA_512));
    }

    @Nullable
    public static String getStringHMacSHA512(@Nullable final byte[] data, @Nullable final byte[] key) {
        return HexUtil.getString(macTemplate(data, key, Constants.ALGORITHM_HMAC_SHA_512));
    }


    /**
     * hash 内容加密模板
     *
     * @param data      加密内容
     * @param algorithm 算法，如MD5,SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
     * @return
     */
    public static byte[] digest(@Nullable final byte[] data, @NonNull final String algorithm) {
        if (ArrayUtil.isEmpty(data)) return null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(data);
            return messageDigest.digest();
        } catch (Exception e) {
            LogUtil.log(e);
        }
        return null;
    }

    /**
     * hash 文件加密模板
     *
     * @param file      加密文件
     * @param algorithm 算法，如MD5,SHA-1, SHA-224, SHA-256, SHA-384, SHA-512
     * @return
     */
    @Nullable
    public static byte[] digest(@Nullable final File file, @NonNull final String algorithm) {
        if (!FileUtil.isFile(file)) return null;
        FileInputStream fis = null;
        DigestInputStream is = null;
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance(algorithm);
            is = new DigestInputStream(fis, md);
            final byte[] buffer = new byte[Constants.FILE_BUFFER_SIZE];
            while (true) if (!(is.read(buffer) > 0)) break;
            md = is.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            LogUtil.log(e);
        } finally {
            IOUtil.close(fis, is);
        }
        return null;
    }

    /**
     * mac 加密模版方法
     *
     * @param data      加密内容
     * @param key       密钥
     * @param algorithm 算法
     * @return 指定加密算法和密钥, 加密后的数据
     */
    @Nullable
    public static byte[] macTemplate(@Nullable final byte[] data, @Nullable final byte[] key, @NonNull final String algorithm) {
        if (data == null || data.length == 0 || key == null || key.length == 0) return null;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            LogUtil.log(e);
            return null;
        }
    }


}
