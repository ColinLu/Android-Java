package com.colin.library.android.utils.encrypt;


import androidx.annotation.Nullable;


import com.colin.library.android.utils.ArrayUtil;
import com.colin.library.android.utils.FileUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.data.Constants;

import java.io.File;


/**
 * 作者： ColinLu
 * 时间： 2019-02-21 14:08
 * <p>
 * 描述： MD5加密
 * 不可逆加密算法:不可逆加密算法的特征是加密过程中不需要使用密钥，输入明文后由系统直接经过加密算法处理成密文，这种加密后的数据是无法被解密的，
 * 只有重新输入明文，并再次经过同样不可逆的加密算法处理，得到相同的加密密文并被系统重新识别后，才能真正解密。显然，在这类加密过程中，加密是自
 * 己，解密还得是自己，而所谓解密，实际上就是重新加一次密，所应用的“密码”也就是输入的明文。不可逆加密算法不存在密钥保管和分发问题，非常适合在
 * 分布式网络系统上使用，但因加密计算复杂，工作量相当繁重，通常只在数据量有限的情形下使用，如广泛应用在计算机系统中的口令加密，利用的就是不可
 * 逆加密算法。近年来，随着计算机系统性能的不断提高，不可逆加密的应用领域正在逐渐增大。在计算机网络中应用较多不可逆加密算法的有RSA公司发明的
 * MD5算法和由美国国家标准局建议的不可逆加密标准SHS(Secure Hash Standard:安全杂乱信息标准)等
 */
public final class MD5Util {

    /**
     * 加密内容 (32 位小写 MD5)
     *
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    @Nullable
    public static byte[] getByte(@Nullable final byte[] data) {
        if (ArrayUtil.isEmpty(data)) return null;
        return EncryptUtil.digest(data, Constants.ALGORITHM_MD_5);
    }

    /**
     * 加密内容 (32 位小写 MD5)
     *
     * @param data 待加密数据
     * @return MD5 加密后的字符串
     */
    @Nullable
    public static byte[] getByte(@Nullable final String data) {
        if (StringUtil.isEmpty(data)) return null;
        return EncryptUtil.digest(data.getBytes(), Constants.ALGORITHM_MD_5);
    }

    /**
     * 加密内容 (32 位小写 MD5)
     *
     * @param file 待加密文件
     * @return MD5 加密后的字符串
     */
    @Nullable
    public static byte[] getByte(@Nullable File file) {
        if (!FileUtil.isFile(file)) return null;
        return EncryptUtil.digest(file, Constants.ALGORITHM_MD_5);
    }

    public static byte[] getByte(@Nullable final byte[] data, @Nullable final byte[] salt) {
        return EncryptUtil.getByteMD5(data, salt);
    }

    public static byte[] getByte(@Nullable final String data, @Nullable final String salt) {
        return EncryptUtil.getByteMD5(data, salt);
    }

    /* hex string */
    @Nullable
    public static String getString(@Nullable final byte[] data) {
        if (ArrayUtil.isEmpty(data)) return null;
        return HexUtil.getString(EncryptUtil.digest(data, Constants.ALGORITHM_MD_5));
    }

    /* hex string */
    @Nullable
    public static String getString(@Nullable final String data) {
        if (StringUtil.isEmpty(data)) return null;
        return HexUtil.getString(EncryptUtil.digest(data.getBytes(), Constants.ALGORITHM_MD_5));
    }


    /* hex string */
    @Nullable
    public static String getString(@Nullable final File file) {
        if (!FileUtil.isFile(file)) return null;
        return HexUtil.getString(EncryptUtil.digest(file, Constants.ALGORITHM_MD_5));
    }

    public static String getString(@Nullable final byte[] data, @Nullable final byte[] salt) {
        return EncryptUtil.getStringMD5(data, salt);
    }

    public static String getString(@Nullable final String data, @Nullable final String salt) {
        return EncryptUtil.getStringMD5(data, salt);
    }

}

