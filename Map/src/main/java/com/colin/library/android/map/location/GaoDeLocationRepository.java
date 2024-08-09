package com.colin.library.android.map.location;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.colin.library.android.map.MapHelper;
import com.colin.library.android.map.def.Constants;
import com.colin.library.android.map.def.Status;
import com.colin.library.android.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :http://amappc.cn-hangzhou.oss-pub.aliyun-inc.com/lbs/static/unzip/Android_Location_Doc/index.html
 */
class GaoDeLocationRepository implements ILocationProxy, AMapLocationListener {
    public static final String TAG = "GaoDeLocationRepository";
    private AMapLocationClient mLocationClient;
    private WeakReference<OnLocationListener> mListenerRef;

    private final ActivityResultLauncher<String[]> mLauncher;
    private final RequestMultiplePermissions mPermissions = new ActivityResultContracts.RequestMultiplePermissions();
    private final ActivityResultCallback<Map<String, Boolean>> mCallback = result -> {
        AtomicBoolean isGranted = new AtomicBoolean(false);
        result.forEach((permission, granted) -> {
            Log.e(TAG, "permission:" + permission + "\t granted:" + granted);
            if (granted) isGranted.set(true);
            if (isGranted.get()) start(true, mListenerRef == null ? null : mListenerRef.get());
        });
    };


    public GaoDeLocationRepository(@NonNull ActivityResultRegistry registry) {
        mLauncher = registry.register(TAG, mPermissions, mCallback);
    }


    @Override
    public void start(boolean granted, @Nullable OnLocationListener listener) {
        if (!granted) {
            mListenerRef = new WeakReference<>(listener);
            mLauncher.launch(Constants.PERMISSIONS_OF_LOCATION);
            return;
        }
        mListenerRef = new WeakReference<>(listener);
        if (mLocationClient == null) mLocationClient = createClient();
        if (mLocationClient.isStarted()) mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }

    @Override
    public void pause() {
        if (mLocationClient != null) mLocationClient.stopLocation();
    }

    @Override
    public void destroy() {
        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @NonNull
    private AMapLocationClient createClient() {
        final Application application = MapHelper.getInstance().getMapConfig().getApplication();
        final AMapLocationClient locationClient = new AMapLocationClient(application);
        locationClient.setLocationListener(this);
        locationClient.setLocationOption(createOption());
        return locationClient;
    }

    private AMapLocationClientOption createOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setOnceLocationLatest(false);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        option.setOffset(false);
        return option;
    }

    @Override
    public void onLocationChanged(AMapLocation mapLocation) {
        LogUtil.i(TAG, String.format(Locale.US, "code:%d msg:%s", mapLocation.getErrorCode(), mapLocation.getErrorInfo()));
        OnLocationListener listener = mListenerRef == null ? null : mListenerRef.get();
        if (listener == null) return;
        if (mapLocation.getErrorCode() == 0) listener.change(Status.Success, convert(mapLocation));
        else listener.change(Status.Failed, convert(mapLocation));
    }

    private Location convert(AMapLocation mapLocation) {
        final Location location = new Location(TAG);
        location.setAccuracy(mapLocation.getAccuracy());
        location.setTime(mapLocation.getTime());
        location.setElapsedRealtimeNanos(mapLocation.getElapsedRealtimeNanos());
        location.setElapsedRealtimeUncertaintyNanos(mapLocation.getElapsedRealtimeUncertaintyNanos());
        location.setLatitude(mapLocation.getLatitude());
        location.setLongitude(mapLocation.getLongitude());
        location.setVerticalAccuracyMeters(mapLocation.getVerticalAccuracyMeters());
        location.setAltitude(mapLocation.getAltitude());
        location.setSpeed(mapLocation.getSpeed());
        location.setSpeedAccuracyMetersPerSecond(mapLocation.getSpeedAccuracyMetersPerSecond());
        location.setBearing(mapLocation.getBearing());
        location.setExtras(mapLocation.getExtras());
        return location;
    }


}
