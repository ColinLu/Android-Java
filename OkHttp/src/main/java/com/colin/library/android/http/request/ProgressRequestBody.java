package com.colin.library.android.http.request;


import androidx.annotation.NonNull;

import com.colin.library.android.helper.ThreadHelper;
import com.colin.library.android.http.progress.IProgress;
import com.colin.library.android.utils.LogUtil;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 作者： ColinLu
 * 时间： 2020-01-01 09:15
 * <p>
 * 描述： 监听上传 进度
 */
public class ProgressRequestBody extends RequestBody {
    private final RequestBody mRequestBody;
    private final IProgress mProgress;

    public ProgressRequestBody(@NonNull RequestBody requestBody, @NonNull IProgress progress) {
        this.mProgress = progress;
        this.mRequestBody = requestBody;
    }


    public RequestBody getRealRequestBody() {
        return mRequestBody;
    }

    /*重写调用实际的响应体的contentType*/
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    /*重写调用实际的响应体的contentLength*/
    @Override
    public long contentLength() {
        try {
            return mRequestBody.contentLength();
        } catch (IOException e) {
            LogUtil.log(e);
            return -1;
        }
    }

    /*重写进行写入 */
    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink, contentLength());
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        mRequestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }


    /**
     * 包装
     */
    private final class CountingSink extends ForwardingSink {
        private long mCurrentSize = 0L;
        private final long mTotal;

        CountingSink(Sink sink, long total) {
            super(sink);
            this.mTotal = total;
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            mCurrentSize += byteCount;
            callback(mTotal, mCurrentSize);
        }
    }

    private void callback(final long total, final long currentSize) {
        ThreadHelper.getInstance().post(() -> mProgress.progress(total, currentSize));
    }
}
