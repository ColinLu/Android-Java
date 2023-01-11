package com.colin.library.android.widgets.web.bridge;


import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：Bridge消息信息数据格式
 * <p>
 * 作者：Colin
 * 时间：2018/8/15
 */
public class BridgeMessage {

    private String handlerName;     //消息名称
    private String callbackId;      //回调id
    private String responseId;      //响应id
    private String responseData;    //响应内容
    private String data;            //消息内容

    private final static String HANDLER_NAME_STR = "handlerName";
    private final static String CALLBACK_ID_STR = "callbackId";
    private final static String RESPONSE_ID_STR = "responseId";
    private final static String RESPONSE_DATA_STR = "responseData";
    public final static String DATA_STR = "data";

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String toJson() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(HANDLER_NAME_STR, getHandlerName());
            jsonObject.put(CALLBACK_ID_STR, getCallbackId());
            jsonObject.put(RESPONSE_ID_STR, getResponseId());
            jsonObject.put(RESPONSE_DATA_STR, getResponseData());
            jsonObject.put(DATA_STR, getData());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数据格式转换  json 字符串 消息类
     *
     * @param jsonStr
     * @return
     */
    public static BridgeMessage toObject(String jsonStr) {
        final BridgeMessage message = new BridgeMessage();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            message.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject.getString(HANDLER_NAME_STR) : null);
            message.setCallbackId(jsonObject.has(CALLBACK_ID_STR) ? jsonObject.getString(CALLBACK_ID_STR) : null);
            message.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject.getString(RESPONSE_ID_STR) : null);
            message.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject.getString(RESPONSE_DATA_STR) : null);
            message.setData(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR) : null);
            return message;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 数据格式转换  json 字符串 转集合
     *
     * @param jsonStr
     * @return
     */
    @NonNull
    public static List<BridgeMessage> toArrayList(String jsonStr) {
        final List<BridgeMessage> list = new ArrayList<>();
        try {
            final JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                BridgeMessage message = new BridgeMessage();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                message.setHandlerName(jsonObject.has(HANDLER_NAME_STR) ? jsonObject.getString(HANDLER_NAME_STR) : null);
                message.setCallbackId(jsonObject.has(CALLBACK_ID_STR) ? jsonObject.getString(CALLBACK_ID_STR) : null);
                message.setResponseData(jsonObject.has(RESPONSE_DATA_STR) ? jsonObject.getString(RESPONSE_DATA_STR) : null);
                message.setResponseId(jsonObject.has(RESPONSE_ID_STR) ? jsonObject.getString(RESPONSE_ID_STR) : null);
                message.setData(jsonObject.has(DATA_STR) ? jsonObject.getString(DATA_STR) : null);
                list.add(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String toString() {
        return "BridgeMessage{" +
                "handlerName='" + handlerName + '\'' +
                ", callbackId='" + callbackId + '\'' +
                ", responseId='" + responseId + '\'' +
                ", responseData='" + responseData + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
