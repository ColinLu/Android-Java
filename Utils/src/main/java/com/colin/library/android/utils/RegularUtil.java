package com.colin.library.android.utils;


import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式帮助类
 * .	匹配除换行符以外的任意字符
 * \w	匹配字母或数字或下划线
 * \s	匹配任意的空白符
 * \d	匹配数字
 * \b	匹配单词的开始或结束
 * ^	匹配字符串的开始
 * $	匹配字符串的结
 * <p>
 * *	重复零次或更多次
 * +	重复一次或更多次
 * ?	重复零次或一次
 * {n}	重复n次
 * {n,}	重复n次或更多次
 * {n,m}	重复n到m次
 * <p>
 * \W	匹配任意不是字母，数字，下划线，汉字的字符
 * \S	匹配任意不是空白符的字符
 * \D	匹配任意非数字的字符
 * \B	匹配不是单词开头或结束的位置
 * [^x]	匹配除了x以外的任意字符
 * [^aeiou]	匹配除了aeiou这几个字母以外的任意字符
 * <p>
 * "^\d+$"　　                    //非负整数（正整数 + 0）
 * "^[0-9]*[1-9][0-9]*$"　　      //正整数
 * "^((-\d+)|(0+))$"　　          //非正整数（负整数 + 0）
 * "^-[0-9]*[1-9][0-9]*$"　　     //负整数
 * "^-?\d+$"　　　　               //整数
 * "^\d+(\.\d+)?$"　　            //非负浮点数（正浮点数 + 0）
 * "^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$"　　  //正浮点数
 * "^((-\d+(\.\d+)?)|(0+(\.0+)?))$"　　                                                   //非正浮点数（负浮点数 + 0）
 * "^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$"　　//负浮点数
 * "^(-?\d+)(\.\d+)?$"  //浮点数
 * "^[A-Za-z]+$"  //由26个英文字母组成的字符串
 * "^[A-Z]+$"  //由26个英文字母的大写组成的字符串
 * "^[a-z]+$"  //由26个英文字母的小写组成的字符串
 * "^[A-Za-z0-9]+$" //由数字和26个英文字母组成的字符串
 * "^\w+$" //由数字、26个英文字母或者下划线组成的字符串
 * "^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$" //email地址
 * "^[a-zA-z]+://(\w+(-\w+)*)(\.(\w+(-\w+)*))*(\?\S*)?$" //url
 * <p>
 */
public final class RegularUtil {
    private RegularUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /**
     * 正则表达式 判断是否移动手机号码
     *
     * @param mobile 国内移动手机号码
     * @return
     */
    public static boolean isMobile(@Nullable final CharSequence mobile) {
        return matcher(Constants.REGEX_MOBILE, mobile);
    }

    /**
     * 正则表达式 判断是否国内座机号码
     *
     * @param telephone
     * @return
     */
    public static boolean isTelephone(@Nullable final CharSequence telephone) {
        return matcher(Constants.REGEX_TEL, telephone);
    }

    /**
     * 正则表达式 判断身份证号码是否争取
     *
     * @param identity
     * @return
     */
    public static boolean isIdentity(@Nullable final CharSequence identity) {
        if (identity == null || identity.length() == 0) return false;
        if (identity.length() != 15 && identity.length() != 18) return false;
        return matcher(Constants.REGEX_IDENTITY_18, identity) || matcher(Constants.REGEX_IDENTITY_15, identity);
    }

    /**
     * 正则表达式 判断Email地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(@Nullable final CharSequence email) {
        return matcher(Constants.REGEX_EMAIL, email);
    }

    /**
     * 正则表达式 合法的车牌号码
     *
     * @param plateCode
     * @return
     */
    public static boolean isPlateCode(@Nullable final CharSequence plateCode) {
        return matcher(Constants.REGEX_PLATE_CODE, plateCode);
    }

    /**
     * 正则表达式 合法的IP地址
     * 1.  为什么三位数的匹配放在二位数/一位数的前面？因为正则表达式规则之一：最先开始的匹配拥有最高的优先权。
     * 2.  0.0.0.0和255.255.255.255是合法存在的IP地址，你知道是为什么吗？
     * 3.  192.169.01.108这种数字前面多带了个0的类型的，在这里不是合法的，为什么要这样？
     * 4.  为什么前面在最前面要有?:呢？它在这里有什么用？
     *
     * @param ip
     * @return
     */
    public static boolean isIp(@Nullable final CharSequence ip) {
        return matcher(Constants.REGEX_IP, ip);
    }

    /**
     * 正则表达式 验证验证输入汉字
     *
     * @param chinese
     * @return 如果是符合格式的字符串, 返回 <b>true </b>,否则为 <b>false </b>
     */
    public static boolean isChinese(@Nullable final CharSequence chinese) {
        return matcher(Constants.REGEX_ZH, chinese);
    }


    /**
     * 正则表达式 16进制
     *
     * @param hex
     * @return
     */
    public static boolean isHex(@Nullable final CharSequence hex) {
        return matcher(Constants.REGEX_HEX, hex);
    }


    /**
     * 正则表达式 字符串中是否含有数字
     *
     * @param data
     * @return
     */
    public boolean checkNumber(@Nullable final CharSequence data) {
        return matcher("\".*\\\\d+.*\"", data);
    }

    /**
     * 正则表达式 验证密码 必须含有数字和字母  默认 6-20
     * <p>
     * 强：字母+数字+特殊字符
     * ^(?![a-zA-z]+$)(?!\d+$)(?![!@#$%^&*]+$)(?![a-zA-z\d]+$)(?![a-zA-z!@#$%^&*]+$)(?![\d!@#$%^&*]+$)[a-zA-Z\d!@#$%^&*]+$
     * <p>
     * 中：字母+数字，字母+特殊字符，数字+特殊字符
     * ^(?![a-zA-z]+$)(?!\d+$)(?![!@#$%^&*]+$)[a-zA-Z\d!@#$%^&*]+$
     * <p>
     * 弱：纯数字，纯字母，纯特殊字符
     * ^(?:\d+|[a-zA-Z]+|[!@#$%^&*]+)$
     *
     * @param password
     * @return
     */
    public static boolean verifyPassword(@Nullable CharSequence password) {
        return verifyPassword(password, 6, 20);
    }

    public static boolean verifyPassword(@Nullable CharSequence password, int min, int max) {
        final String regex = String.format(Locale.US, "^(?![0-9]+$)(?![a-zA-Z]+$)[a-zA-Z0-9]{%d,%d}", min, max);
        return matcher(regex, password);
    }


    /**
     * 正则判断
     *
     * @param regex 正则规则
     * @param value 需要比较的值
     * @return
     */
    public static boolean matcher(@Nullable final String regex, @Nullable final CharSequence value) {
        if (StringUtil.isEmpty(regex) || StringUtil.isEmpty(value)) return false;
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * 正则判断
     *
     * @param regex 正则规则
     * @param value 需要比较的值
     * @return
     */
    public static int matcherCount(@Nullable final String regex, @Nullable final CharSequence value) {
        int count = 0;
        if (StringUtil.isEmpty(regex) || StringUtil.isEmpty(value)) return count;
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(value);
        while (matcher.find()) count += 1;
        return count;
    }

}
