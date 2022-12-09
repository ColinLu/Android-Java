package com.colin.library.android.http.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.colin.library.android.http.bean.IRequestBody;
import com.colin.library.android.http.progress.IProgress;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.RequestBody;

/**
 * 作者： ColinLu
 * 时间： 2020-10-27 17:06
 * <p>
 * 描述： 请求体
 */
public interface IBody<Returner> extends IRequest<Returner> {
    @NonNull
    RequestBody getRequestBody();

    /*是否拼接网址*/
    Returner splice(boolean url);

    /*多表单提交*/
    Returner multipart(boolean body);

    /*上传Json内容*/
    Returner json(@Nullable String json);

    Returner json(@Nullable String json, @NonNull String charset);

    Returner json(@Nullable JSONObject json);

    Returner json(@Nullable JSONObject json, @NonNull String charset);

    Returner json(@Nullable @Size(min = 0) JSONArray json);

    Returner json(@Nullable @Size(min = 0) JSONArray json, @NonNull String charset);

    /*上传Xml内容*/
    Returner xml(@Nullable String xml);

    Returner xml(@Nullable String xml, @NonNull String charset);

    /*上传文本内容*/
    Returner content(@Nullable String content);

    Returner content(@Nullable String content, @NonNull String contentType);

    Returner content(@Nullable String content, @NonNull String contentType, @NonNull String charset);

    /*上传字节数组内容*/
    Returner bytes(@Nullable byte[] bytes);

    Returner bytes(@Nullable byte[] bytes, @NonNull String charset);

    Returner bytes(@Nullable byte[] bytes, int offset, int count);

    Returner bytes(@Nullable byte[] bytes, @NonNull String contentType, int offset, int count);

    Returner bytes(@Nullable byte[] bytes, @NonNull String contentType, @NonNull String charset, int offset, int count);

    /*上传文件*/
    Returner file(@Nullable File... files);

    Returner file(@Nullable List<File> list);

    Returner file(@Nullable String key, @Nullable File... files);

    Returner file(@Nullable String key, @Nullable List<File> list);

    Returner file(@Nullable String key, @Nullable String charset, @Nullable File... files);

    Returner file(@Nullable String key, @Nullable String charset, @Nullable List<File> list);

    Returner removeFile(@Nullable String key);

    Returner removeFileAll();

    Returner body(@Nullable RequestBody requestBody);

    Returner body(@Nullable String contentType, @Nullable IRequestBody requestBody);

    Returner setProgress(@Nullable IProgress progress);


}