package com.colin.library.android.okHttp.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.colin.library.android.okHttp.progress.IProgress;

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

    Returner json(@Nullable String json, @Nullable String encode);

    Returner json(@Nullable JSONObject json);

    Returner json(@Nullable JSONObject json, @Nullable String encode);

    Returner json(@Nullable @Size(min = 0) JSONArray json);

    Returner json(@Nullable @Size(min = 0) JSONArray json, @Nullable String encode);

    /*上传Xml内容*/
    Returner xml(@Nullable String xml);

    Returner xml(@Nullable String xml, @Nullable String encode);

    /*上传文本内容*/
    Returner content(@Nullable String content);

    Returner content(@Nullable String content, @Nullable String contentType);

    Returner content(@Nullable String content, @Nullable String contentType, @Nullable String encode);

    /*上传字节数组内容*/
    Returner bytes(@Nullable @Size(min = 0) byte[] bytes);

    Returner bytes(@Nullable @Size(min = 0) byte[] bytes, @Nullable String encode);

    Returner bytes(@Nullable @Size(min = 0) byte[] bytes, int offset, int count);

    Returner bytes(@Nullable @Size(min = 0) byte[] bytes, @Nullable String contentType, int offset, int count);

    Returner bytes(@Nullable @Size(min = 0) byte[] bytes, @Nullable String contentType, @Nullable String encode, int offset, int count);

    /*上传文件*/
    Returner file(@Nullable File file);

    Returner file(@Nullable File file, @Nullable String encode);

    Returner file(@Nullable File file, @Nullable String key, @Nullable String encode);

    Returner file(@Nullable List<File> files);

    Returner file(@Nullable List<File> files, @Nullable String encode);

    Returner file(@Nullable String key, @Size(min = 0) @Nullable File... files);

    Returner file(@Nullable String key, @Nullable @Size(min = 0) List<File> list);

    Returner file(@Nullable String key, @Nullable @Size(min = 0) List<File> list, @Nullable String encode);

    Returner removeFile(@Nullable String key);

    Returner removeFileAll();

    Returner body(@Nullable RequestBody requestBody);

    Returner body(@Nullable String contentType, @Nullable IRequestBody requestBody);

    Returner setProgress(@Nullable IProgress progress);


}