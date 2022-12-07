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
 * 描述： TODO
 */
public class LogFragment extends AppFragment<FragmentLogBinding> {
    public static final String TAG = "LogFragment";

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.mButtonLogI.setOnClickListener(v -> LogUtil.i((Object) null));
        mBinding.mButtonLogV.setOnClickListener(v -> LogUtil.vTag("Tag V", "log v", "1.f"));
        mBinding.mButtonLogD.setOnClickListener(v -> LogUtil.d("String", 1, 2.0D, 3.0F));
        mBinding.mButtonLogW.setOnClickListener(v -> LogUtil.w("log w"));
        mBinding.mButtonLogA.setOnClickListener(v -> LogUtil.a("log a", "log a"));
        mBinding.mButtonLogE.setOnClickListener(v -> LogUtil.e(TAG, "log e", "log e"));
        mBinding.mButtonLogJson.setOnClickListener(v -> LogUtil.json(LogLevel.D, "{\"l1\":{\"l1_1\":[\"l1_1_1\",\"l1_1_2\"],\"l1_2\":{\"l1_2_1\":121}},\"l2\":{\"l2_1\":null,\"l2_2\":true,\"l2_3\":{}}}"));
        mBinding.mButtonLogXml.setOnClickListener(v -> LogUtil.xml(LogLevel.E, "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<resources>\n" +
                "    <color name=\"purple_200\">#FFBB86FC</color>\n" +
                "    <color name=\"purple_500\">#FF6200EE</color>\n" +
                "    <color name=\"purple_700\">#FF3700B3</color>\n" +
                "    <color name=\"teal_200\">#FF03DAC5</color>\n" +
                "    <color name=\"teal_700\">#FF018786</color>\n" +
                "    <color name=\"black\">#FF000000</color>\n" +
                "    <color name=\"white\">#FFFFFFFF</color>\n" +
                "</resources>"));
        mBinding.mButtonLogThrowable.setOnClickListener(v -> LogUtil.log(new NullPointerException("log throwable")));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}
