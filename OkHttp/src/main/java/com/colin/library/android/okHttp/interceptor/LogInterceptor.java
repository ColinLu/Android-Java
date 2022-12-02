//package com.colin.library.android.okHttp.interceptor;
//
//import android.text.TextUtils;
//
//import com.colin.library.android.okHttp.BuildConfig;
//import com.colin.library.android.okHttp.request.ProgressRequestBody;
//import com.colin.library.android.utils.HttpUtil;
//import com.colin.library.android.utils.LogUtil;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.IOException;
//import java.nio.charset.Charset;
//import java.util.List;
//import java.util.Map;
//
//import okhttp3.FormBody;
//import okhttp3.Headers;
//import okhttp3.Interceptor;
//import okhttp3.MediaType;
//import okhttp3.Protocol;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//import okio.Buffer;
//import okio.BufferedSource;
//
///**
// * 作者： ColinLu
// * 时间： 2021-06-13 13:11
// * <p>
// * 描述： 日志打印工具类
// * addNetworkInterceptor
// * 在给OkhttpClient添加网络请求拦截器的时候需要注意，应该调用方法addNetworkInterceptor，而不是addInterceptor。
// * 因为有时候可能会通过cookieJar在header里面去添加一些持久化的cookie或者session信息。
// * 这样就在请求头里面就不会打印出这些信息
// * addNetInterceptor是添加网络拦截器，addInterceptor是添加应用拦截器，
// * 如果看到okhttp的流程分析的知道：应用拦截器是在网络拦截器前执行的。
// */
//public final class LogInterceptor implements Interceptor {
//    private static final String LABEL = "-----------------";
//    private static final int KEY_LENGTH = 20;
//    private static final String SPACE = " ";
//    private boolean mDebug = BuildConfig.DEBUG;
//
//    public LogInterceptor() {
//        this(true);
//    }
//
//    public LogInterceptor(boolean debug) {
//        this.mDebug = debug;
//    }
//
//    @NotNull
//    @Override
//    public Response intercept(@NotNull Chain chain) throws IOException {
//        Response response = null;
//        long startTime = System.currentTimeMillis();
//        final StringBuffer sb = new StringBuffer();
//        try {
//            sb.append(LABEL).append("Start:").append(startTime).append(LABEL).append('\n');
//            Request request = chain.request();
//            sb.append("url").append(space("url")).append(":").append(request.url().toString()).append('\n').append("method").append(space("method")).append(":").append(request.method()).append('\n').append("isHttps").append(space("isHttps")).append(":").append(request.isHttps()).append('\n').append("Protocol").append(space("Protocol")).append(":").append(getProtocol(chain)).append('\n');
//
//            sb.append(LABEL).append("Header").append(LABEL).append('\n');
//            Headers headers = request.headers().newBuilder().build();
//            Map<String, List<String>> headMap = headers.toMultimap();
//            for (Map.Entry<String, List<String>> stringListEntry : headMap.entrySet()) {
//                List<String> list = stringListEntry.getValue();
//                String key = stringListEntry.getKey();
//                for (String value : list) {
//                    sb.append(key).append(space(key)).append(":").append(HttpUtil.decode(value)).append('\n');
//                }
//            }
//            sb.append(LABEL).append("Header").append(LABEL).append('\n');
//            sb.append(LABEL).append("Body").append(LABEL).append('\n');
//            RequestBody requestBody = request.body();
//            FormBody formBody = getFormBody(requestBody);
//            if (null == formBody) sb.append("No RequestBody").append('\n');
//            else {
//                for (int i = 0; i < formBody.size(); i++) {
//                    final String name = formBody.name(i);
//                    sb.append(name).append(space(name)).append(":").append(formBody.value(i)).append('\n');
//                }
//            }
//            sb.append(LABEL).append("Body").append(LABEL).append('\n');
//            sb.append(LABEL).append("Result").append(LABEL).append('\n');
//            response = chain.proceed(chain.request());
//            Response clone = response.newBuilder().build();
//            sb.append("code      :").append(clone.code()).append('\t').append("message   :").append(clone.message()).append('\n');
//            ResponseBody body = clone.body();
//            if (clone.isSuccessful() && null != body && isPlaintext(body.contentType())) {
//                if (!isPlaintext(body.contentType())) sb.append("not text").append('\n');
//                else {
//                    BufferedSource source = clone.body().source();
//                    source.request(Long.MAX_VALUE); // request the entire body.
//                    Buffer buffer = source.getBuffer();
//                    String readString = buffer.clone().readString(Charset.defaultCharset());
//                    if (TextUtils.isEmpty(readString)) sb.append("parse fail").append('\n');
//                    else if ((readString.startsWith("[") && readString.endsWith("]")) || (readString.startsWith("{") && readString.endsWith("}")))
//                        sb.append(LogUtil.formatJson(readString)).append('\n');
//                    else if (readString.startsWith("<?xml"))
//                        sb.append(LogUtil.formatXml(readString)).append('\n');
//                    else sb.append(readString).append('\n');
//                }
//            }
//            sb.append(LABEL).append("Result").append(LABEL).append('\n');
//        } catch (Throwable e) {
//            sb.append(LABEL).append("Exception").append(LABEL).append('\n');
//            sb.append(e.toString());
//            sb.append(LABEL).append("Exception").append(LABEL).append('\n');
//            throw new IOException(e);
//        } finally {
//            long endTime = System.currentTimeMillis();
//            long duration = endTime - startTime;
//            sb.append(LABEL).append("Duration:").append(duration).append(LABEL).append('\n');
//            sb.append(LABEL).append("End:").append(endTime).append(LABEL).append('\n');
//            LogUtil.e(sb);
//        }
//        return response;
//    }
//
//    private FormBody getFormBody(RequestBody requestBody) {
//        if (null == requestBody) return null;
//        if (requestBody instanceof FormBody) return (FormBody) requestBody;
//        if (requestBody instanceof ProgressRequestBody) {
//            RequestBody realRequestBody = ((ProgressRequestBody) requestBody).getRealRequestBody();
//            if (realRequestBody instanceof FormBody) return (FormBody) realRequestBody;
//        }
//        return null;
//    }
//
//    private String space(String key) {
//        StringBuilder stringBuilder = new StringBuilder();
//        int length = null == key ? KEY_LENGTH : KEY_LENGTH - key.length();
//        for (int i = 0; i < length; i++) stringBuilder.append(SPACE);
//        return stringBuilder.toString();
//    }
//
//    private String getProtocol(Chain chain) {
//        return null == chain || null == chain.connection() || null == chain.connection().protocol() ? Protocol.HTTP_1_1.toString() : chain.connection().protocol().toString();
//    }
//
//
//    /**
//     * Returns true if the body in question probably contains human readable text. Uses a small sample
//     * of code points to detect unicode control characters commonly used in binary file signatures.
//     */
//    private static boolean isPlaintext(MediaType mediaType) {
//        if (mediaType == null) return false;
//        if (mediaType.type() != null && mediaType.type().equals("text")) {
//            return true;
//        }
//        String subtype = mediaType.subtype();
//        if (subtype != null) {
//            subtype = subtype.toLowerCase();
//            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
//                return true;
//        }
//        return false;
//    }
//
//}
