package com.colin.android.demo.java.ui.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.BannerAdapter;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentBannerBinding;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.widgets.def.Constants;
import com.colin.library.android.widgets.def.OnItemClickListener;
import com.colin.library.android.widgets.transform.AccordionTransformer;
import com.colin.library.android.widgets.transform.BackgroundToForegroundTransformer;
import com.colin.library.android.widgets.transform.CubeInTransformer;
import com.colin.library.android.widgets.transform.CubeOutTransformer;
import com.colin.library.android.widgets.transform.DefaultTransformer;
import com.colin.library.android.widgets.transform.DepthPageTransformer;
import com.colin.library.android.widgets.transform.FlipHorizontalTransformer;
import com.colin.library.android.widgets.transform.FlipVerticalTransformer;
import com.colin.library.android.widgets.transform.ForegroundToBackgroundTransformer;
import com.colin.library.android.widgets.transform.MeiZuEffectTransformer;
import com.colin.library.android.widgets.transform.RotateDownTransformer;
import com.colin.library.android.widgets.transform.RotateUpTransformer;
import com.colin.library.android.widgets.transform.ScaleInOutTransformer;
import com.colin.library.android.widgets.transform.ScaleInTransformer;
import com.colin.library.android.widgets.transform.ScaleYTransformer;
import com.colin.library.android.widgets.transform.StackTransformer;
import com.colin.library.android.widgets.transform.TabletTransformer;
import com.colin.library.android.widgets.transform.ZoomInTransformer;
import com.colin.library.android.widgets.transform.ZoomOutSlideTransformer;
import com.colin.library.android.widgets.transform.ZoomOutTransformer;

import java.util.Arrays;
import java.util.List;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-07-27
 * Des   :Banner效果
 */
public class BannerFragment extends AppFragment<FragmentBannerBinding> implements OnItemClickListener {
    private StringAdapter mAdapter;

    @Override
    public void initView(@Nullable Bundle bundle) {
        mAdapter = new StringAdapter(Arrays.asList(getResources().getStringArray(R.array.banner_transformer)), this);
        DemoUtils.initRecyclerView(mBinding.mRecyclerView, mAdapter);
        initBanner(getTransformer(mAdapter.getSelected()));
    }

    private void initBanner(ViewPager2.PageTransformer transformer) {
        final List<Integer> list = Arrays.asList(R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4, R.mipmap.banner5);
        final BannerAdapter adapter = new BannerAdapter(list, (view, position, object) -> {
            LogUtil.i("position:" + position);
        });
        mBinding.mBanner.setAdapter(adapter);
        mBinding.mBanner.setPageTransformer(transformer);
    }

    private ViewPager2.PageTransformer getTransformer(int selected) {
        if (selected == Constants.INVALID) return null;
        final String text = getResources().getStringArray(R.array.banner_transformer)[selected];
        ViewPager2.PageTransformer transformer = null;
        switch (text) {
            case "Accordion":
                transformer = new AccordionTransformer();
                break;
            case "BackgroundToForeground":
                transformer = new BackgroundToForegroundTransformer();
                break;
            case "CubeIn":
                transformer = new CubeInTransformer();
                break;
            case "CubeOut":
                transformer = new CubeOutTransformer();
                break;
            case "Default":
                transformer = new DefaultTransformer();
                break;
            case "DepthPage":
                transformer = new DepthPageTransformer();
                break;
            case "FlipHorizontal":
                transformer = new FlipHorizontalTransformer();
                break;
            case "FlipVertical":
                transformer = new FlipVerticalTransformer();
                break;
            case "ForegroundToBackground":
                transformer = new ForegroundToBackgroundTransformer();
                break;
            case "MeiZuEffect":
                transformer = new MeiZuEffectTransformer();
                break;
            case "RotateDown":
                transformer = new RotateDownTransformer();
                break;
            case "RotateUp":
                transformer = new RotateUpTransformer();
                break;
            case "ScaleInOut":
                transformer = new ScaleInOutTransformer();
                break;
            case "ScaleIn":
                transformer = new ScaleInTransformer(0.55F);
                break;
            case "ScaleY":
                transformer = new ScaleYTransformer();
                break;
            case "Stack":
                transformer = new StackTransformer();
                break;
            case "Tablet":
                transformer = new TabletTransformer();
                break;
            case "ZoomIn":
                transformer = new ZoomInTransformer();
                break;
            case "ZoomOutSlide":
                transformer = new ZoomOutSlideTransformer();
                break;
            case "ZoomOut":
                transformer = new ZoomOutTransformer();
                break;
            default:
                break;
        }
        return transformer;
    }

    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {
        final int old = mAdapter.getSelected();
        if (old == position) return;
        mAdapter.setSelected(position);
        if (old != Constants.INVALID) mAdapter.notifyItemChanged(old);
        mAdapter.notifyItemChanged(position);
        initBanner(getTransformer(position));
    }
}