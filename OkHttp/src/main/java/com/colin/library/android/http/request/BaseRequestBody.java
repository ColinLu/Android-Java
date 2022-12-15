package com.colin.library.android.http.request;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.annotation.Encode;
import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.bean.ByteBody;
import com.colin.library.android.http.bean.Constants;
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
        if (isMultipart()) return Constants.CONTENT_TYPE_MULTIPART;
        else return super.getContentType();
    }

    @NonNull
    @Override
    public RequestBody getRequestBody(@Nullable IProgress progress) {
        final RequestBody requestBody = isMultipart() ? getMultipartBody(getEncode()) : getSingleRequestBody(getEncode());
        return progress != null ? new ProgressRequestBody(requestBody, progress) : requestBody;
    }

    @Override
    public Returner json(@Nullable String json) {
        return content(json, Constants.CONTENT_TYPE_JSON, null);
    }

    @Override
    public Returner json(@Nullable String json, @Nullable String charset) {
        return content(json, Constants.CONTENT_TYPE_JSON, charset);
    }

    @Override
    public Returner json(@Nullable JSONObject json) {
        return content(null == json ? null : json.toString(), Constants.CONTENT_TYPE_JSON, null);
    }

    @Override
    public Returner json(@Nullable JSONObject json, @Nullable String charset) {
        return content(null == json ? null : json.toString(), Constants.CONTENT_TYPE_JSON, charset);
    }

    @Override
    public Returner json(@Nullable JSONArray json) {
        return content(null == json ? null : json.toString(), Constants.CONTENT_TYPE_JSON, null);
    }

    @Override
    public Returner json(@Nullable JSONArray json, @Nullable String charset) {
        return content(null == json ? null : json.toString(), Constants.CONTENT_TYPE_JSON, charset);
    }

    @Override
    public Returner xml(@Nullable String xml) {
        return content(xml, Constants.CONTENT_TYPE_XML, null);
    }

    @Override
    public Returner xml(@Nullable String xml, @Nullable String charset) {
        return content(xml, Constants.CONTENT_TYPE_XML, charset);
    }

    @Override
    public Returner content(@Nullable String content) {
        return content(content, Constants.CONTENT_TYPE_DEFAULT, null);
    }

    @Override
    public Returner content(@Nullable String content, @NonNull String contentType) {
        return content(content, Constants.CONTENT_TYPE_DEFAULT, null);
    }

    @Override
    public Returner content(@Nullable String content, @NonNull String contentType, @Nullable String charset) {
        if (!StringUtil.isEmpty(content)) {
            mRequestBodyMap.put(contentType, new ContentBody(content, contentType, charset));
        }
        return (Returner) this;
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes) {
        return bytes(bytes, Constants.CONTENT_TYPE_STREAM, Constants.ENCODE_DEFAULT, 0, null == bytes ? 0 : bytes.length);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @Nullable String charset) {
        return bytes(bytes, Constants.CONTENT_TYPE_STREAM, charset, 0, null == bytes ? 0 : bytes.length);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, int offset, int count) {
        return bytes(bytes, Constants.CONTENT_TYPE_STREAM, Constants.ENCODE_DEFAULT, offset, count);
    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @NonNull String contentType, int offset, int count) {
        return bytes(bytes, contentType, Constants.ENCODE_DEFAULT, offset, count);

    }

    @Override
    public Returner bytes(@Nullable byte[] bytes, @NonNull String contentType, @Nullable String charset, int offset, int count) {
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


    ///////////////////////////////////////////////////////////////////////////
    // 辅助方法
    ///////////////////////////////////////////////////////////////////////////
    private boolean isMultipart() {
        return mMultipartBody || (mFileBodyList != null && mFileBodyList.size() > 0);
    }

    @NonNull
    private RequestBody getSingleRequestBody(@Nullable String encode) {
        //不为空 直接返回
        if (mRequestBody != null) return mRequestBody;
        //参数
        mRequestBody = mParams.build().toRequestBody(encode);
        if (mRequestBody != null) return mRequestBody;
        //content json xml bytes
        mRequestBody = getContentBody(getContentType(), encode);
        if (mRequestBody == null) mRequestBody = mParams.build().newBuilder(encode).build();
        return mRequestBody;
    }

    @NonNull
    private RequestBody getMultipartBody(@Nullable String encode) {
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


    @Nullable
    private RequestBody getContentBody(@NonNull String contentType, String encode) {
        if (mRequestBodyMap.size() == 0) return null;
        RequestBody requestBody = null;
        final String type = contentType.toLowerCase(Locale.ENGLISH);
        //指定json
        if (Constants.CONTENT_TYPE_JSON.equals(type) || type.contains(Constants.CONTENT_TYPE_JSON)) {
            IRequestBody body = getContentRequestBody(Constants.CONTENT_TYPE_JSON);
            if (body != null) return body.getRequestBody(encode);
        }
        //指定xml
        if (Constants.CONTENT_TYPE_JSON.equals(type) || type.contains(Constants.CONTENT_TYPE_XML)) {
            IRequestBody body = getContentRequestBody(Constants.CONTENT_TYPE_XML);
            if (body != null) return body.getRequestBody(encode);

        }
        //指定字节流
        if (Constants.CONTENT_TYPE_JSON.equals(type) || type.contains(Constants.CONTENT_TYPE_STREAM)) {
            IRequestBody body = getContentRequestBody(Constants.CONTENT_TYPE_STREAM);
            if (body != null) return body.getRequestBody(encode);

        }
        //没有指定
        if (Constants.CONTENT_TYPE_JSON.equals(type) || type.contains(Constants.CONTENT_TYPE_DEFAULT)) {
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
            final String key = entry.getKey();
            if (contentType.equals(key) || contentType.contains(key) || key.contains(contentType)) {
                return entry.getValue();
            }
        }
        return null;
    }

}
