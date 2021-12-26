package com.colin.android.demo.java.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.BannerAdapter;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.databinding.FragmentHomeBinding;
import com.colin.android.demo.java.transform.AccordionTransformer;
import com.colin.android.demo.java.transform.BackgroundToForegroundTransformer;
import com.colin.android.demo.java.transform.CubeInTransformer;
import com.colin.android.demo.java.transform.CubeOutTransformer;
import com.colin.android.demo.java.transform.CustomTransformer;
import com.colin.android.demo.java.transform.DefaultTransformer;
import com.colin.android.demo.java.transform.DepthPageTransformer;
import com.colin.android.demo.java.transform.FlipHorizontalTransformer;
import com.colin.android.demo.java.transform.FlipVerticalTransformer;
import com.colin.android.demo.java.transform.ForegroundToBackgroundTransformer;
import com.colin.android.demo.java.transform.RotateDownTransformer;
import com.colin.android.demo.java.transform.RotateUpTransformer;
import com.colin.android.demo.java.transform.ScaleInOutTransformer;
import com.colin.android.demo.java.transform.ScaleInTransformer;
import com.colin.android.demo.java.transform.ScaleYTransformer;
import com.colin.android.demo.java.transform.StackTransformer;
import com.colin.android.demo.java.transform.TabletTransformer;
import com.colin.android.demo.java.transform.ZoomInTransformer;
import com.colin.android.demo.java.transform.ZoomOutSlideTransformer;
import com.colin.android.demo.java.transform.ZoomOutTranformer;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final ViewPager2 viewPager2 = binding.bannerPager;
        final RecyclerView recyclerView = binding.titleList;
        initRecyclerView(requireContext(), recyclerView);
        return root;
    }


    private void initRecyclerView(@NonNull Context context, @NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final List<String> list = Arrays.asList("AccordionTransformer", "BackgroundToForegroundTransformer"
                , "CubeInTransformer", "CubeOutTransformer", "CustomTransformer", "DefaultTransformer",
                "DepthPageTransformer", "FlipHorizontalTransformer", "FlipVerticalTransformer", "ForegroundToBackgroundTransformer",
                "RotateDownTransformer", "RotateUpTransformer", "ScaleInOutTransformer", "ScaleInTransformer",
                "ScaleYTransformer", "StackTransformer", "TabletTransformer", "ZoomInTransformer",
                "ZoomOutSlideTransformer", "ZoomOutTranformer");
        final StringAdapter adapter = new StringAdapter(context, list);
        adapter.setOnItemClickListener((view, position, object) -> {
            final String title = object == null ? "" : object.toString();
            switch (title) {
                case "AccordionTransformer":
                    initTrans(binding.bannerPager, new AccordionTransformer());
                    break;
                case "BackgroundToForegroundTransformer":
                    initTrans(binding.bannerPager, new BackgroundToForegroundTransformer());
                    break;
                case "CubeInTransformer":
                    initTrans(binding.bannerPager, new CubeInTransformer());
                    break;
                case "CubeOutTransformer":
                    initTrans(binding.bannerPager, new CubeOutTransformer());
                    break;
                case "CustomTransformer":
                    initTrans(binding.bannerPager, new CustomTransformer());
                    break;
                case "DefaultTransformer":
                    initTrans(binding.bannerPager, new DefaultTransformer());
                    break;
                case "DepthPageTransformer":
                    initTrans(binding.bannerPager, new DepthPageTransformer());
                    break;
                case "FlipHorizontalTransformer":
                    initTrans(binding.bannerPager, new FlipHorizontalTransformer());
                    break;
                case "FlipVerticalTransformer":
                    initTrans(binding.bannerPager, new FlipVerticalTransformer());
                    break;
                case "ForegroundToBackgroundTransformer":
                    initTrans(binding.bannerPager, new ForegroundToBackgroundTransformer());
                    break;
                case "RotateDownTransformer":
                    initTrans(binding.bannerPager, new RotateDownTransformer());
                    break;
                case "RotateUpTransformer":
                    initTrans(binding.bannerPager, new RotateUpTransformer());
                    break;
                case "ScaleInOutTransformer":
                    initTrans(binding.bannerPager, new ScaleInOutTransformer());
                    break;
                case "ScaleInTransformer":
                    initTrans(binding.bannerPager, new ScaleInTransformer(0.9f));
                    break;
                case "ScaleYTransformer":
                    initTrans(binding.bannerPager, new ScaleYTransformer());
                    break;
                case "StackTransformer":
                    initTrans(binding.bannerPager, new StackTransformer());
                    break;
                case "TabletTransformer":
                    initTrans(binding.bannerPager, new TabletTransformer());
                    break;
                case "ZoomInTransformer":
                    initTrans(binding.bannerPager, new ZoomInTransformer());
                    break;
                case "ZoomOutSlideTransformer":
                    initTrans(binding.bannerPager, new ZoomOutSlideTransformer());
                    break;
                case "ZoomOutTranformer":
                    initTrans(binding.bannerPager, new ZoomOutTranformer());
                    break;
                default:
                    break;
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void initTrans(@NonNull ViewPager2 viewPager2, ViewPager2.PageTransformer transformer) {
        final List<Integer> list = Arrays.asList(R.mipmap.banner1, R.mipmap.banner2, R.mipmap.banner3, R.mipmap.banner4, R.mipmap.banner5);
        final BannerAdapter adapter = new BannerAdapter(requireContext(), list);
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer(transformer);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}