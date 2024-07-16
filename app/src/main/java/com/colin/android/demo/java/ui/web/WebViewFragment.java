package com.colin.android.demo.java.ui.web;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentWebViewBinding;
import com.colin.android.demo.java.def.Constants;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.ViewUtil;
import com.colin.library.android.widgets.web.IWebClient;


public class WebViewFragment extends AppFragment<FragmentWebViewBinding> implements IWebClient {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewUtil.destroy(mBinding.mWebView);
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        ViewUtil.init(mBinding.mWebView, new DemoWebViewClient(this), new DemoWebChromeClient(this));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (bundle != null) {
            int type = bundle.getInt(Constants.EXTRAS_WEB_TYPE, Constants.EXTRAS_WEB_TYPE_DEFAULT);
            String url = bundle.getString(Constants.EXTRAS_WEB_URL, Constants.EXTRAS_WEB_URL_DEFAULT);
            mBinding.mWebView.loadUrl(url);
        }
    }


    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
        LogUtil.i("shouldOverrideUrlLoading url:%s", request.toString());
        mBinding.mWebView.loadUrl(request.getUrl().toString(), request.getRequestHeaders());
        return true;
    }

    @Override
    public void onReceivedTitle(@NonNull WebView view, @Nullable String title) {
        ((MainActivity) getActivity()).updateTitle(title);
    }

    @Override
    public void onProgressChanged(@NonNull WebView view, int newProgress) {
        mBinding.mProgress.setProgress(newProgress);
        if (newProgress >= 0 && newProgress <= 100) {
            mBinding.mProgress.setVisibility(View.VISIBLE);
        }
        if (newProgress >= 100 || newProgress < 0) {
            mBinding.mProgress.setVisibility(View.GONE);
        }
    }

}