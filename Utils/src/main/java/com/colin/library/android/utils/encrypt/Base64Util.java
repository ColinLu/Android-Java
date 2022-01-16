package com.colin.library.android.utils.encrypt;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.colin.library.android.utils.BitmapUtil;
import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.data.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public final class Base64Util {
    @NonNull
    private final static char[] ENCODE_BASE64_CHARS = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
            'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    @NonNull
    private final static byte[] DECODE_BASE64_BYTES = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56,
            57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
            13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29,
            30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1,
            -1, -1, -1, -1};

    @Nullable
    public static String getString(@Nullable String data, boolean isEncode) {
        if (TextUtils.isEmpty(data)) return null;
        byte[] bytes = getBytes(data, Constants.UTF_8);
        if (null == bytes || bytes.length == 0) return null;
        byte[] values = isEncode ? encode(bytes) : decode(bytes);
        if (null == values || values.length == 0) return null;
        return new String(values);
    }

    @Nullable
    public static String getString(@Nullable byte[] bytes, boolean isEncode) {
        if (null == bytes || bytes.length == 0) return null;
        byte[] values = isEncode ? encode(bytes) : decode(bytes);
        if (null == values || values.length == 0) return null;
        return new String(values);
    }

    @Nullable
    public static byte[] getBytes(@Nullable String data, boolean isEncode) {
        if (TextUtils.isEmpty(data)) return null;
        byte[] bytes = getBytes(data, Constants.UTF_8);
        if (null == bytes || bytes.length == 0) return null;
        return isEncode ? encode(bytes) : decode(bytes);
    }

    @Nullable
    public static byte[] getBytes(@Nullable byte[] bytes, boolean isEncode) {
        if (null == bytes || bytes.length == 0) return null;
        return isEncode ? encode(bytes) : decode(bytes);
    }

    /**
     * 加密
     *
     * @param data
     * @return
     */
    @Nullable
    public static byte[] encode(@Nullable byte[] data) {
        if (null == data || data.length == 0) return null;
        final StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xFF;
            if (i == len) {
                sb.append(ENCODE_BASE64_CHARS[b1 >>> 2]);
                sb.append(ENCODE_BASE64_CHARS[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(ENCODE_BASE64_CHARS[b1 >>> 2]);
                sb.append(ENCODE_BASE64_CHARS[((b1 & 0x03) << 4) | ((b2 & 0xF0) >>> 4)]);
                sb.append(ENCODE_BASE64_CHARS[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(ENCODE_BASE64_CHARS[b1 >>> 2]);
            sb.append(ENCODE_BASE64_CHARS[((b1 & 0x03) << 4) | ((b2 & 0xF0) >>> 4)]);
            sb.append(ENCODE_BASE64_CHARS[((b2 & 0x0f) << 2) | ((b3 & 0xC0) >>> 6)]);
            sb.append(ENCODE_BASE64_CHARS[b3 & 0x3f]);
        }

        return getBytes(sb.toString(), Constants.UTF_8);
    }

    /**
     * 解密
     *
     * @param data
     * @return
     */
    @Nullable
    public static byte[] decode(String data) {
        if (null == data || data.length() == 0) return null;
        return decode(getBytes(data, Constants.UTF_8));
    }

    @Nullable
    public static byte[] decode(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return null;
        StringBuffer sb = new StringBuffer();
        int len = bytes.length;
        int i = 0;
        int b1, b2, b3, b4;
        while (i < len) {
            do {
                b1 = DECODE_BASE64_BYTES[bytes[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1) break;

            do {
                b2 = DECODE_BASE64_BYTES[bytes[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1) break;

            sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
            do {
                b3 = bytes[i++];
                if (b3 == 61) return getBytes(sb.toString(), Constants.ENCODE_ISO_8859_1);
                b3 = DECODE_BASE64_BYTES[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1) break;

            sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
            do {
                b4 = bytes[i++];
                if (b4 == 61) return getBytes(sb.toString(), Constants.ENCODE_ISO_8859_1);
                b4 = DECODE_BASE64_BYTES[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1) break;
            sb.append((char) (((b3 & 0x03) << 6) | b4));
        }
        return getBytes(sb.toString(), Constants.ENCODE_ISO_8859_1);
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    @Nullable
    public static String getBase64(@Nullable final Bitmap bitmap) {
        if (BitmapUtil.isEmpty(bitmap)) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        IOUtil.flush(baos);
        IOUtil.close(baos);
        byte[] bitmapBytes = baos.toByteArray();
        return getString(bitmapBytes, true);
    }

    /**
     * base64 转图片 bitmap
     *
     * @param string Base64字符串  可以为空  包含前缀(data:image/png;base64) 或不包含
     * @return
     */
    @Nullable
    public static Bitmap toBitmap(@Nullable final String string) {
        if (TextUtils.isEmpty(string)) return null;
        try {
            String[] split = string.split(",");
            if (split.length == 0) return null;
            byte[] bitmapArray = getBytes(split[split.length - 1], false);
            if (null == bitmapArray || bitmapArray.length == 0) return null;
            return BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param base64Code
     * @param savePath
     */
    public static void toFile(String base64Code, String savePath) {
        if (TextUtils.isEmpty(base64Code) || TextUtils.isEmpty(savePath)) return;
        byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(savePath);
            out.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.flush(out);
            IOUtil.close(out);
        }
    }

    private static byte[] getBytes(String data, String charsetName) {
        if (null == data || data.length() == 0) return null;
        try {
            return data.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
