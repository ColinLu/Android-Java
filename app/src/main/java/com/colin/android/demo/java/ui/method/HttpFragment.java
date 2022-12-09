package com.colin.android.demo.java.ui.method;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentHttpBinding;
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.http.OkHttp;
import com.colin.library.android.http.callback.BitmapHttpCallback;
import com.colin.library.android.http.callback.FileHttpCallback;
import com.colin.library.android.http.callback.StringHttpCallback;
import com.colin.library.android.utils.LogUtil;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2022-12-08 22:49
 * <p>
 * 描述： TODO
 */
public class HttpFragment extends AppFragment<FragmentHttpBinding> {
    public static final String DOWN_TEXT = "https://down.xbaoshu.com/d/file/down/2020/11/24/%E5%AE%8C%E7%BE%8E%E4%B8%96%E7%95%8C.txt";
    public static final String DOWN_IMAGE = "http://inews.gtimg.com/newsapp_bt/0/876781763/1000";

    public static final String HTTP_BASE = "https://postman-echo.com/";
    public static final String HTTP_METHOD_GET = HTTP_BASE + "get?test=123";
    public static final String HTTP_METHOD_DELETE = HTTP_BASE + "delete";
    public static final String HTTP_METHOD_HEAD = HTTP_BASE + "head";
    public static final String HTTP_METHOD_OPTIONS = HTTP_BASE + "options";
    public static final String HTTP_METHOD_PATCH = HTTP_BASE + "patch";
    public static final String HTTP_METHOD_POST = HTTP_BASE + "post";
    public static final String HTTP_METHOD_PUT = HTTP_BASE + "put";
    public static final String HTTP_METHOD_TRACE = HTTP_BASE + "trace";


    public static final String[] IMAGE_URL_ARRAY = {
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
        mBinding.mButtonDelete.setOnClickListener(v -> httpDelete());
        mBinding.mButtonHead.setOnClickListener(v -> httpHead());
        mBinding.mButtonOption.setOnClickListener(v -> httpOption());
        mBinding.mButtonPatch.setOnClickListener(v -> httpPatch());
        mBinding.mButtonPut.setOnClickListener(v -> httpPut());
        mBinding.mButtonTrace.setOnClickListener(v -> httpTrace());
        mBinding.mButtonDownload.setOnClickListener(v -> httpDownload());
    }


    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }


    private void httpGet() {
        OkHttp.getInstance().get(HTTP_METHOD_GET).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpPost() {
        OkHttp.getInstance().post(HTTP_METHOD_POST).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpDelete() {
        OkHttp.getInstance().delete(HTTP_METHOD_DELETE).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpHead() {
        OkHttp.getInstance().head(HTTP_METHOD_HEAD).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpOption() {
        OkHttp.getInstance().options(HTTP_METHOD_OPTIONS).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpPatch() {
        OkHttp.getInstance().patch(HTTP_METHOD_PATCH).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpPut() {
        OkHttp.getInstance().put(HTTP_METHOD_PUT).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpTrace() {
        OkHttp.getInstance().trace(HTTP_METHOD_TRACE).execute(new StringHttpCallback() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpDownload() {
        OkHttp.getInstance().get(DOWN_TEXT).execute(new FileHttpCallback() {
            @Override
            public void success(@Nullable File file) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), file.getName(), file.getAbsolutePath());
            }

            @Override
            public void progress(float total, float progress) {
                LogUtil.d(total, progress);
            }
        });
    }


}
