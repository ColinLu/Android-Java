package com.colin.android.demo.java.ui.method;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentLogBinding;
import com.colin.library.android.annotation.LogLevel;
import com.colin.library.android.utils.LogUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:33
 * <p>
 * 描述： Log 用例
 */
public class LogFragment extends AppFragment<FragmentLogBinding> {
    public static final String TAG = "LogFragment";
    public static final String JSON = "{\n" + "  \"sites\": {\n" + "    \"site\": [\n" + "      {\n" + "        \"id\": \"1\",\n" + "        \"name\": \"菜鸟教程\",\n" + "        \"url\": \"www.runoob.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"2\",\n" + "        \"name\": \"菜鸟工具\",\n" + "        \"url\": \"www.jyshare.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"3\",\n" + "        \"name\": \"Google\",\n" + "        \"url\": \"www.google.com\"\n" + "      }\n" + "    ]\n" + "  }\n" + "}";
    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<sites>\n" + "    <site>\n" + "        <id>1</id>\n" + "        <name>菜鸟教程</name>\n" + "        <url>www.runoob.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>2</id>\n" + "        <name>菜鸟工具</name>\n" + "        <url>www.jyshare.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>3</id>\n" + "        <name>Google</name>\n" + "        <url>www.google.com</url>\n" + "    </site>\n" + "</sites>\n";

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.mButtonLogI.setOnClickListener(v -> LogUtil.i((Object) null));
        mBinding.mButtonLogV.setOnClickListener(v -> LogUtil.v("Tag V", "log v", "1.f"));
        mBinding.mButtonLogD.setOnClickListener(v -> LogUtil.d("String", 1, 2.0D, 3.0F));
        mBinding.mButtonLogW.setOnClickListener(v -> LogUtil.w("log w"));
        mBinding.mButtonLogA.setOnClickListener(v -> LogUtil.log("log a", "log a"));
        mBinding.mButtonLogE.setOnClickListener(v -> LogUtil.e(TAG, "log e", "log e"));
        mBinding.mButtonLogJson.setOnClickListener(v -> LogUtil.json(LogLevel.I, JSON));
        mBinding.mButtonLogXml.setOnClickListener(v -> LogUtil.xml(LogLevel.E, XML));
        mBinding.mButtonLogThrowable.setOnClickListener(v -> LogUtil.log(new NullPointerException("log throwable")));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}
