package com.colin.android.demo.java.ui.web;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentWebViewBinding;
import com.colin.android.demo.java.def.Constants;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.ViewUtil;
import com.colin.library.android.widgets.web.IWebClient;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-07-18
 * Des   :TODO
 */


public class WebViewFragment extends AppFragment<FragmentWebViewBinding> implements IWebClient {
    private final OnBackPressedCallback mBackCallback = new OnBackPressedCallback(true) {

        @Override
        public void handleOnBackPressed() {
            LogUtil.e(String.format("handleOnBackPressed canGoBack:%s", mBinding.mWebView.canGoBack()));
            if (mBinding.mWebView.canGoBack()) {
                mBinding.mWebView.goBack();
            }
        }
    };
    ActivityResultLauncher<Intent> launcherIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result -> {
        LogUtil.i("launcherIntent:%s", result.toString());
    }));

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, mBackCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.mWebView.onPause();
    }


    @Override
    public void onDestroyView() {
        ViewUtil.destroy(mBinding.mWebView);
        super.onDestroyView();
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.mRefreshList.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mBinding.mRefreshList.setOnRefreshListener(() -> mBinding.mWebView.reload());
        ViewUtil.init(mBinding.mWebView, new DemoWebViewClient(this), new DemoWebChromeClient(this));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (bundle != null) {
            final int type = bundle.getInt(Constants.EXTRAS_WEB_TYPE, Constants.EXTRAS_WEB_TYPE_DEFAULT);
            final String url = bundle.getString(Constants.EXTRAS_WEB_URL, Constants.EXTRAS_WEB_URL_DEFAULT);
            mBinding.mWebView.loadUrl(url);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
        final Uri uri = request.getUrl();
        LogUtil.i("shouldOverrideUrlLoading uri:%s", uri.toString());
        if (shouldInterceptUri(uri)) return true;
        mBinding.mWebView.loadUrl(request.getUrl().toString(), request.getRequestHeaders());
        return true;
    }

    @Override
    public void onReceivedTitle(@NonNull WebView view, @Nullable String title) {
        ((MainActivity) requireActivity()).updateTitle(title);
    }

    @Override
    public void onPageStarted(@NonNull WebView view, String url, Bitmap favicon) {
        mBinding.mRefreshList.setRefreshing(true);
        mBinding.mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onProgressChanged(@NonNull WebView view, int newProgress) {
        mBinding.mProgress.setProgress(newProgress);
    }

    @Override
    public void onPageFinished(@NonNull WebView view, String url) {
        mBinding.mRefreshList.setRefreshing(false);
        mBinding.mProgress.setVisibility(View.GONE);
        final boolean canGoBack = mBinding.mWebView.canGoBack();
        LogUtil.i(String.format("onPageFinished canGoBack:%s", canGoBack));
        mBackCallback.setEnabled(!canGoBack);
    }

    @Override
    public boolean onShowFileChooser(@NonNull WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        LogUtil.i("fileChooserParams:%s", fileChooserParams.createIntent().toString());
        launcherIntent.launch(fileChooserParams.createIntent(), ActivityOptionsCompat.makeBasic());
        return true;
    }

    private boolean shouldInterceptUri(@NonNull final Uri uri) {
        String scheme = uri.getScheme();
        String host = uri.getHost();
        LogUtil.i(String.format("uri scheme:%s host:%s", scheme == null ? "null" : scheme, host));
        if ("sms".equalsIgnoreCase(scheme) || "smsto".equalsIgnoreCase(scheme)) {
            launcherIntent.launch(new Intent(Intent.ACTION_SENDTO, uri), ActivityOptionsCompat.makeBasic());
            return true;
        }
        if ("tel".equalsIgnoreCase(scheme)) {
            launcherIntent.launch(new Intent(Intent.ACTION_DIAL, uri), ActivityOptionsCompat.makeBasic());
            return true;
        }
        return false;
    }
}