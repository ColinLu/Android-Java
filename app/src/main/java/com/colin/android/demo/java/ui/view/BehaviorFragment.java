package com.colin.android.demo.java.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentBehaviorBinding;
import com.colin.android.demo.java.ui.web.DemoWebChromeClient;
import com.colin.android.demo.java.ui.web.DemoWebViewClient;
import com.colin.library.android.utils.ViewUtil;
import com.colin.library.android.widgets.behavior.BottomAreaBehavior;
import com.colin.library.android.widgets.behavior.TopAreaBehavior;
import com.colin.library.android.widgets.def.OnItemClickListener;
import com.colin.library.android.widgets.scroll.NestedScrollBottomRecyclerView;
import com.colin.library.android.widgets.scroll.NestedScrollTopWebView;
import com.colin.library.android.widgets.web.IWebClient;

import java.util.Arrays;

/**
 * 作者： ColinLu
 * 时间： 2023-01-01 00:07
 * <p>
 * 描述： TODO
 */
public class BehaviorFragment extends AppFragment<FragmentBehaviorBinding> implements OnItemClickListener, IWebClient {
    private WebView mWebView;
    private RecyclerView mRecyclerView;
    private StringAdapter mAdapter;
    private String mUrl = "https://mp.weixin.qq.com/s/zgfLOMD2JfZJKfHx-5BsBg";

    @Override
    public void initView(@Nullable Bundle bundle) {
        final CoordinatorLayout.LayoutParams topParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        topParams.setBehavior(new TopAreaBehavior(requireContext()));
        mWebView = new NestedScrollTopWebView(requireContext());
        mBinding.mCoordinator.setTopAreaView(mWebView, topParams);

        mRecyclerView = new NestedScrollBottomRecyclerView(requireContext());
        final CoordinatorLayout.LayoutParams bottomParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bottomParams.setBehavior(new BottomAreaBehavior());
        mBinding.mCoordinator.setBottomAreaView(mRecyclerView, bottomParams);
        ViewUtil.init(mWebView, new DemoWebViewClient(this), new DemoWebChromeClient(this));
        mWebView.loadUrl(mUrl);
        initRecyclerView(requireContext());


    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }

    private void initRecyclerView(@NonNull Context context) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
        if (mAdapter == null) {
            mAdapter = new StringAdapter(this);
            mAdapter.setData(Arrays.asList(context.getResources().getStringArray(R.array.flow_data)));
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewUtil.destroy(mWebView);
    }

    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {

    }
}
