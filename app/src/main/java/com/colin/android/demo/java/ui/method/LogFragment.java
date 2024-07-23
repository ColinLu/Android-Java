package com.colin.android.demo.java.ui.method;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.LayoutListBinding;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.widgets.def.OnItemClickListener;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:33
 * <p>
 * 描述： Log 用例
 */
public class LogFragment extends AppFragment<LayoutListBinding> implements OnItemClickListener {
    public static final String TAG = "LogFragment";
    public static final String JSON = "{\n" + "  \"sites\": {\n" + "    \"site\": [\n" + "      {\n" + "        \"id\": \"1\",\n" + "        \"name\": \"菜鸟教程\",\n" + "        \"url\": \"www.runoob.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"2\",\n" + "        \"name\": \"菜鸟工具\",\n" + "        \"url\": \"www.jyshare.com\"\n" + "      },\n" + "      {\n" + "        \"id\": \"3\",\n" + "        \"name\": \"Google\",\n" + "        \"url\": \"www.google.com\"\n" + "      }\n" + "    ]\n" + "  }\n" + "}";
    public static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<sites>\n" + "    <site>\n" + "        <id>1</id>\n" + "        <name>菜鸟教程</name>\n" + "        <url>www.runoob.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>2</id>\n" + "        <name>菜鸟工具</name>\n" + "        <url>www.jyshare.com</url>\n" + "    </site>\n" + "    <site>\n" + "        <id>3</id>\n" + "        <name>Google</name>\n" + "        <url>www.google.com</url>\n" + "    </site>\n" + "</sites>\n";

    private LogFragmentViewModel mViewModel;
    private StringAdapter mAdapter;

    @Override
    public void initView(@Nullable Bundle bundle) {
        initRecyclerView();
        mBinding.mRefreshList.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mBinding.mRefreshList.setOnRefreshListener(() -> loadData(true));
    }

    private void initRecyclerView() {
        if (mAdapter == null) mAdapter = new StringAdapter(this);
        mAdapter.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        DemoUtils.initRecyclerView(mBinding.mRecyclerView, mAdapter);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mViewModel = new ViewModelProvider(this).get(LogFragmentViewModel.class);
        mViewModel.getList().observe(this, list -> mAdapter.setData(list));
        mViewModel.getLoadState().observe(this, state -> mBinding.mRefreshList.setRefreshing(state == LoadState.ING));
    }

    @Override
    public void loadData(boolean refresh) {
        mViewModel.refresh(refresh);
    }

    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {
        final String value = object == null ? "" : object.toString();
        switch (value) {
            case "Log V":
                LogUtil.v(value);
                break;
            case "Log V with Tag":
                LogUtil.v(TAG, value);
                break;
            case "Log V by format":
                LogUtil.v("format log-> tag:%s int:%d float:%.2f string:%s", TAG, 3, 5.4567F, value);
                break;
            case "Log D":
                LogUtil.d(value);
                break;
            case "Log D with Tag":
                LogUtil.d(TAG, value);
                break;
            case "Log D by format":
                LogUtil.d("format log-> int:%d float:%.2f string:%s", 3, 5.4567F, value);
                break;
            case "Log I":
                LogUtil.i(value);
                break;
            case "Log I with Tag":
                LogUtil.i(TAG, value);
                break;
            case "Log I by format":
                LogUtil.i("format log-> int:%d float:%.2f string:%s", 3, 5.4567F, value);
                break;
            case "Log W":
                LogUtil.w(value);
                break;
            case "Log W with Tag":
                LogUtil.w(TAG, value);
                break;
            case "Log W by format":
                LogUtil.w("format log-> int:%d float:%.2f string:%s", 3, 5.4567F, value);
                break;
            case "Log E":
                LogUtil.e(value);
                break;
            case "Log E with Tag":
                LogUtil.e(TAG, value);
                break;
            case "Log E by format":
                LogUtil.e("format log-> int:%d float:%.2f string:%s", 3, 5.4567F, value);
                break;
            case "Log A":
                LogUtil.a(value);
                break;
            case "Log A with Tag":
                LogUtil.a(TAG, value);
                break;
            case "Log A by format":
                LogUtil.a("format log-> int:%d float:%.2f string:%s", 3, 5.4567F, value);
                break;
            case "Log Json":
                LogUtil.json(JSON);
                break;
            case "Log Json with Tag":
                LogUtil.json(TAG, JSON);
                break;
            case "Log Xml":
                LogUtil.xml(XML);
                break;
            case "Log Xml with Tag":
                LogUtil.xml(TAG, XML);
                break;
            case "Log Error":
                LogUtil.log(new Throwable(value));
                break;
            default:
                break;
        }
    }
}
