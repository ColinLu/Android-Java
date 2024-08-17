package com.colin.library.android.map.location;

import android.location.Location;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.colin.library.android.map.MapHelper;
import com.colin.library.android.map.def.Constants;
import com.colin.library.android.map.def.Status;
import com.colin.library.android.utils.LogUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 */
class GoogleLocationRepository implements ILocationProxy, ActivityResultCallback<Map<String, Boolean>> {
    public static final String TAG = "GoogleLocationRepository";
    private ActivityResultLauncher<String[]> mLauncher;
    private WeakReference<OnLocationListener> mListenerRef;
    private FusedLocationProviderClient mLocationClient;
    private final LocationCallback mCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                LogUtil.d("Location:" + location.toString());
                GoogleLocationRepository.this.notify(Status.Success, location);
            }
        }
    };

    public GoogleLocationRepository(@NonNull ActivityResultRegistry registry, @NonNull OnLocationListener listener) {
        this.mLauncher = registry.register(TAG, new ActivityResultContracts.RequestMultiplePermissions(), this);
        this.mListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void onActivityResult(@NonNull Map<String, Boolean> result) {
        final AtomicBoolean isGranted = new AtomicBoolean(false);
        result.forEach((permission, granted) -> {
            Log.i(TAG, "permission:" + permission + "\t granted:" + granted);
            if (granted) isGranted.set(true);
        });
        start(isGranted.get());
    }

    @Override
    public void start() {
        mLauncher.launch(Constants.PERMISSIONS_OF_LOCATION);
    }

    private void start(boolean granted) {
        if (!granted) {
            notify(Status.Failed, new Location(TAG));
            return;
        }
        if (mLocationClient == null) mLocationClient = createClient();
        mLocationClient.requestLocationUpdates(createRequest(), Executors.newSingleThreadExecutor(), mCallback);
    }


    @Override
    public void pause() {
        if (mLocationClient != null) mLocationClient.removeLocationUpdates(mCallback);
    }

    @Override
    public void destroy() {
        mLauncher = null;
        mListenerRef = null;
        if (mLocationClient != null) mLocationClient.removeLocationUpdates(mCallback);
    }

    @NonNull
    private FusedLocationProviderClient createClient() {
        return LocationServices.getFusedLocationProviderClient(MapHelper.getInstance().getMapConfig().getApplication());
    }

    @NonNull
    private LocationRequest createRequest() {
        return new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1_000L)
                .setWaitForAccurateLocation(true)//设置等待高精度定位
                .setMinUpdateDistanceMeters(10F)
                .setMinUpdateIntervalMillis(500L)
                .build();
    }


    private void notify(@Status int status, @NonNull Location location) {
        OnLocationListener listener = mListenerRef == null ? null : mListenerRef.get();
        if (listener != null) listener.change(status, location);
    }
}
