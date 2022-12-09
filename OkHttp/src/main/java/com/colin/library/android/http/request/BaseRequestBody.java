package com.colin.library.android.http.request;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.OkHttp;
import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.bean.ByteBody;
import com.colin.library.android.http.bean.ContentBody;
import com.colin.library.android.http.bean.FileBody;
import com.colin.library.android.http.bean.IRequestBody;
import com.colin.library.android.http.progress.IProgress;
import com.colin.library.android.utils.StringUtil;

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
 * 描述： 请求体基础类
 */
public class BaseRequestBody<Returner> extends BaseRequest<Returner> implements IBody<Returner> {
    protected transient boolean mSpliceUrl;                 //拼接地址
    protected transient boolean mMultipartBody;             //多表单
    protected transient RequestBody mRequestBody;           //请求体
    protected transient final HashMap<String, IRequestBody> mRequestBodyMap;
    protected transient final List<FileBody> mFileBodyList;
    protected transient IProgress mProgress;

    public BaseRequestBody(@NonNull String url, @NonNull @Method String method) {
        super(url, method);
        this.mRequestBodyMap = new HashMap<>();
        this.mFileBodyList = new ArrayList<>();
    }

    @NonNull
    @Override
    public String getUrl() {
        return mSpliceUrl ? super.getUrl() : mUrl;
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
        return content(json, OkHttp.CONTENT_TYPE_JSON, OkHttp.ENCODE_DEFAULT);
    }

    @Override
    public Returner json(@Nullable String json, @NonNull String charset) {
        return content(json, OkHttp.CONTENT_TYPE_JSON, charset);
    }

    @Override
    public Returner json(@Nullable JSONObject json) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, OkHttp.ENCODE_DEFAULT);
    }

    @Override
    public Returner json(@Nullable JSONObject json, @NonNull String charset) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, charset);
    }

    @Override
    public Returner json(@Nullable JSONArray json) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, OkHttp.ENCODE_DEFAULT);
    }

    @Override
    public Returner json(@Nullable JSONArray json, @NonNull String charset) {
        return content(null == json ? null : json.toString(), OkHttp.CONTENT_TYPE_JSON, charset);
    }

    @Override
    public Returner xml(@Nullable String xml) {
        return content(xml, OkHttp.CONTENT_TYPE_XML, OkHttp.ENCODE_DEFAULT);
    }

    @Override
    public Returner xml(@Nullable String xml, @NonNull String charset) {
        return content(xml, OkHttp.CONTENT_TYPE_XML, charset);
    }

    @Override
    public Returner content(@Nullable String content) {
        return content(content, OkHttp.CONTENT_TYPE_DEFAULT, OkHttp.ENCODE_DEFAULT);
    }

    @Override
    public Returner content(@Nullable String content, @NonNull String contentType) {
        return content(content, OkHttp.CONTENT_TYPE_DEFAULT, OkHttp.ENCODE_DEFAULT);
    }

    @Override
    public Returner content(@Nullable String content, @NonNull String contentType, @NonNull String charset) {
        if (!TextUtils.isEmpty(content)) {
            mRequestBodyMap.put(contentType, new ContentBody(content, contentType, charset));
        }
        return (Returner) this;
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes) {
        return bytes(bytes, OkHttp.CONTENT_TYPE_STREAM, OkHttp.ENCODE_DEFAULT, 0, null == bytes ? 0 : bytes.length);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @NonNull String charset) {
        return bytes(bytes, OkHttp.CONTENT_TYPE_STREAM, charset, 0, null == bytes ? 0 : bytes.length);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, int offset, int count) {
        return bytes(bytes, OkHttp.CONTENT_TYPE_STREAM, OkHttp.ENCODE_DEFAULT, offset, count);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @NonNull String contentType, int offset, int count) {
        return bytes(bytes, contentType, OkHttp.ENCODE_DEFAULT, offset, count);

    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @NonNull String contentType, @NonNull String charset, int offset, int count) {
        if (bytes != null && bytes.length >= count && count > offset) {
            mRequestBodyMap.put(contentType, new ByteBody(bytes, contentType, charset, offset, count));
        }
        return (Returner) this;
    }

    @Override
    public Returner file(@Nullable File... files) {
        return file(null, null, files);
    }


    @Override
    public Returner file(@Nullable List<File> files) {
        return file(null, null, files);
    }


    @Override
    public Returner file(@Nullable String key, @Nullable File... files) {
        return file(key, null, files);
    }

    @Override
    public Returner file(@Nullable String key, @Nullable List<File> files) {
        return file(key, null, files);
    }

    @Override
    public Returner file(@Nullable String key, @Nullable String charset, @Nullable File... files) {
        if (files != null && files.length > 0) {
            for (File file : files) {
                mFileBodyList.add(new FileBody(file, key, charset));
            }
        }
        return (Returner) this;
    }

    @Override
    public Returner file(@Nullable String key, @Nullable String charset, @Nullable List<File> list) {
        if (list != null && list.size() > 0) {
            for (File file : list) {
                mFileBodyList.add(new FileBody(file, key, charset));
            }
        }
        return (Returner) this;
    }


    @Override
    public Returner removeFile(@Nullable String key) {
        if (!TextUtils.isEmpty(key)) {
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
                builder.addPart(entry.getValue().getRequestBody());
            }
        }
        //File
        if (mFileBodyList != null && mFileBodyList.size() > 0) {
            for (final FileBody fileBody : mFileBodyList) {
                builder.addFormDataPart(fileBody.getKey(), fileBody.getFileName(), fileBody.getRequestBody());
            }
        }
        return builder.build();
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

    @Nullable
    private RequestBody getContentBody(@NonNull String contentType, String encode) {
        if (mRequestBodyMap.size() == 0) return null;
        RequestBody requestBody = null;
        final String type = contentType.toLowerCase(Locale.ENGLISH);
        //指定json
        if (type.equals(OkHttp.CONTENT_TYPE_JSON) || type.contains(OkHttp.CONTENT_TYPE_JSON)) {
            IRequestBody body = getContentRequestBody(OkHttp.CONTENT_TYPE_JSON);
            if (body != null) return body.getRequestBody();
        }
        //指定xml
        if (type.equals(OkHttp.CONTENT_TYPE_XML) || type.contains(OkHttp.CONTENT_TYPE_XML)) {
            IRequestBody body = getContentRequestBody(OkHttp.CONTENT_TYPE_XML);
            if (body != null) return body.getRequestBody();

        }
        //指定字节流
        if (type.equals(OkHttp.CONTENT_TYPE_STREAM) || type.contains(OkHttp.CONTENT_TYPE_STREAM)) {
            IRequestBody body = getContentRequestBody(OkHttp.CONTENT_TYPE_STREAM);
            if (body != null) return body.getRequestBody();

        }
        //没有指定
        if (type.equals(OkHttp.CONTENT_TYPE_DEFAULT) || type.contains(OkHttp.CONTENT_TYPE_DEFAULT)) {
            Iterator<Map.Entry<String, IRequestBody>> iterator = mRequestBodyMap.entrySet().iterator();
            IRequestBody body = null;
            while (iterator.hasNext()) body = iterator.next().getValue();
            requestBody = null == body ? null : body.getRequestBody();
            return requestBody;
        }
        return null;
    }


    @Nullable
    private IRequestBody getContentRequestBody(@NonNull String contentType) {
        for (Map.Entry<String, IRequestBody> entry : mRequestBodyMap.entrySet()) {
            final String key = entry.getKey();
            if (key.equals(contentType) || key.contains(contentType) || contentType.contains(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
