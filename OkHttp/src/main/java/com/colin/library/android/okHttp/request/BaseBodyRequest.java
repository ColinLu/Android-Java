package com.colin.library.android.okHttp.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.OkHttp;
import com.colin.library.android.okHttp.annotation.Method;
import com.colin.library.android.okHttp.bean.ByteBody;
import com.colin.library.android.okHttp.bean.ContentBody;
import com.colin.library.android.okHttp.bean.FileBody;
import com.colin.library.android.okHttp.progress.IProgress;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.encrypt.MD5Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2022-02-17 00:38
 * <p>
 * 描述： TODO
 */
public class BaseBodyRequest<Returner> extends BaseRequest<Returner> implements IBody<Returner> {
    protected transient IProgress mProgress;

    protected transient boolean mSpliceUrl;                 //拼接地址
    protected transient boolean mMultipartBody;             //多表单
    protected transient RequestBody mRequestBody;           //请求体
    protected transient final HashMap<String, IRequestBody> mRequestBodyMap;
    protected transient final List<FileBody> mFileBodyList;

    public BaseBodyRequest(@NonNull String url, @NonNull @Method String method) {
        super(url, method);
        this.mRequestBodyMap = new HashMap<>();
        this.mFileBodyList = new ArrayList<>();
    }

    @NonNull
    @Override
    public String getUrl() {
        return !mSpliceUrl ? mUrl : super.getUrl();
    }

    @NonNull
    @Override
    public String getContentType() {
        if (isMultipart()) return OkHttp.CONTENT_TYPE_MULTIPART;
        else return super.getContentType();
    }

    @NonNull
    @Override
    public RequestBody getRequestBody() {
        final RequestBody requestBody = isMultipart() ? getMultipartBody(getEncode()) : getSingleRequestBody(getEncode());
        return mProgress != null ? new ProgressRequestBody(requestBody, mProgress) : requestBody;
    }

    @Override
    public Returner json(@Nullable String json) {
        return content(json, OkHttp.CONTENT_TYPE_JSON, null);
    }

    @Override
    public Returner json(@Nullable String json, @Nullable String encode) {
        return content(json, OkHttp.CONTENT_TYPE_JSON, encode);
    }

    @Override
    public Returner json(@Nullable JSONObject json) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, null);
    }

    @Override
    public Returner json(@Nullable JSONObject json, @Nullable String encode) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, encode);
    }

    @Override
    public Returner json(@Nullable JSONArray json) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, null);
    }

    @Override
    public Returner json(@Nullable JSONArray json, @Nullable String encode) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, encode);
    }

    @Override
    public Returner xml(@Nullable String xml) {
        return content(xml, OkHttp.CONTENT_TYPE_XML, null);
    }

    @Override
    public Returner xml(@Nullable String xml, @Nullable String encode) {
        return content(xml, OkHttp.CONTENT_TYPE_XML, encode);
    }

    @Override
    public Returner content(@Nullable String content) {
        return content(content, OkHttp.CONTENT_TYPE_DEFAULT, null);
    }

    @Override
    public Returner content(@Nullable String content, @Nullable String contentType) {
        return content(content, OkHttp.CONTENT_TYPE_DEFAULT, null);
    }

    @Override
    public Returner content(@Nullable String content, @Nullable String contentType, @Nullable String encode) {
        if (!StringUtil.isEmpty(content)) {
            mRequestBodyMap.put(MD5Util.getString(content), new ContentBody(content, contentType, encode));
        }
        return (Returner) this;
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes) {
        return bytes(bytes, OkHttp.CONTENT_TYPE_STREAM, null, 0, null == bytes ? 0 : bytes.length);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @Nullable String encode) {
        return bytes(bytes, OkHttp.CONTENT_TYPE_STREAM, encode, 0, null == bytes ? 0 : bytes.length);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, int offset, int count) {
        return bytes(bytes, OkHttp.CONTENT_TYPE_STREAM, null, offset, count);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @Nullable String contentType, int offset, int count) {
        return bytes(bytes, contentType, null, offset, count);

    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @Nullable String contentType, @Nullable String encode, int offset, int count) {
        if (bytes != null && bytes.length > 0 && count > offset && bytes.length >= count) {
            mRequestBodyMap.put(MD5Util.getString(bytes), new ByteBody(bytes, contentType, encode, offset, count));
        }
        return (Returner) this;
    }

    @Override
    public Returner file(@Nullable File file) {
        return file(file, file == null ? null : file.getName(), null);
    }

    @Override
    public Returner file(@Nullable File file, @Nullable String encode) {
        return file(file, file == null ? null : file.getName(), encode);
    }

    @Override
    public Returner file(@Nullable File file, @Nullable String key, @Nullable String encode) {
        if (file != null && file.isFile()) {
            mFileBodyList.add(new FileBody(file, StringUtil.isEmpty(key) ? file.getName() : key, encode));
        }
        return (Returner) this;
    }

    @Override
    public Returner file(@Nullable List<File> files) {
        return file(files, null);
    }

    @Override
    public Returner file(@Nullable List<File> files, @Nullable String encode) {
        if (files != null && !files.isEmpty()) {
            for (File file : files) file(file, file == null ? null : file.getName(), encode);
        }
        return (Returner) this;

    }

    @Override
    public Returner file(@Nullable String key, @Nullable File... files) {
        if (!StringUtil.isEmpty(key) && files != null && files.length > 0) {
            for (File file : files) file(file, key, null);
        }
        return (Returner) this;

    }

    @Override
    public Returner file(@Nullable String key, @Nullable List<File> files) {
        if (!StringUtil.isEmpty(key) && files != null && !files.isEmpty()) {
            for (File file : files) file(file, key, null);
        }
        return (Returner) this;
    }

    @Override
    public Returner file(@Nullable String key, @Nullable List<File> files, @Nullable String encode) {
        if (!StringUtil.isEmpty(key) && files != null && !files.isEmpty()) {
            for (File file : files) file(file, key, encode);
        }
        return (Returner) this;
    }

    @Override
    public Returner removeFile(@Nullable String key) {
        if (!StringUtil.isEmpty(key)) {
            final Iterator<FileBody> iterator = mFileBodyList.isEmpty() ? null : mFileBodyList.iterator();
            while (iterator != null && iterator.hasNext()) {
                final FileBody formBody = iterator.next();
                if (StringUtil.equals(key, formBody.getKey())) iterator.remove();
            }
        }
        return (Returner) this;
    }

    @Override
    public Returner removeFileAll() {
        mRequestBodyMap.clear();
        return (Returner) this;
    }

    @Override
    public Returner body(@Nullable RequestBody requestBody) {
        this.mRequestBody = requestBody;
        return (Returner) this;
    }

    @Override
    public Returner body(@Nullable String contentType, @Nullable IRequestBody requestBody) {
        if (!StringUtil.isEmpty(contentType)) {
            mRequestBodyMap.put(contentType, requestBody);
        }
        return (Returner) this;
    }

    @Override
    public Returner splice(boolean url) {
        this.mSpliceUrl = url;
        return (Returner) this;
    }

    @Override
    public Returner multipart(boolean body) {
        this.mMultipartBody = body;
        return (Returner) this;
    }

    @Override
    public Returner setProgress(@Nullable IProgress progress) {
        this.mProgress = progress;
        return (Returner) this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 辅助方法
    ///////////////////////////////////////////////////////////////////////////
    private boolean isMultipart() {
        return mMultipartBody || (mFileBodyList != null && mFileBodyList.size() > 0);
    }

    @NonNull
    private RequestBody getSingleRequestBody(@NonNull String encode) {
        //不为空 直接返回
        if (mRequestBody != null) return mRequestBody;
        //参数
        mRequestBody = mParams.build().toRequestBody(encode);
        if (mRequestBody != null) return mRequestBody;
        //content json xml bytes
        mRequestBody = getContentBody(getContentType(), encode);
        if (mRequestBody == null) mRequestBody = new FormBody.Builder().build();
        return mRequestBody;
    }

    @NonNull
    private RequestBody getMultipartBody(@NonNull String encode) {
        final MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //自定义RequestBody
        if (mRequestBody != null) builder.addPart(mRequestBody);

        //Params
        final RequestBody params = mParams.build().toRequestBody(encode);
        if (params != null) builder.addPart(params);

        //content json xml bytes
        if (mRequestBodyMap.size() > 0) {
            for (Map.Entry<String, IRequestBody> entry : mRequestBodyMap.entrySet()) {
                builder.addPart(entry.getValue().getRequestBody(encode));
            }
        }
        //File
        if (mFileBodyList != null && mFileBodyList.size() > 0) {
            for (final FileBody fileBody : mFileBodyList) {
                builder.addFormDataPart(fileBody.getKey(), fileBody.getFileName(), fileBody.getRequestBody(encode));
            }
        }
        return builder.build();
    }

    private RequestBody getContentBody(@NonNull String contentType, String encode) {
        if (mRequestBodyMap.size() == 0) return null;
        RequestBody requestBody = null;
        final String type = contentType.toLowerCase(Locale.ENGLISH);
        //指定json
        if (type.equals(OkHttp.CONTENT_TYPE_JSON) || type.contains(OkHttp.CONTENT_TYPE_JSON)) {
            IRequestBody body = getContentRequestBody(OkHttp.CONTENT_TYPE_JSON);
            if (body != null) return body.getRequestBody(encode);
        }
        //指定xml
        if (type.equals(OkHttp.CONTENT_TYPE_XML) || type.contains(OkHttp.CONTENT_TYPE_XML)) {
            IRequestBody body = getContentRequestBody(OkHttp.CONTENT_TYPE_XML);
            if (body != null) return body.getRequestBody(encode);

        }
        //指定字节流
        if (type.equals(OkHttp.CONTENT_TYPE_STREAM) || type.contains(OkHttp.CONTENT_TYPE_STREAM)) {
            IRequestBody body = getContentRequestBody(OkHttp.CONTENT_TYPE_STREAM);
            if (body != null) return body.getRequestBody(encode);

        }
        //没有指定
        if (type.equals(OkHttp.CONTENT_TYPE_DEFAULT) || type.contains(OkHttp.CONTENT_TYPE_DEFAULT)) {
            Iterator<Map.Entry<String, IRequestBody>> iterator = mRequestBodyMap.entrySet().iterator();
            IRequestBody body = null;
            while (iterator.hasNext()) body = iterator.next().getValue();
            requestBody = null == body ? null : body.getRequestBody(encode);
            return requestBody;
        }
        return null;
    }

    @Nullable
    private IRequestBody getContentRequestBody(@NonNull String contentType) {
        for (Map.Entry<String, IRequestBody> entry : mRequestBodyMap.entrySet()) {
            final String key = entry.getKey().toLowerCase(Locale.ENGLISH);
            if (contentType.equals(key) || contentType.contains(key) || key.contains(contentType)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
