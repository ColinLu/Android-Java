package com.colin.library.android.widgets.web.bridge;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.encrypt.EncodeUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * 作者： ColinLu
 * 时间： 2020-02-16 22:24
 * <p>
 * 描述： BridgeUtil
 */
public final class BridgeUtil {

    /*格式为   colin://return/{function}/returncontent*/
    private final static String COLIN_OVERRIDE_SCHEMA = "colin://";
    private final static String COLIN_RETURN_DATA = COLIN_OVERRIDE_SCHEMA + "return/";
    private final static String COLIN_FETCH_QUEUE = COLIN_RETURN_DATA + "_fetchQueue/";
    private final static String EMPTY_STR = "";
    private final static String SPLIT_MARK = "/";
    private final static String JAVASCRIPT_STR = "javascript:";

    public final static String UNDERLINE_STR = "_";
    public final static String CALLBACK_ID_FORMAT = "JAVA_CB_%s";
    public final static String JS_HANDLE_MESSAGE_FROM_JAVA = "javascript:WebViewJavascriptBridge._handleMessageFromNative('%s');";
    public final static String JS_FETCH_QUEUE_FROM_JAVA = "javascript:WebViewJavascriptBridge._fetchQueue();";

    public final static String BRIDGE_JS_FILE = "WebViewJavascriptBridge.js";
    public final static String BRIDGE_JS_FILE_NAME = "WebViewJavascriptBridge";


    /**
     * 例子 javascript:WebViewJavascriptBridge._fetchQueue(); --> _fetchQueue
     *
     * @param jsUrl url
     * @return 返回字符串，注意获取的时候判断空
     */
    public static String parseFunctionName(String jsUrl) {
        return parseFunctionName(jsUrl, BRIDGE_JS_FILE_NAME);
    }

    /**
     * 通过URL 得到方法名
     *
     * @param jsUrl      html 网址
     * @param jsFileName 自定义js 名字 eg:WebViewJavascriptBridge
     * @return
     */
    public static String parseFunctionName(String jsUrl, String jsFileName) {
        return jsUrl.replace(JAVASCRIPT_STR + jsFileName + ".", EMPTY_STR).replaceAll("\\(.*\\);", EMPTY_STR);
    }


    /**
     * 获取到传递信息的body值
     * url = colin://return/_fetchQueue/[{"responseId":"JAVA_CB_2_3957",
     * "responseData":"Javascript Says Right back aka!"}]
     *
     * @param url url
     * @return 返回字符串，注意获取的时候判断空
     */
    public static String getDataFromReturnUrl(String url) {
        // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
        if (url.startsWith(COLIN_FETCH_QUEUE)) return url.replace(COLIN_FETCH_QUEUE, EMPTY_STR);

        // temp = _fetchQueue/[{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
        String temp = url.replace(COLIN_RETURN_DATA, EMPTY_STR);
        String[] functionAndData = temp.split(SPLIT_MARK);

        if (functionAndData.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < functionAndData.length; i++) sb.append(functionAndData[i]);
            // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
            return sb.toString();
        }
        return null;
    }

    // 获取到传递信息的方法
    // url = colin://return/_fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
    public static String getFunctionFromReturnUrl(String url) {
        // temp = _fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
        String temp = url.replace(COLIN_RETURN_DATA, EMPTY_STR);
        String[] functionAndData = temp.split(SPLIT_MARK);
        // functionAndData[0] = _fetchQueue
        if (functionAndData.length >= 1) return functionAndData[0];
        return null;
    }

    /**
     * js 文件将注入为第一个script引用
     *
     * @param view WebView
     * @param url  url
     */
    public static void webViewLoadJs(WebView view, String url) {
        String js = "var newscript = document.createElement(\"script\");";
        js += "newscript.src=\"" + url + "\";";
        js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);";
        view.loadUrl(JAVASCRIPT_STR + js);
    }

    /**
     * 这里只是加载lib包中assets中的 WebViewJavascriptBridge.js
     *
     * @param view webview
     * @param path 路径
     */
    public static void webViewLoadLocalJs(WebView view, String path) {
        String jsContent = getString(view.getContext(), path);
        view.loadUrl(JAVASCRIPT_STR + jsContent);
    }


    /**
     * 拦截地址的时候判断是否js
     *
     * @param view
     * @param url
     * @return
     */
    public static boolean loadJsBridge(@Nullable View view, @Nullable String url) {
        if (!(view instanceof WebViewJavascriptBridge) || StringUtil.isEmpty(url)) return false;
        url = EncodeUtil.decode(url, "UTF-8");
        LogUtil.e("loadJsBridge", url);
        if (StringUtil.isEmpty(url)) return false;
        if (url.startsWith(BridgeUtil.COLIN_RETURN_DATA)) { // 如果是返回数据
            ((WebViewJavascriptBridge) view).handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.COLIN_OVERRIDE_SCHEMA)) { //遍历 js消息反馈给Android 原生
            ((WebViewJavascriptBridge) view).flushMessageQueue();
            return true;
        }
        return false;
    }

    /**
     * 注入Js
     *
     * @param view
     * @param url
     */
    public static void insertJsBridge(@NonNull WebView view, String url) {
        if (!(view instanceof WebViewJavascriptBridge) || TextUtils.isEmpty(url)) return;
        //注入本地资源
        webViewLoadLocalJs(view, BridgeUtil.BRIDGE_JS_FILE);
        final WebViewJavascriptBridge webViewJavascriptBridge = (WebViewJavascriptBridge) view;
        List<BridgeMessage> messageList = webViewJavascriptBridge.getMessage();
        if (messageList != null && messageList.size() > 0) {
            for (BridgeMessage bridgeMessage : messageList) {
                //分发message 必须在主线程才分发成功
                webViewJavascriptBridge.dispatchMessage(bridgeMessage);
            }
            webViewJavascriptBridge.setMessage(null);
        }
    }

    /**
     * 解析assets文件夹里面的代码,去除注释,取可执行的代码
     *
     * @param context  context
     * @param fileName 路径
     * @return 可执行代码
     */
    @Nullable
    private static String getString(@NonNull Context context, @NonNull String fileName) {
        InputStream in = null;
        BufferedReader reader = null;
        try {
            in = context.getAssets().open(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            final StringBuilder sb = new StringBuilder();
            do {
                line = reader.readLine();
                // 去除注释
                if (line != null && !line.matches("^\\s*\\/\\/.*")) sb.append(line);
            } while (line != null);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(reader, in);
        }
        return null;
    }


}
