# 高德地图

## 集成高德地图

### 1. 注册高德地图 API Key
在使用高德地图 SDK 之前，需要到 [高德开放平台](https://lbs.amap.com/) 注册一个开发者账号并创建一个应用，然后获取 API Key（也叫作应用密钥）。

### 2. 添加高德地图 SDK 依赖
在你的项目中，打开 `build.gradle`（位于 `app` 目录下），添加高德地图的依赖：

```groovy
dependencies {
    implementation 'com.amap.api:3dmap:latest_version'
}
```
将 `latest_version` 替换为最新的 SDK 版本号，可以在 [高德地图 SDK 下载页面](https://lbs.amap.com/api/android-sdk/summary) 找到最新的版本号。

### 3. 配置 AndroidManifest.xml
在 `AndroidManifest.xml` 文件中，添加必要的权限、服务以及高德地图 API Key：

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="your.package.name">

    <!-- 权限 -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <!-- 高德地图 API Key 配置 -->
        <meta-data
            android:name="com.amap.api.v2.apikey"
            android:value="your_api_key"/>

    </application>
</manifest>
```

请将 `"your_api_key"` 替换为你在高德开放平台获取的 API Key。

### 4. 在布局文件中添加 `MapView`
在 `res/layout` 目录下的 XML 布局文件中，添加 `MapView` 组件：

```xml
<com.amap.api.maps.MapView
    android:id="@+id/map_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

### 5. 在 Activity 中初始化 `MapView`
在你的 `Activity` 中，通过以下步骤来初始化 `MapView` 并展示地图：

```java
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // 确保布局文件中包含 MapView

        // 获取 MapView 组件
        mapView = findViewById(R.id.map_view);

        // 在 Activity 的 onCreate 方法中执行 MapView 的 onCreate 方法
        mapView.onCreate(savedInstanceState);

        // 初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // 设置地图显示的类型（可选）
        aMap.setMapType(AMap.MAP_TYPE_NORMAL); // 普通地图模式

        // 可以进一步设置地图的初始视角、添加标记等操作
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在 Activity 的 onResume 方法中执行 MapView 的 onResume 方法
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在 Activity 的 onPause 方法中执行 MapView 的 onPause 方法
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在 Activity 的 onDestroy 方法中执行 MapView 的 onDestroy 方法
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 在 Activity 的 onSaveInstanceState 方法中执行 MapView 的 onSaveInstanceState 方法
        mapView.onSaveInstanceState(outState);
    }
}
```

### 6. 运行项目
完成上述步骤后，运行你的项目。应用启动后，`MapView` 会显示高德地图，并可以进行进一步的地图操作，比如定位、添加标记等。

### 7. 动态权限申请（如果必要）
如果你的应用运行在 Android 6.0 及以上版本，可能需要动态申请权限。可以在 `Activity` 中使用以下代码来申请定位权限：

```java

String[] PERMISSIONS_OF_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};

 ActivityResultCallback<Map<String, Boolean>> mCallback = result -> {
    AtomicBoolean isGranted = new AtomicBoolean(false);
    result.forEach((permission, granted) -> {
    Log.i(TAG, "permission:" + permission + "\t granted:" + granted);
        if (!granted) isGranted.set(false);
    });
    if(isGranted.get()){
         //TODO 定位权限同意
    }else{
        //TODO 定位权限不同意
    }
}

private ActivityResultLauncher<String[]> mLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), mCallback);

@Override
protected void onStart() {
    super.onStart();
    mLauncher.launch(PERMISSIONS_OF_LOCATION);
}
```

### 8. 调试与发布
请确保在调试阶段正确配置了 SHA-1 指纹和 API Key，并在发布时切换到发布版的 API Key，以确保地图功能的正常使用。




## `AMap` 和 `UiSettings` API 方法的介绍。

### `AMap` ：用于控制地图的显示和交互。
以下是高德地图 AMap API 中部分常用方法的简要介绍，这些方法主要用于控制地图显示、添加覆盖物、设置事件监听等。

#### 1.1 地图控制相关

- **`getCameraPosition()`**
    - **功能**：获取当前地图的相机位置，包括经纬度、缩放级别、倾斜角度和旋转角度。
    - **返回值**：`CameraPosition` 对象。

- **`moveCamera(CameraUpdate update)`**
    - **功能**：立即移动地图相机到指定位置。
    - **参数**：`CameraUpdate`，指定相机移动的目标位置或变化。

- **`animateCamera(CameraUpdate update)`**
    - **功能**：以动画方式移动地图相机到指定位置。
    - **参数**：`CameraUpdate`，相机的目标位置或变化。

- **`getMaxZoomLevel()`**
    - **功能**：获取地图支持的最大缩放级别。
    - **返回值**：最大缩放级别（浮点数）。

- **`getMinZoomLevel()`**
    - **功能**：获取地图支持的最小缩放级别。
    - **返回值**：最小缩放级别（浮点数）。

- **`setMapType(int mapType)`**
    - **功能**：设置地图的类型，比如普通地图、卫星图等。
    - **参数**：`int mapType`，地图类型。

#### 1.2 地图覆盖物相关

- **`addMarker(MarkerOptions options)`**
    - **功能**：在地图上添加一个标记（Marker）。
    - **参数**：`MarkerOptions`，标记的配置选项。
    - **返回值**：`Marker` 对象。

- **`addPolyline(PolylineOptions options)`**
    - **功能**：在地图上添加一条折线（Polyline）。
    - **参数**：`PolylineOptions`，折线的配置选项。
    - **返回值**：`Polyline` 对象。

- **`addCircle(CircleOptions options)`**
    - **功能**：在地图上添加一个圆形覆盖物。
    - **参数**：`CircleOptions`，圆形的配置选项。
    - **返回值**：`Circle` 对象。

- **`addPolygon(PolygonOptions options)`**
    - **功能**：在地图上添加一个多边形覆盖物。
    - **参数**：`PolygonOptions`，多边形的配置选项。
    - **返回值**：`Polygon` 对象。

- **`addTileOverlay(TileOverlayOptions options)`**
    - **功能**：添加瓦片图层（TileOverlay）到地图上。
    - **参数**：`TileOverlayOptions`，瓦片图层的配置选项。
    - **返回值**：`TileOverlay` 对象。

#### 1.3 定位和地图状态相关

- **`isMyLocationEnabled()`**
    - **功能**：判断地图是否启用了定位功能。
    - **返回值**：`boolean`，表示定位功能是否启用。

- **`setMyLocationEnabled(boolean enabled)`**
    - **功能**：设置地图是否启用定位功能。
    - **参数**：`boolean enabled`，`true` 表示启用，`false` 表示禁用。

- **`getMyLocation()`**
    - **功能**：获取当前设备的位置信息。
    - **返回值**：`Location` 对象，包含经纬度、精度等信息。

#### 1.4 事件监听相关

- **`setOnMapClickListener(OnMapClickListener listener)`**
    - **功能**：设置地图单击事件的监听器。
    - **参数**：`OnMapClickListener`，回调接口。

- **`setOnMapLongClickListener(OnMapLongClickListener listener)`**
    - **功能**：设置地图长按事件的监听器。
    - **参数**：`OnMapLongClickListener`，回调接口。

- **`setOnMarkerClickListener(OnMarkerClickListener listener)`**
    - **功能**：设置标记点击事件的监听器。
    - **参数**：`OnMarkerClickListener`，回调接口。

#### 1.5 地图界面设置

- **`getUiSettings()`**
    - **功能**：获取当前地图的UI设置对象，通过此对象可以控制地图界面元素（如缩放按钮、指南针等）。
    - **返回值**：`UiSettings` 对象。

- **`showTraffic(boolean enable)`**
    - **功能**：设置是否在地图上显示实时交通信息。
    - **参数**：`boolean enable`，`true` 表示启用，`false` 表示禁用。

#### 1.6 清理和重置

- **`clear()`**
    - **功能**：清除地图上的所有覆盖物（如标记、折线、圆形等）。

- **`stopAnimation()`**
    - **功能**：停止当前正在进行的地图动画。

以上是高德地图 AMap API 常用方法的简要介绍。开发者可以利用这些方法在应用中构建功能丰富、交互性强的地图服务。

高德地图`AMap`类的全部API接口方法如下：
- `getCameraPosition()`：获取当前相机位置。
- `getMaxZoomLevel()`：获取最大缩放级别。
- `getMinZoomLevel()`：获取最小缩放级别。
- `moveCamera(CameraUpdate var1)`：移动相机。
- `animateCamera(CameraUpdate var1)`：动画相机。
- `animateCamera(CameraUpdate var1, CancelableCallback var2)`：动画相机并提供可取消的回调。
- `animateCamera(CameraUpdate var1, long var2, CancelableCallback var4)`：动画相机，指定动画持续时间和回调。
- `stopAnimation()`：停止动画。
- `addNavigateArrow(NavigateArrowOptions var1)`：添加导航箭头。
- `addPolyline(PolylineOptions var1)`：添加折线。
- `addBuildingOverlay()`：添加建筑物覆盖物。
- `addCircle(CircleOptions var1)`：添加圆形。
- `addArc(ArcOptions var1)`：添加弧线。
- `addPolygon(PolygonOptions var1)`：添加多边形。
- `addGroundOverlay(GroundOverlayOptions var1)`：添加地面覆盖物。
- `addMarker(MarkerOptions var1)`：添加标记。
- `addGL3DModel(GL3DModelOptions var1)`：添加3D模型。
- `addText(TextOptions var1)`：添加文本。
- `addMarkers(ArrayList<MarkerOptions> var1, boolean var2)`：添加多个标记。
- `getMapScreenMarkers()`：获取地图屏幕上的标记列表。
- `addTileOverlay(TileOverlayOptions var1)`：添加瓦片覆盖物。
- `addMVTTileOverlay(MVTTileOverlayOptions var1)`：添加MVTTile覆盖物。
- `addHeatMapLayer(HeatMapLayerOptions var1)`：添加热力图图层。
- `addHeatMapGridLayer(HeatMapGridLayerOptions var1)`：添加热力图网格图层。
- `addMultiPointOverlay(MultiPointOverlayOptions var1)`：添加多点覆盖物。
- `addParticleOverlay(ParticleOverlayOptions var1)`：添加粒子覆盖物。
- `addGLTFOverlay(GLTFOverlayOptions var1)`：添加GLTF覆盖物。
- `add3DModelTileOverlay(AMap3DModelTileOverlayOptions var1)`：添加3D模型瓦片覆盖物。
- `addContourLineOverlay(ep var1)`：添加等高线覆盖物。
- `clear()`：清空地图。
- `clear(boolean var1)`：根据参数清空地图。
- `getMapType()`：获取地图类型。
- `setMapType(int var1)`：设置地图类型。
- `isTrafficEnabled()`：判断交通是否启用。
- `setTrafficEnabled(boolean var1)`：设置交通启用状态。
- `showMapText(boolean var1)`：设置地图文本显示状态。
- `showIndoorMap(boolean var1)`：设置室内地图显示状态。
- `showBuildings(boolean var1)`：设置建筑物显示状态。
- `setMyTrafficStyle(MyTrafficStyle var1)`：已过时，不建议使用。
- `getMyTrafficStyle()`：已过时，不建议使用。
- `setTrafficStyleWithTextureData(byte[] var1)`：设置交通样式的纹理数据。
- `setTrafficStyleWithTexture(byte[] var1, MyTrafficStyle var2)`：设置交通样式的纹理和样式。
- `isMyLocationEnabled()`：判断我的位置是否启用。
- `setMyLocationEnabled(boolean var1)`：设置我的位置启用状态。
- `getMyLocation()`：获取我的位置。
- `setLocationSource(LocationSource var1)`：设置位置源。
- `setMyLocationStyle(MyLocationStyle var1)`：设置我的位置样式。
- `getMyLocationStyle()`：获取我的位置样式。
- `setMyLocationType(int var1)`：已过时，不建议使用。
- `setMyLocationRotateAngle(float var1)`：已过时，不建议使用。
- `getUiSettings()`：获取用户界面设置。
- `getProjection()`：获取投影。
- `setOnCameraChangeListener(OnCameraChangeListener var1)`：设置相机变化监听器。
- `setOnMapClickListener(OnMapClickListener var1)`：设置地图点击监听器。
- `setOnMapTouchListener(OnMapTouchListener var1)`：设置地图触摸监听器。
- `setOnPOIClickListener(OnPOIClickListener var1)`：设置POI点击监听器。
- `setOnMyLocationChangeListener(OnMyLocationChangeListener var1)`：设置我的位置变化监听器。
- `setOnMapLongClickListener(OnMapLongClickListener var1)`：设置地图长按监听器。
- `setOnMarkerClickListener(OnMarkerClickListener var1)`：设置标记点击监听器。
- `setOnPolylineClickListener(OnPolylineClickListener var1)`：设置折线点击监听器。
- `setOnMarkerDragListener(OnMarkerDragListener var1)`：设置标记拖拽监听器。
- `setOnInfoWindowClickListener(OnInfoWindowClickListener var1)`：设置信息窗口点击监听器。
- `setInfoWindowAdapter(InfoWindowAdapter var1)`：设置信息窗口适配器。
- `setCommonInfoWindowAdapter(CommonInfoWindowAdapter var1)`：设置通用信息窗口适配器。
- `setOnMapLoadedListener(OnMapLoadedListener var1)`：设置地图加载完成监听器。
- `setOnIndoorBuildingActiveListener(OnIndoorBuildingActiveListener var1)`：设置室内建筑物激活监听器。
- `setOnMultiPointClickListener(OnMultiPointClickListener var1)`：设置多点点击监听器。
- `getMapPrintScreen(onMapPrintScreenListener var1)`：已过时，不建议使用。
- `getMapScreenShot(OnMapScreenShotListener var1)`：获取地图屏幕截图。
- `getMapRegionSnapshot(LatLng var1, LatLng var2, Size var3, final OnMapSnapshotListener var4)`：获取地图区域快照。
- `getScalePerPixel()`：获取每像素的缩放比例。
- `runOnDrawFrame()`：运行在绘制帧上。
- `removecache()`：移除缓存。
- `removecache(OnCacheRemoveListener var1)`：移除缓存并提供缓存移除完成的监听器。
- `setCustomRenderer(CustomRenderer var1)`：设置自定义渲染器。
- `setPointToCenter(int var1, int var2)`：设置点到中心。
- `setMapTextZIndex(int var1)`：设置地图文本的Z索引。
- `setLoadOfflineData(boolean var1)`：设置加载离线数据状态。
- `getMapTextZIndex()`：获取地图文本的Z索引。
- `reloadMap()`：重新加载地图。
- `setRenderFps(int var1)`：设置渲染帧率。
- `setIndoorBuildingInfo(IndoorBuildingInfo var1)`：设置室内建筑物信息。
- `setAMapGestureListener(AMapGestureListener var1)`：设置地图手势监听器。
- `getZoomToSpanLevel(LatLng var1, LatLng var2)`：获取缩放到跨度的级别。
- `calculateZoomToSpanLevel(int var1, int var2, int var3, int var4, LatLng var5, LatLng var6)`：计算缩放到跨度的级别。
- `getInfoWindowAnimationManager()`：获取信息窗口动画管理器。
- `setMaskLayerParams(int var1, int var2, int var3, int var4, int var5, long var6)`：设置遮罩层参数。
- `setMaxZoomLevel(float var1)`：设置最大缩放级别。
- `setMinZoomLevel(float var1)`：设置最小缩放级别。
- `resetMinMaxZoomPreference()`：重置最小最大缩放偏好。
- `setMapStatusLimits(LatLngBounds var1)`：设置地图状态限制。
- `addCrossOverlay(CrossOverlayOptions var1)`：添加十字覆盖物。
- `addRouteOverlay()`：添加路线覆盖物。
- `getViewMatrix()`：获取视图矩阵。
- `getProjectionMatrix()`：获取投影矩阵。
- `setMapCustomEnable(boolean var1)`：已过时，不建议使用。
- `setCustomMapStylePath(String var1)`：已过时，不建议使用。
- `setCustomMapStyle(CustomMapStyleOptions var1)`：设置自定义地图样式。
- `setCustomMapStyleID(String var1)`：已过时，不建议使用。
- `setCustomTextureResourcePath(String var1)`：已过时，不建议使用。
- `setRenderMode(int var1)`：设置渲染模式。
- `getP20MapCenter(IPoint var1)`：获取P20地图中心。
- `getMapContentApprovalNumber()`：获取地图内容批准编号。
- `getSatelliteImageApprovalNumber()`：获取卫星图像批准编号。
- `getTerrainApprovalNumber()`：获取地形批准编号。
- `setMapLanguage(String var1)`：设置地图语言。
- `setRoadArrowEnable(boolean var1)`：设置道路箭头启用状态。
- `setNaviLabelEnable(boolean var1, int var2, int var3)`：设置导航标签启用状态。
- `setTouchPoiEnable(boolean var1)`：设置触摸POI启用状态。
- `isTouchPoiEnable()`：判断触摸POI是否启用。
- `getNativeMapController()`：获取原生地图控制器。
- `getNativeMapEngineID()`：获取原生地图引擎ID。
- `addOnCameraChangeListener(OnCameraChangeListener var1)`：添加相机变化监听器。
- `addOnMapClickListener(OnMapClickListener var1)`：添加地图点击监听器。
- `addOnMarkerDragListener(OnMarkerDragListener var1)`：添加标记拖拽监听器。
- `addOnMapLoadedListener(OnMapLoadedListener var1)`：添加地图加载完成监听器。
- `addOnMapTouchListener(OnMapTouchListener var1)`：添加地图触摸监听器。
- `addOnMarkerClickListener(OnMarkerClickListener var1)`：添加标记点击监听器。
- `addOnPolylineClickListener(OnPolylineClickListener var1)`：添加折线点击监听器。
- `addOnPOIClickListener(OnPOIClickListener var1)`：添加POI点击监听器。
- `addOnMapLongClickListener(OnMapLongClickListener var1)`：添加地图长按监听器。
- `addOnInfoWindowClickListener(OnInfoWindowClickListener var1)`：添加信息窗口点击监听器。
- `addOnIndoorBuildingActiveListener(OnIndoorBuildingActiveListener var1)`：添加室内建筑物激活监听器。
- `addOnMyLocationChangeListener(OnMyLocationChangeListener var1)`：添加我的位置变化监听器。
- `removeOnCameraChangeListener(OnCameraChangeListener var1)`：移除相机变化监听器。
- `removeOnMapClickListener(OnMapClickListener var1)`：移除地图点击监听器。
- `removeOnMarkerDragListener(OnMarkerDragListener var1)`：移除标记拖拽监听器。
- `removeOnMapLoadedListener(OnMapLoadedListener var1)`：移除地图加载完成监听器。
- `removeOnMapTouchListener(OnMapTouchListener var1)`：移除地图触摸监听器。
- `removeOnMarkerClickListener(OnMarkerClickListener var1)`：移除标记点击监听器。
- `removeOnPolylineClickListener(OnPolylineClickListener var1)`：移除折线点击监听器。
- `removeOnPOIClickListener(OnPOIClickListener var1)`：移除POI点击监听器。
- `removeOnMapLongClickListener(OnMapLongClickListener var1)`：移除地图长按监听器。
- `removeOnInfoWindowClickListener(OnInfoWindowClickListener var1)`：移除信息窗口点击监听器。
- `removeOnIndoorBuildingActiveListener(OnIndoorBuildingActiveListener var1)`：移除室内建筑物激活监听器。
- `removeOnMyLocationChangeListener(OnMyLocationChangeListener var1)`：移除我的位置变化监听器。
- `setWorldVectorMapStyle(String var1)`：设置世界矢量地图样式。
- `getCurrentStyle()`：获取当前样式。
- `accelerateNetworkInChinese(boolean var1)`：加速中文网络。
- `setConstructingRoadEnable(boolean var1)`：设置在建道路启用状态。
- `addAMapAppResourceListener(AMapAppResourceRequestListener var1)`：添加AMap应用资源监听器。
- `removeAMapAppResourceListener(AMapAppResourceRequestListener var1)`：移除AMap应用资源监听器。
- `hideBuildings(List<LatLng> var1)`：隐藏建筑物。
- `showHideBuildings(int var1)`：显示或隐藏建筑物。
- `addSignleClickInterceptorListener(SignleClickInterceptorListener var1)`：添加单次点击拦截器监听器。
- `removeSignleClickInterceptorListener(SignleClickInterceptorListener var1)`：移除单次点击拦截器监听器。
- `addPoiFilter(PoiFilter var1)`：添加POI过滤器。
- `removePoiFilter(String var1)`：移除POI过滤器。
- `clearPoiFilter()`：清除POI过滤器。
- `getOverlayBoundRect(String var1)`：获取覆盖物边界矩形。


###  `UiSettings` ：通过 `AMap` 对象的 `getUiSettings()` 方法获得 `UiSettings` 实例。控制高德地图的 UI 设置，如缩放控件、指南针、定位按钮、手势操作等。每个方法都有不同的用途，以下是这些 API 方法的详细介绍：

#### 1.1 `setScaleControlsEnabled(boolean var1)`
- **描述**: 设置是否显示地图的比例尺控件。
- **参数**:
    - `var1`: `true` 显示比例尺，`false` 隐藏比例尺。

#### 1.2 `setZoomControlsEnabled(boolean var1)`
- **描述**: 设置是否显示地图的缩放控件（加号和减号）。
- **参数**:
    - `var1`: `true` 显示缩放控件，`false` 隐藏缩放控件。

#### 1.3 `setCompassEnabled(boolean var1)`
- **描述**: 设置是否显示指南针。当地图被旋转时，指南针会出现。
- **参数**:
    - `var1`: `true` 显示指南针，`false` 隐藏指南针。

#### 1.4 `setMyLocationButtonEnabled(boolean var1)`
- **描述**: 设置是否显示定位按钮。定位按钮通常在用户当前位置被显示时出现。
- **参数**:
    - `var1`: `true` 显示定位按钮，`false` 隐藏定位按钮。

#### 1.5 `setScrollGesturesEnabled(boolean var1)`
- **描述**: 设置是否启用地图的滚动手势（拖动地图）。
- **参数**:
    - `var1`: `true` 启用滚动手势，`false` 禁用滚动手势。

#### 1.6 `setZoomGesturesEnabled(boolean var1)`
- **描述**: 设置是否启用地图的缩放手势（双指缩放）。
- **参数**:
    - `var1`: `true` 启用缩放手势，`false` 禁用缩放手势。

#### 1.7 `setTiltGesturesEnabled(boolean var1)`
- **描述**: 设置是否启用地图的倾斜手势（上下滑动两个手指）。
- **参数**:
    - `var1`: `true` 启用倾斜手势，`false` 禁用倾斜手势。

#### 1.8 `setRotateGesturesEnabled(boolean var1)`
- **描述**: 设置是否启用地图的旋转手势（旋转两个手指）。
- **参数**:
    - `var1`: `true` 启用旋转手势，`false` 禁用旋转手势。

#### 1.9 `setAllGesturesEnabled(boolean var1)`
- **描述**: 设置是否启用所有手势操作。
- **参数**:
    - `var1`: `true` 启用所有手势，`false` 禁用所有手势。

#### 1.10 `setLogoPosition(int var1)`
- **描述**: 设置地图底部高德地图的 logo 位置。
- **参数**:
    - `var1`: logo 的位置。可以是以下值：
        - `AMapOptions.LOGO_POSITION_BOTTOM_LEFT`: 左下角。
        - `AMapOptions.LOGO_POSITION_BOTTOM_CENTER`: 底部居中。
        - `AMapOptions.LOGO_POSITION_BOTTOM_RIGHT`: 右下角。

#### 1.11 `setZoomPosition(int var1)`
- **描述**: 设置缩放控件的位置。
- **参数**:
    - `var1`: 控件的位置。可用值：
        - `AMapOptions.ZOOM_POSITION_RIGHT_CENTER`: 地图右侧居中。
        - `AMapOptions.ZOOM_POSITION_RIGHT_BOTTOM`: 地图右下角。

#### 1.12 `getZoomPosition()`
- **描述**: 获取缩放控件的位置。
- **返回值**: 返回当前缩放控件的位置。

#### 1.13 `isScaleControlsEnabled()`
- **描述**: 判断地图的比例尺控件是否启用。
- **返回值**: `true` 表示比例尺控件已启用，`false` 表示未启用。

#### 1.14 `isZoomControlsEnabled()`
- **描述**: 判断地图的缩放控件是否启用。
- **返回值**: `true` 表示缩放控件已启用，`false` 表示未启用。

#### 1.15 `isCompassEnabled()`
- **描述**: 判断指南针是否启用。
- **返回值**: `true` 表示指南针已启用，`false` 表示未启用。

#### 1.16 `isMyLocationButtonEnabled()`
- **描述**: 判断定位按钮是否启用。
- **返回值**: `true` 表示定位按钮已启用，`false` 表示未启用。

#### 1.17 `isScrollGesturesEnabled()`
- **描述**: 判断滚动手势是否启用。
- **返回值**: `true` 表示滚动手势已启用，`false` 表示未启用。

#### 1.18 `isZoomGesturesEnabled()`
- **描述**: 判断缩放手势是否启用。
- **返回值**: `true` 表示缩放手势已启用，`false` 表示未启用。

#### 1.19 `isTiltGesturesEnabled()`
- **描述**: 判断倾斜手势是否启用。
- **返回值**: `true` 表示倾斜手势已启用，`false` 表示未启用。

#### 1.20 `isRotateGesturesEnabled()`
- **描述**: 判断旋转手势是否启用。
- **返回值**: `true` 表示旋转手势已启用，`false` 表示未启用。

#### 1.21 `getLogoPosition()`
- **描述**: 获取地图底部高德地图 logo 的位置。
- **返回值**: 返回 logo 的位置。

#### 1.22 `isIndoorSwitchEnabled()`
- **描述**: 判断室内地图开关是否启用。
- **返回值**: `true` 表示室内地图开关已启用，`false` 表示未启用。

#### 1.23 `setIndoorSwitchEnabled(boolean var1)`
- **描述**: 设置是否启用室内地图开关。
- **参数**:
    - `var1`: `true` 启用室内地图开关，`false` 禁用室内地图开关。

#### 1.24 `setLogoMarginRate(int var1, float var2)`
- **描述**: 设置地图 logo 在屏幕边距的位置比例。
- **参数**:
    - `var1`: 边距的方向。
    - `var2`: 边距比例。

#### 1.25 `getLogoMarginRate(int var1)`
- **描述**: 获取地图 logo 在指定方向上的边距比例。
- **参数**:
    - `var1`: 边距的方向。
- **返回值**: 返回 logo 的边距比例。

#### 1.26 `setLogoLeftMargin(int var1)`
- **描述**: 设置地图 logo 左边距。
- **参数**:
    - `var1`: 左边距的像素值。

#### 1.27 `setLogoBottomMargin(int var1)`
- **描述**: 设置地图 logo 底边距。
- **参数**:
    - `var1`: 底边距的像素值。

#### 1.28 `setZoomInByScreenCenter(boolean var1)`
- **描述**: 设置是否以屏幕中心点进行地图缩放。
- **参数**:
    - `var1`: `true` 以屏幕中心缩放，`false` 以手势点缩放。

#### 1.29 `setGestureScaleByMapCenter(boolean var1)`
- **描述**: 设置是否以地图中心进行手势缩放。
- **参数**:
    - `var1`: `true` 以地图中心缩放，`false` 以手势点缩放。

#### 1.30 `isGestureScaleByMapCenter()`
- **描述**: 判断手势缩放是否以地图中心为中心。
- **返回值**: `true` 表示以地图中心缩放，`false` 表示以手势点缩放。

#### 1.31 `setLogoEnable(boolean var1)`
- **描述**: 设置是否显示地图底部的 logo。
- **参数**:
    - `var1`: `true` 显示 logo，`false` 隐藏 logo。

#### 1.32 `isLogoEnable()`
- **描述**: 判断地图底部的 logo 是否显示。
- **返回值**: `true` 表示 logo 显示，`false` 表示 logo 隐藏。

#### 1.33.`setLogoCenter(int var1, int var2)` (Deprecated)
- **描述**: 设置 logo 的中心位置。该方法已废弃。
- **参数**:
    - `var1`: logo 的 x 坐标。
    - `var2`: logo 的 y 坐标。
