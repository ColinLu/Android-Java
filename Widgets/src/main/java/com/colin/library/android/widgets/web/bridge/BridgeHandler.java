package com.colin.library.android.widgets.web.bridge;

/**
 * 描述：Bridge接口回调
 * <p>
 * 作者：Colin
 * 时间：2018/8/15
 */
public interface BridgeHandler {

    void handler(String handlerName, String data, CallBackFunction function);

    //默认
    BridgeHandler DEFAULT_HANDLER = (handlerName, data, function) -> {
        if (function != null) function.onCallBack("DefaultHandler response data");
    };
}
