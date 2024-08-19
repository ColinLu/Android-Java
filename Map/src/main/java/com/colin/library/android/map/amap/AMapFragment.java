package com.colin.library.android.map.amap;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.colin.library.android.map.MapHelper;
import com.colin.library.android.map.R;
import com.colin.library.android.map.def.Status;
import com.colin.library.android.map.location.OnLocationListener;
import com.colin.library.android.map.widgets.GaodeMapView;
import com.colin.library.android.utils.ToastUtil;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-17
 * Des   :高德地图初始化界面：地图展示+定位
 */
public class AMapFragment extends Fragment implements OnLocationListener {

    private static final String EXTRA_KEY = "EXTRA_KEY";
    private String mKey;

    public static AMapFragment newInstance(@NonNull String key) {
        AMapFragment fragment = new AMapFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    public AMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        if (bundle != null) mKey = bundle.getString(EXTRA_KEY);
        MapHelper.getInstance().location(requireActivity().getActivityResultRegistry(), getLifecycle(), this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (view instanceof GaodeMapView) initMapView((GaodeMapView) view, savedInstanceState);
        else initMapView(view.findViewById(R.id.map_view), savedInstanceState);
    }

    private void initMapView(@NonNull GaodeMapView view, @Nullable Bundle savedInstanceState) {
        view.onCreate(savedInstanceState);
        view.init(getLifecycle());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ((GaodeMapView) requireView().findViewById(R.id.map_view)).onSaveInstanceState(outState);
    }

    @Override
    public void change(int status, @NonNull Location location) {
        if (status == Status.Success) {
            ((GaodeMapView) requireView().findViewById(R.id.map_view)).updateLocation(location);
        } else {
            ToastUtil.show("定位失败");
        }
    }
}