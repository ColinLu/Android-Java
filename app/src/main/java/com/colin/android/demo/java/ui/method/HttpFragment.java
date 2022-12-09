package com.colin.android.demo.java.ui.method;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentHttpBinding;
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.http.OkHttp;
import com.colin.library.android.http.callback.BitmapHttpCallback;
import com.colin.library.android.http.callback.StringHttpCallback;

import okhttp3.Request;

/**
 * 作者： ColinLu
 * 时间： 2022-12-08 22:49
 * <p>
 * 描述： TODO
 */
public class HttpFragment extends AppFragment<FragmentHttpBinding> {
    public static final String DOWN_APP = "http://rongxiaoliu.oss-cn-hangzhou.aliyuncs.com/fenfa/109_yinxingfenqi/81_doudoufenqi/2_4_59/doudoufenqi_2459_17538_sign.apk";
    public static final String DOWN_IMAGE = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1163419914,1588199831&fm=26&gp=0.jpg";
    public static final String DOWN_SHARE_SHARE = "http://inews.gtimg.com/newsapp_bt/0/876781763/1000";

    public static final String HTTP_BASE = "https://echo.getpostman.com/";
    public static final String HTTP_METHOD_GET = HTTP_BASE + "status/200";
    //    public static final String HTTP_METHOD_GET = "https://autodev.openspeech.cn/csp/api/v2.1/weather?openId=aiuicus&clientType=android&sign=android&city=上海&latitude=39.902895&longitude=116.427915&needMoreData=true&pageNo=1&pageSize=7";
    public static final String HTTP_METHOD_DELETE = HTTP_BASE + "delete";
    public static final String HTTP_METHOD_HEAD = HTTP_BASE + "head";
    public static final String HTTP_METHOD_OPTIONS = HTTP_BASE + "options";
    public static final String HTTP_METHOD_PATCH = HTTP_BASE + "patch";
    public static final String HTTP_METHOD_POST = HTTP_BASE + "post";
    public static final String HTTP_METHOD_PUT = HTTP_BASE + "put";
    public static final String HTTP_METHOD_TRACE = HTTP_BASE + "trace";


    public static final String[] IMAGE_URL_ARRAY = {
            DOWN_SHARE_SHARE,
            DOWN_IMAGE,
            "http://img6.16fan.com/201510/11/005258wdngg6rv0tpn8z9z.jpg",
            "http://img6.16fan.com/201510/11/013553aj3kp9u6iuz6k9uj.jpg",
            "http://img6.16fan.com/201510/11/011753fnanichdca0wbhxc.jpg",
            "http://img6.16fan.com/201510/11/011819zbzbciir9ctn295o.jpg",
            "http://img6.16fan.com/201510/11/004847l7w568jc5n5wn385.jpg",
            "http://img6.16fan.com/201510/11/004906z0a0a0e0hs56ce0t.jpg",
            "http://img6.16fan.com/201510/11/004937pwttwjt0bgtoton7.jpg",
            "http://img6.16fan.com/201510/11/004946t38ybzt8bq8c838y.jpg",
            "http://img6.16fan.com/201510/11/004955d8ftz3t1sttt7ft7.jpg",
            "http://img6.16fan.com/201510/11/005027qy2g55yyglb59zdu.jpg",
            "http://img6.16fan.com/201510/11/005229bbtxkczcl0btmw8e.jpg",
            // 下面这张是：5760 * 3840
            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg",
            // 下面这张是：2280 * 22116
            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg"
    };

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.mButtonGet.setOnClickListener(v -> httpGet());
        mBinding.mButtonPost.setOnClickListener(v -> httpPost());
    }


    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }


    private void httpGet() {
        OkHttp.getInstance().get(DOWN_SHARE_SHARE).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });

/*        OkHttp.getInstance().get(DOWN_SHARE_SHARE).execute(new BitmapHttpCallback(){
            @Override
            public void success(@Nullable Bitmap bitmap) {
                super.success(bitmap);
            }
        });*/
//        ThreadHelper.getInstance().doAsync(() -> {
//            final String result = HttpUtil.getForm(HTTP_METHOD_GET, "");
//            DialogManager.getInstance().showTip(getChildFragmentManager(), result);
//        });
    }

    private void httpPost() {

    }
}
