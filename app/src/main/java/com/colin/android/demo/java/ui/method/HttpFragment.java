package com.colin.android.demo.java.ui.method;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentHttpBinding;
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.annotation.Encode;
import com.colin.library.android.http.OkHttpHelper;
import com.colin.library.android.http.action.ActionBitmap;
import com.colin.library.android.http.action.ActionFile;
import com.colin.library.android.http.action.ActionString;
import com.colin.library.android.utils.LogUtil;

import java.io.File;
import java.util.Locale;

/**
 * 作者： ColinLu
 * 时间： 2022-12-08 22:49
 * <p>
 * 描述： TODO
 */
public class HttpFragment extends AppFragment<FragmentHttpBinding> {
    //    public static final String DOWN_TEXT = "https://txt.xbaoshu.com/d/file/down/2023/11/29/都市医仙.txt";txt
    public static final String DOWN_IMAGE = "http://inews.gtimg.com/newsapp_bt/0/876781763/1000";
    public static final String DOWN_TEXT = "http://dt.80zw.la/167882/%E9%87%8D%E7%94%9F%E6%97%A5%E5%B8%B8%E4%BF%AE%E4%BB%99.txt";

    public static final String HTTP_BASE = "https://postman-echo.com/";
    public static final String HTTP_METHOD_GET = HTTP_BASE + "get?test=123";
    public static final String HTTP_METHOD_DELETE = HTTP_BASE + "delete";
    public static final String HTTP_METHOD_HEAD = HTTP_BASE + "head";
    public static final String HTTP_METHOD_OPTIONS = HTTP_BASE + "options";
    public static final String HTTP_METHOD_PATCH = HTTP_BASE + "patch";
    public static final String HTTP_METHOD_POST = HTTP_BASE + "post";
    public static final String HTTP_METHOD_PUT = HTTP_BASE + "put";
    public static final String HTTP_METHOD_TRACE = HTTP_BASE + "trace";
    public static final int REQUEST_STORAGE = 1;


    public static final String[] IMAGE_URL_ARRAY = {DOWN_IMAGE, "http://img6.16fan.com/201510/11/005258wdngg6rv0tpn8z9z.jpg", "http://img6.16fan" + ".com/201510/11/013553aj3kp9u6iuz6k9uj.jpg", "http://img6.16fan.com/201510/11/011753fnanichdca0wbhxc.jpg", "http" + "://img6.16fan" + ".com/201510/11/011819zbzbciir9ctn295o.jpg", "http://img6.16fan.com/201510/11/004847l7w568jc5n5wn385.jpg", "http" + "://img6.16fan" + ".com/201510/11/004906z0a0a0e0hs56ce0t.jpg", "http://img6.16fan.com/201510/11/004937pwttwjt0bgtoton7.jpg", "http" + "://img6.16fan" + ".com/201510/11/004946t38ybzt8bq8c838y.jpg", "http://img6.16fan.com/201510/11/004955d8ftz3t1sttt7ft7.jpg", "http" + "://img6.16fan" + ".com/201510/11/005027qy2g55yyglb59zdu.jpg", "http://img6.16fan.com/201510/11/005229bbtxkczcl0btmw8e.jpg",
            // 下面这张是：5760 * 3840
            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818127263ge.jpeg",
            // 下面这张是：2280 * 22116
            "http://img6.16fan.com/attachments/wenzhang/201805/18/152660818716180ge.jpeg"};


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
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpHelper.getInstance().cancelTag(this);
    }

    private void httpGet() {
        OkHttpHelper.getInstance().get(DOWN_IMAGE).tag(this).execute(new ActionBitmap() {
            @Override
            public void success(@Nullable Bitmap bitmap) {
                DialogManager.getInstance().showImage(getChildFragmentManager(), bitmap);
            }
        });
    }

    private void httpPost() {
        OkHttpHelper.getInstance().post(HTTP_METHOD_POST).param("param", "post").execute(new ActionString() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpDelete() {
        OkHttpHelper.getInstance().delete(HTTP_METHOD_DELETE).execute(new ActionString() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpHead() {
        OkHttpHelper.getInstance().head(HTTP_METHOD_HEAD).execute(new ActionString() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpOption() {
        OkHttpHelper.getInstance().options(HTTP_METHOD_OPTIONS).execute(new ActionString() {

            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpPatch() {
        OkHttpHelper.getInstance().patch(HTTP_METHOD_PATCH).execute(new ActionString() {

            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpPut() {
        OkHttpHelper.getInstance().put(HTTP_METHOD_PUT).execute(new ActionString() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpTrace() {
        OkHttpHelper.getInstance().trace(HTTP_METHOD_TRACE).execute(new ActionString() {
            @Override
            public void success(@Nullable String tips) {
                DialogManager.getInstance().showTip(getChildFragmentManager(), tips);
            }
        });
    }

    private void httpDownload() {
        OkHttpHelper.getInstance().get(DOWN_TEXT).encode(Encode.UTF_8).execute(new ActionFile() {
            @Override
            public void success(@Nullable File file) {
                LogUtil.e("file:" + (file == null ? "file is null" : file.getAbsolutePath()));
                DialogManager.getInstance().showTip(getChildFragmentManager(), file == null ? "file==null" : file.getName(), file == null ? "file==null" : file.getAbsolutePath());
            }

            @Override
            public void progress(long total, long progress) {
                LogUtil.d(String.format(Locale.US, "progress:total=%d progress=%d", total, progress));
            }
        });
    }


}
