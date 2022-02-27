package com.colin.library.android.okHttp.policy;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.okHttp.bean.HttpException;
import com.colin.library.android.okHttp.callback.IHttpCallback;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.thread.ThreadUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;

/**
 * 作者： ColinLu
 * 时间： 2021-09-10 22:46
 * <p>
 * 描述： 网络接口回调操作
 */
public class HttpPolicy<Result> implements IPolicy<Result> {
    private final OkHttpClient mHttpClient;
    private final Request mRequest;
    private volatile Call mCall;
    private int mRetryCall;
    private volatile boolean mExecuted;
    private volatile boolean mCanceled;


    public HttpPolicy(@NonNull OkHttpClient client, @NonNull Request request, int retryCall) {
        this.mHttpClient = client;
        this.mRequest = request;
        this.mRetryCall = retryCall;
    }

    @Nullable
    @Override
    public synchronized Response execute() {
        Response response = null;
        try {
            checkState();
            response = mCall.execute();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (response != null) Util.closeQuietly(response);
        }
        return response;
    }

    @Override
    public void execute(@NonNull IHttpCallback<Result> callback) {
        ThreadUtil.runUI(() -> {
            try {
                //开始
                callback.start(mRequest);
                //检查状态
                checkState();
                //开始请求服务器
                request(callback);
            } catch (Throwable e) {
                fail(callback, new HttpException(HttpException.CODE_HTTP_STATE, mRequest.url().toString(), e));
            }
        });
    }

    @Override
    public boolean isExecuted() {
        return mExecuted;
    }

    @Override
    public boolean isCanceled() {
        if (mCanceled) return true;
        synchronized (this) {
            return mCall != null && mCall.isCanceled();
        }
    }

    @Override
    public void cancel() {
        mCanceled = true;
        if (null != mCall) mCall.cancel();
    }

    @Override
    public void success(@NonNull IHttpCallback<Result> callback, @Nullable Result result) {
        ThreadUtil.runUI(() -> {
            callback.success(result);
            callback.finish(true);
        });
    }

    @Override
    public void fail(@NonNull IHttpCallback<Result> callback, @NonNull HttpException exception) {
        ThreadUtil.runUI(() -> {
            callback.fail(exception);
            callback.finish(false);
        });
    }

    private void checkState() throws Throwable {
        if (mExecuted) throw new Throwable("Already executed!");
        mCall = mHttpClient.newCall(mRequest);
        mExecuted = true;
        if (mCanceled) mCall.cancel();
    }


    private void request(final IHttpCallback<Result> callback) {
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (isCanceled() || call.isCanceled()) {
                    LogUtil.d("isCanceled");
                    return;
                }
                if (e instanceof SocketTimeoutException && mRetryCall > 0) {
                    mRetryCall--;
                    mCall = mHttpClient.newCall(mRequest);
                    request(callback);
                } else {
                    fail(callback, new HttpException(HttpException.CODE_HTTP_FAIL, mRequest.url().toString(), e));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final int code = response.code();
                ResponseBody responseBody = null;
                try {
                    //操作取消
                    if (isCanceled() || call.isCanceled()) {
                        LogUtil.e("isCanceled");
                        return;
                    }
                    responseBody = response.body();
                    //服务器问题 请求失败  或者没有请求体
                    if (!response.isSuccessful() || null == responseBody || code == 404 || code >= 500) {
                        fail(callback, new HttpException(code, mRequest.url().toString(), response.message()));
                        return;
                    }
                    //解析操作
                    final Result result = callback.parse(response);
                    success(callback, result);
                } catch (Throwable e) {
                    fail(callback, new HttpException(code, mRequest.url().toString(), e));
                } finally {
                    if (responseBody != null) Util.closeQuietly(responseBody);
                    Util.closeQuietly(response);
                }
            }
        });
    }


}
