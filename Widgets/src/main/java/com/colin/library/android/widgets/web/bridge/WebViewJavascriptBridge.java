package com.colin.library.android.widgets.web.bridge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2020-02-16 22:19
 * <p>
 * 描述： Js 接口回调逻辑处理
 */
public interface WebViewJavascriptBridge {
    /*注册js 方法 单个*/
    void registerHandler(@NonNull String handlerName, @NonNull BridgeHandler handler);

    /*注册js 方法 集合*/
    void registerHandler(@NonNull List<String> handlerNameList, @NonNull BridgeHandler handler);

    void callHandler(@NonNull String handlerName, @Nullable String data, @Nullable CallBackFunction callBack);

    /*解绑 js方法*/
    void unregisterHandler(@Nullable String handlerName);

    /*发送信息 给js*/
    void send(@Nullable String data);

    /*发送信息 给js*/
    void send(@Nullable String data, @Nullable CallBackFunction responseCallback);

    /*轮训 获取 js 返回内容*/
    void handlerReturnData(@NonNull String url);

    /*遍历 js消息反馈给Android 原生*/
    void flushMessageQueue();

    /*分发message 必须在主线程才分发成功*/
    void dispatchMessage(@NonNull BridgeMessage bridgeMessage);

    @Nullable
    List<BridgeMessage> getMessage();

    void setMessage(@Nullable List<BridgeMessage> list);
}
