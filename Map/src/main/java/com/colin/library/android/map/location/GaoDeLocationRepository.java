package com.colin.library.android.map.location;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

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
class GaoDeLocationRepository implements ILocationProxy, ActivityResultCallback<Map<String, Boolean>>, AMapLocationListener {
    public static final String TAG = "GaoDeLocationRepository";
    private ActivityResultLauncher<String[]> mLauncher;
    private WeakReference<OnLocationListener> mListenerRef;
    private AMapLocationClient mLocationClient;

    public GaoDeLocationRepository(@NonNull ActivityResultRegistry registry, @NonNull OnLocationListener listener) {
        this.mLauncher = registry.register(TAG, new ActivityResultContracts.RequestMultiplePermissions(), this);
        this.mListenerRef = new WeakReference<>(listener);
    }

    @Override
    public void onActivityResult(@NonNull Map<String, Boolean> result) {
        AtomicBoolean isGranted = new AtomicBoolean(false);
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
        if (mLocationClient.isStarted()) mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }


    @Override
    public void pause() {
        if (mLocationClient != null) mLocationClient.stopLocation();
    }

    @Override
    public void destroy() {
        mLauncher = null;
        mListenerRef = null;
        if (mLocationClient != null) {
            if (mLocationClient.isStarted()) mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @NonNull
    private AMapLocationClient createClient() {
        try {
            final Application application = MapHelper.getInstance().getMapConfig().getApplication();
            AMapLocationClient.updatePrivacyAgree(application, true);
            AMapLocationClient.updatePrivacyShow(application, true, true);
            final AMapLocationClient locationClient = new AMapLocationClient(application);
            locationClient.setLocationListener(this);
            locationClient.setLocationOption(createOption());
            return locationClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

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
    public void onLocationChanged(@NonNull AMapLocation mapLocation) {
        LogUtil.i(TAG, String.format(Locale.US, "code:%d msg:%s", mapLocation.getErrorCode(), mapLocation.getErrorInfo()));
        notify(mapLocation.getErrorCode() == 0 ? Status.Success : Status.Failed, convert(mapLocation));
    }

    @NonNull
    private Location convert(@NonNull AMapLocation mapLocation) {
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

    private void notify(@Status int status, @NonNull Location location) {
        OnLocationListener listener = mListenerRef == null ? null : mListenerRef.get();
        if (listener != null) listener.change(status, location);
    }
}
