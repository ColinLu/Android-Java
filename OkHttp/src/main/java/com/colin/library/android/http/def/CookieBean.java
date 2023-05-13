package com.colin.library.android.http.def;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.encrypt.HexUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import okhttp3.Cookie;

/**
 * 作者： ColinLu
 * 时间： 2020-01-02 11:28
 * <p>
 * 描述： Serializable 序列化 操作 网络传输 中使用
 * <p>
 * 根据请求结果生成对应的缓存实体类，以下为缓存相关的响应头
 * Cache-Control: public                             响应被缓存，并且在多用户间共享
 * Cache-Control: private                            响应只能作为私有缓存，不能在用户之间共享
 * Cache-Control: no-cache                           提醒浏览器要从服务器提取文档进行验证
 * Cache-Control: no-store                           绝对禁止缓存（用于机密，敏感文件）
 * Cache-Control: max-age=60                         60秒之后缓存过期（相对时间）,优先级比Expires高
 * Date: Mon, 19 Nov 2012 08:39:00 GMT               当前response发送的时间
 * Expires: Mon, 19 Nov 2012 08:40:01 GMT            缓存过期的时间（绝对时间）
 * Last-Modified: Mon, 19 Nov 2012 08:38:01 GMT      服务器端文件的最后修改时间
 * ETag: "20b1add7ec1cd1:0"                          服务器端文件的ETag值
 * 如果同时存在cache-control和Expires，浏览器总是优先使用cache-control
 */
public class CookieBean implements Serializable {
    private static final long serialVersionUID = 6374381323722046732L;

    public static final String TABLE_COOKIE = "http_cookie";
    public static final String HOST = "host";
    public static final String NAME = "name";
    public static final String DOMAIN = "domain";
    public static final String DATA = "data";

    public String host;
    public String name;
    public String value;
    public String domain;
    private transient Cookie cookie;

    public CookieBean(@NonNull String host, @NonNull Cookie cookie) {
        this.host = host;
        this.cookie = cookie;
        this.name = cookie.name();
        this.value = cookie.value();
        this.domain = cookie.domain();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CookieBean that = (CookieBean) o;
        if (!TextUtils.equals(host, that.host)) return false;
        if (!TextUtils.equals(name, that.name)) return false;
        return TextUtils.equals(domain, that.domain);
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        return result;
    }

    public Cookie getCookie() {
        return cookie;
    }


    /**
     * 将cookie写到对象流中
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(this.cookie.name());
        out.writeObject(this.cookie.value());
        out.writeLong(this.cookie.expiresAt());
        out.writeObject(this.cookie.domain());
        out.writeObject(this.cookie.path());
        out.writeBoolean(this.cookie.secure());
        out.writeBoolean(this.cookie.httpOnly());
        out.writeBoolean(this.cookie.hostOnly());
        out.writeBoolean(this.cookie.persistent());
    }

    /**
     * 从对象流中构建cookie对象
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        final String name = (String) in.readObject();
        final String value = (String) in.readObject();
        final long expiresAt = in.readLong();
        final String domain = (String) in.readObject();
        final String path = (String) in.readObject();
        final boolean secure = in.readBoolean();
        final boolean httpOnly = in.readBoolean();
        final boolean hostOnly = in.readBoolean();
        final boolean persistent = in.readBoolean();
        Cookie.Builder builder = new Cookie.Builder()
                .name(name)
                .value(value)
                .expiresAt(expiresAt)
                .path(path);
        builder = hostOnly ? builder.hostOnlyDomain(domain) : builder.domain(domain);
        builder = secure ? builder.secure() : builder;
        builder = httpOnly ? builder.httpOnly() : builder;
        this.cookie = builder.build();
    }

    /*cookies 序列化成 string*/
    @Nullable
    public static String encode(@NonNull final String host, @NonNull final Cookie cookie) {
        return HexUtil.getString(toBytes(host, cookie));
    }

    /*将字符串反序列化成cookies*/
    public static Cookie decode(@NonNull final String encode) {
        return toCookie(HexUtil.getBytes(encode));
    }

    @Nullable
    public static byte[] toBytes(@Nullable final String host, @Nullable final Cookie cookie) {
        if (null == host || null == cookie) return null;
        final CookieBean cookieBean = new CookieBean(host, cookie);
        ObjectOutputStream outputStream = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookieBean);
            os.flush();
            os.close();
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(outputStream);
        }
        return null;
    }


    @Nullable
    public static Cookie toCookie(@Nullable final byte[] bytes) {
        if (null == bytes || bytes.length == 0) return null;
        ByteArrayInputStream is = null;
        try {
            is = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(is);
            return ((CookieBean) objectInputStream.readObject()).getCookie();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(is);
        }
        return null;
    }


}
