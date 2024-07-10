package com.colin.library.android.http.request.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.def.Constants;
import com.colin.library.android.http.progress.IProgress;
import com.colin.library.android.http.progress.ProgressRequestBody;
import com.colin.library.android.http.request.body.ByteBody;
import com.colin.library.android.http.request.body.ContentBody;
import com.colin.library.android.http.request.body.FileBody;
import com.colin.library.android.http.request.body.IRequestBody;
import com.colin.library.android.utils.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    protected transient boolean mMultipartBody;             //多表单
    protected transient RequestBody mRequestBody;           //请求体
    protected transient final HashMap<String, IRequestBody> mRequestBodyMap;
    protected transient final List<FileBody> mFileBodyList;

    public BaseRequestBody(@NonNull String url, @NonNull @Method String method) {
        super(url, method);
        this.mRequestBodyMap = new HashMap<>();
        this.mFileBodyList = new ArrayList<>();
    }

    @Override
    public Returner multipart(boolean body) {
        this.mMultipartBody = body;
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
        if (files != null) {
            for (File file : files) {
                mFileBodyList.add(new FileBody(file, key, charset));
            }
        }
        return (Returner) this;
    }

    @Override
    public Returner file(@Nullable String key, @Nullable String charset, @Nullable List<File> list) {
        if (list != null && !list.isEmpty()) {
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

    ///////////////////////////////////////////////////////////////////////////
    // 辅助方法
    ///////////////////////////////////////////////////////////////////////////
    private boolean isMultipart() {
        return mMultipartBody || (mFileBodyList != null && mFileBodyList.size() > 0);
    }

    @NonNull
    private RequestBody getSingleRequestBody(@Nullable String encode) {
        //自定义RequestBody 不为空 直接返回
        if (mRequestBody != null) return mRequestBody;
        //content json xml bytes
        mRequestBody = getContentBody(getContentType(), encode);
        //Params
        mRequestBody = mParams.build().toRequestBody(encode);
        if (mRequestBody != null) return mRequestBody;
        return new FormBody.Builder().build();
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
        if (!mRequestBodyMap.isEmpty()) {
            for (Map.Entry<String, IRequestBody> entry : mRequestBodyMap.entrySet()) {
                builder.addPart(entry.getValue().toRequestBody(encode));
            }
        }
        //File
        if (mFileBodyList != null && !mFileBodyList.isEmpty()) {
            for (final FileBody fileBody : mFileBodyList) {
                builder.addFormDataPart(fileBody.getKey(), fileBody.getFileName(), fileBody.toRequestBody(encode));
            }
        }
        return builder.build();
    }


    @Nullable
    private RequestBody getContentBody(@NonNull String contentType, String encode) {
        if (mRequestBodyMap.isEmpty()) return null;
        IRequestBody body = null;
        //指定字节流
        body = getContentRequestBody(contentType, Constants.CONTENT_TYPE_STREAM);
        if (body != null) return body.toRequestBody(encode);
        //指定json
        body = getContentRequestBody(contentType, Constants.CONTENT_TYPE_JSON);
        if (body != null) return body.toRequestBody(encode);
        //指定xml
        body = getContentRequestBody(contentType, Constants.CONTENT_TYPE_XML);
        if (body != null) return body.toRequestBody(encode);
        //没有指定
        for (Map.Entry<String, IRequestBody> entry : mRequestBodyMap.entrySet()) {
            return entry.getValue().toRequestBody(encode);
        }
        return null;
    }

    @Nullable
    private IRequestBody getContentRequestBody(@NonNull String contentType, @NonNull String type) {
        if (contentType.equalsIgnoreCase(type) || contentType.contains(type)) {
            for (Map.Entry<String, IRequestBody> entry : mRequestBodyMap.entrySet()) {
                final String key = entry.getKey();
                if (type.equalsIgnoreCase(key) || type.contains(key) || key.contains(type)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

}
