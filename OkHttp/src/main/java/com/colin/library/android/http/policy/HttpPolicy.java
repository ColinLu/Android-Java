package com.colin.library.android.http.policy;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.helper.ThreadHelper;
import com.colin.library.android.http.def.HttpException;
import com.colin.library.android.http.action.IAction;
import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.LogUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者： ColinLu
 * 时间： 2021-09-10 22:46
 * <p>
 * 描述： 网络接口回调操作
 */
public class HttpPolicy implements IPolicy {
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
            if (response != null) IOUtil.close(response);
        }
        return response;
    }

    @Override
    public <Result> void execute(@NonNull IAction<Result> action) {
        ThreadHelper.getInstance().post(() -> {
            try {
                //开始
                action.start(mRequest);
                //检查状态
                checkState();
                //开始请求服务器
                request(action);
            } catch (Throwable e) {
                fail(action, new HttpException(HttpException.CODE_HTTP_STATE, mRequest.toString(), e));
            }
        });
    }

    public boolean isExecuted() {
        return mExecuted;
    }


    public void cancel() {
        mCanceled = true;
        if (null != mCall) mCall.cancel();
    }

    public boolean isCanceled() {
        if (mCanceled) return true;
        synchronized (this) {
            return mCall != null && mCall.isCanceled();
        }
    }

    @Override
    public <Result> void fail(@NonNull IAction<Result> action, @NonNull Throwable e) {
        ThreadHelper.getInstance().post(() -> {
            action.fail(e);
            action.finish(mRequest);
        });
    }

    public <Result> void success(@NonNull IAction<Result> action, @Nullable Result result) {
        ThreadHelper.getInstance().post(() -> {
            action.success(result);
            action.finish(mRequest);
        });
    }

    private void checkState() throws Throwable {
        if (mExecuted) throw new Throwable("Already executed!");
        mCall = mHttpClient.newCall(mRequest);
        mExecuted = true;
        if (mCanceled) mCall.cancel();
    }


    private <Result> void request(final IAction<Result> action) {
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
                    request(action);
                } else {
                    fail(action, new HttpException(HttpException.CODE_HTTP_FAIL, mRequest.toString(), e));
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
                    if (!response.isSuccessful() || code == 404 || code >= 500) {
                        fail(action, new HttpException(code, mRequest.toString(), response.message()));
                        return;
                    }
                    //解析操作
                    final Result result = action.parse(response);
                    success(action, result);
                } catch (Throwable e) {
                    fail(action, new HttpException(code, mRequest.toString(), e));
                } finally {
                    IOUtil.close(responseBody, response);
                }
            }
        });
    }
}
