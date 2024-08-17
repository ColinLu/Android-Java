package com.colin.library.android.map.google;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.colin.library.android.map.MapHelper;
import com.colin.library.android.map.R;
import com.colin.library.android.map.amap.AMapFragment;
import com.colin.library.android.map.location.MapLocationObserver;
import com.colin.library.android.map.location.OnLocationListener;
import com.colin.library.android.utils.ToastUtil;


public class GoogleMapFragment extends Fragment implements OnLocationListener {
    private static final String EXTRA_KEY = "EXTRA_KEY";
    private String mKey;
    private MapLocationObserver mLocationObserver;
    private TextView mLoctionText;

    public static AMapFragment newInstance(@NonNull String key) {
        AMapFragment fragment = new AMapFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    public GoogleMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        if (bundle != null) mKey = bundle.getString(EXTRA_KEY);
        mLocationObserver = MapHelper.getInstance().location(requireActivity().getActivityResultRegistry(), getLifecycle(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_google_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void change(int status, @NonNull Location location) {
        ContextCompat.getMainExecutor(requireContext()).execute(() -> ToastUtil.show(location.toString()));
    }
}