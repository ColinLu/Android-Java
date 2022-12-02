package com.colin.library.android.utils.data;

import androidx.annotation.NonNull;

import java.util.TimeZone;

/**
 * 作者： ColinLu
 * 时间： 2021-12-25 08:09
 * <p>
 * 描述： Util 常量
 */
public interface Constants {
    ///////////////////////////////////////////////////////////////////////////
    // 分隔符
    ///////////////////////////////////////////////////////////////////////////
    String FILE_SEP = System.getProperty("file.separator");
    String LINE_SEP = System.getProperty("line.separator");

    int INVALID = -1;
    //文件读写缓冲区大小
    int FILE_BUFFER_SIZE = 10 * 1024;
    int BUFFER_FILE_SIZE = 10 * 1024;
    int BUFFER_STREAM_SIZE = 4 * 1024;
    ///////////////////////////////////////////////////////////////////////////
    // time of format
    ///////////////////////////////////////////////////////////////////////////
    TimeZone TIME_ZONE_GMT = TimeZone.getTimeZone("GMT");
    String FORMAT_DAY_PATTERN = "yyyy-MM-dd";
    String FORMAT_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss:SSS";
    String FORMAT_TIME_HTTP = "EEE, dd MMM y HH:mm:ss 'GMT'";

    ///////////////////////////////////////////////////////////////////////////
    // 剪切板 key
    ///////////////////////////////////////////////////////////////////////////
    String CLIP_LABEL_TEXT = "TEXT";
    String CLIP_LABEL_URI = "URI";
    String CLIP_LABEL_INTENT = "INTENT";

    String HEIGHT_STATUS_BAR = "status_bar_height";
    String HEIGHT_NAVIGATION_BAR = "navigation_bar_height";
    /*常用mime 类型值*/
    @NonNull
    String MIME_TYPE_ALL = "*/*";
    @NonNull
    String MIME_TYPE_FILE = "file/*";
    @NonNull
    String MIME_TYPE_IMAGE = "image/*";
    /*多媒体后缀*/
    @NonNull
    String DEFAULT_IMAGE_SUFFIX = ".jpg";
    @NonNull
    String DEFAULT_MUSIC_SUFFIX = ".mp3";
    @NonNull
    String DEFAULT_VIDEO_SUFFIX = ".mp4";

    @NonNull
    String ENCODE_UNICODE = "Unicode";
    @NonNull
    String ENCODE_UTF_16 = "UTF-16";
    @NonNull
    String ENCODE_UTF_16BE = "UTF-16BE";
    @NonNull
    String ENCODE_UTF_32 = "UTF-32";
    @NonNull
    String ENCODE_ISO_8859_1 = "iso8859-1";
    /*常见加密*/
    @NonNull
    byte[] DIGITS_BYTE_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    @NonNull
    byte[] DIGITS_BYTE_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    ///////////////////////////////////////////////////////////////////////////
    // 正则:http://tool.oschina.net/regex
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Regex of exact mobile.
     * <p>china mobile: 134(0-8), 135, 136, 137, 138, 139, 147, 150, 151, 152, 157, 158, 159, 178, 182, 183, 184, 187, 188, 198</p>
     * <p>china unicom: 130, 131, 132, 145, 155, 156, 166, 171, 175, 176, 185, 186</p>
     * <p>china telecom: 133, 153, 173, 177, 180, 181, 189, 199</p>
     * <p>global star: 1349</p>
     * <p>virtual operator: 170</p>
     */
    String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(16[6])|(17[0,1,3,5-8])|(18[0-9])|(19[8,9]))\\d{8}$";
    /**
     * Regex of username.
     * scope for "a-z", "A-Z", "0-9", "_", "Chinese character"</p>
     * can't end with "_"</p>
     * length is between 6 to 20</p>
     */
    String REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
    /*Regex of simple mobile*/
    String REGEX_MOBILE = "^[1]\\d{10}$";
    /*Regex of telephone number.*/
    String REGEX_TEL = "\\d{3}-\\d{8}|\\d{4}-\\{7,8}";
    /*Regex of id card number which length is 15*/
    String REGEX_IDENTITY_15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    /*Regex of id card number which length is 18*/
    String REGEX_IDENTITY_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    /*Regex of email*/
    String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /*Regex of url*/
    String REGEX_URL = "[a-zA-z]+://[^\\s]*";
    /*Regex of Chinese character*/
    String REGEX_ZH = "^[\\u4e00-\\u9fa5]+$";
    /*Regex of date which pattern is "yyyy-MM-dd"*/
    String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    /*Regex of ip address*/
    String REGEX_IP = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
    /*Regex of double-byte characters*/
    String REGEX_DOUBLE_BYTE_CHAR = "[^\\x00-\\xff]";
    /*Regex of blank line*/
    String REGEX_BLANK_LINE = "\\n\\s*\\r";
    /*Regex of postal code in China*/
    String REGEX_CHINA_POSTAL_CODE = "[1-9]\\d{5}(?!\\d)";
    /*Regex of positive integer 0>*/
    String REGEX_POSITIVE_INTEGER = "^[1-9]\\d*$";
    /*Regex of negative integer <0*/
    String REGEX_NEGATIVE_INTEGER = "^-[1-9]\\d*$";
    /*Regex of integer !=0 */
    String REGEX_INTEGER = "^-?[1-9]\\d*$";
    /*Regex of non-negative integer >=0*/
    String REGEX_NOT_NEGATIVE_INTEGER = "^[1-9]\\d*|0$";
    /*Regex of non-positive integer  <=0*/
    String REGEX_NOT_POSITIVE_INTEGER = "^-[1-9]\\d*|0$";
    /*Regex of positive float*/
    String REGEX_POSITIVE_FLOAT = "^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$";
    /*Regex of negative float*/
    String REGEX_NEGATIVE_FLOAT = "^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$";
    /*Regex of hex*/
    String REGEX_HEX = "^[A-Fa-f0-9]+$";
    /*Regex of plate code*/
    String REGEX_PLATE_CODE = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领 A-Z]{1}[A-Z]{1}(([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领 A-Z]{1}[A-Z]{1}[A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9 挂学警港澳]{1})";

    String REGEX_IP_V4 = "\"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$\"";
    String REGEX_IP_V6 = "^[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4}){7}$";
    //压缩过
    String REGEX_IP_V6_HEX = "^(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)::(([0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4}){0,5})?)$";

    int BITMAP_QUALITY_HEIGHT = 100;
    int BITMAP_QUALITY_LOW = 0;
}
