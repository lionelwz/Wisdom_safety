package com.wf.wisdom_safety.ui.homepage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.squareup.picasso.Picasso;
import com.wf.util.PermissionUtil;
import com.wf.util.RxSubscriptionCollection;
import com.wf.util.enum_type.UserType;
import com.wf.wisdom_safety.R;
import com.wf.wisdom_safety.bean.inspect.InspectVo;
import com.wf.wisdom_safety.bean.inspect.PublicDevice;
import com.wf.wisdom_safety.bean.user.User;
import com.wf.wisdom_safety.model.inspect.InspectManager;
import com.wf.wisdom_safety.model.user.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;

import static android.content.ContentValues.TAG;
import static android.content.Context.SENSOR_SERVICE;

/**
 * 首页
 * Created by Lionel on 2017/9/13.
 */

public class HomepageFragment extends Fragment implements SensorEventListener, OnGetGeoCoderResultListener {

    @Inject
    InspectManager mInspectManager;
    @Inject
    RxSubscriptionCollection mRxSubscriptionCollection;
    @Inject
    UserManager mUserManager;
    @Inject
    Picasso mPicasso;

    @Bind(R.id.geocodekey)
    EditText geoText;
    @Bind(R.id.geocode)
    Button geoBtn;

    private Context mContext;

    /****地图相关***/
    private static final String[] PERMISSIONS_CONTACT = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE} ;
    private static final int REQUEST_CONTACTS = 1000;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;

    //信息显示布局
    @Bind(R.id.rl_marker)
    LinearLayout rl_marker;
    @Bind(R.id.inspect_name)
    TextView inspectNameText;
    @Bind(R.id.inspect_unit_name)
    TextView inspectUnitNameText;
    @Bind(R.id.inspect_addr)
    TextView inspectAddrText;
    @Bind(R.id.inspect_contact)
    TextView inspectContactText;
    @Bind(R.id.inspect_state)
    TextView inspectStateText;

    /**GEO相关**/
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdGreen = BitmapDescriptorFactory
            .fromResource(R.drawable.pin_green_solid);
    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdGreen_b1 = BitmapDescriptorFactory
            .fromResource(R.drawable.pin_green_solid_b1);

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdYellow = BitmapDescriptorFactory
            .fromResource(R.drawable.pin_yellow_solid);
    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdYellow_b1 = BitmapDescriptorFactory
            .fromResource(R.drawable.pin_yellow_solid_b1);

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdRed = BitmapDescriptorFactory
            .fromResource(R.drawable.pin_red_solid);
    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdRed_b1 = BitmapDescriptorFactory
            .fromResource(R.drawable.pin_red_solid_b1);

    private User user;

    public HomepageFragment() {

    }

    /**
     * 发起搜索
     *
     * @param view
     */

    @OnClick({R.id.geocode})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.geocode:
                String geoStr = geoText.getText().toString();
                if(geoStr != null && geoStr.length() > 0) {
                    mSearch.geocode(new GeoCodeOption().city("").address(geoStr));
                }
                break;
        }

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(mContext, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(12.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                getPointLocal();
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        DaggerHomepageComponent.create().inject(this);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        getPointLocal();
        Log.i(TAG, "onResume");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView");
        isFirstLoc = true;
        user = mUserManager.getUser();
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        mSensorManager = (SensorManager) this.getContext().getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;

        // 地图初始化
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //地图点击事件
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }
            @Override
            public void onMapClick(LatLng arg0) {
                rl_marker.setVisibility(View.GONE);
            }
        });

        //添加marker点击事件的监听
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                //从marker中获取info信息
                Bundle bundle = marker.getExtraInfo();
                InspectVo inspectVo = (InspectVo)bundle.getSerializable("inspectInfo");

                inspectNameText.setText(inspectVo.getName());
                inspectUnitNameText.setText(inspectVo.getUnitName());
                inspectAddrText.setText(inspectVo.getAddr());
                inspectContactText.setText(inspectVo.getContactPerson()+"/"+inspectVo.getContactPhone());
                inspectStateText.setText("正常-"+inspectVo.getNormalCount()+"； 故障-"+inspectVo.getFaultCount() + "； 损坏-"+inspectVo.getBrokenCount());
                rl_marker.setVisibility(View.VISIBLE);
                //Toast.makeText(mContext, inspectVo.getName(), Toast.LENGTH_LONG).show();
                return true;
            }
        });
        // 定位初始化
        mLocClient = new LocationClient(this.getContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        if (Build.VERSION.SDK_INT>=23) { showContacts(mMapView); }else { mLocClient.start(); }
        //mLocClient.start();


        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        ButterKnife.bind(this, view);
        return view;
    }

    private void showContacts(View v) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            requestContactsPermissions(v);
        } else {
            mLocClient.start();
        }
    }

    private void requestContactsPermissions(View v) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (HomepageFragment.this.shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_COARSE_LOCATION)
                || HomepageFragment.this.shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION)
                || HomepageFragment.this.shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || HomepageFragment.this.shouldShowRequestPermissionRationale(
                Manifest.permission.READ_PHONE_STATE)
                ) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying contacts permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(v, "permission_contacts_rationale",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HomepageFragment.this.requestPermissions( PERMISSIONS_CONTACT, REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            HomepageFragment.this.requestPermissions( PERMISSIONS_CONTACT, REQUEST_CONTACTS);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode==REQUEST_CONTACTS){
            if (PermissionUtil.verifyPermissions(grantResults)) {
                mLocClient.start();
            } else {
                Toast.makeText(mContext.getApplicationContext(),"授权不通过",Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onDestroyView() {
        // 退出时销毁定位
        /*mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mSearch.destroy();
        Log.i(TAG, "onDestroyView");
        ButterKnife.unbind(this);
        mRxSubscriptionCollection.cancelAll();*/
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        // 关闭定位图层
        // 退出时销毁定位
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();

        //回收资源
        bdGreen.recycle();
        bdGreen_b1.recycle();
        bdYellow.recycle();
        bdYellow_b1.recycle();
        bdRed.recycle();
        bdRed_b1.recycle();

        mMapView = null;

        mSearch.destroy();
        ButterKnife.unbind(this);
        mRxSubscriptionCollection.cancelAll();
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    private void getPointLocal() {
        mBaiduMap.clear();
        Subscription subscribe = mInspectManager.getBuildingsLocal().subscribe(new Observer<List<Map<String, Object>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Map<String, Object>> buildingsMarkers) {
                setBuildingsMarker(buildingsMarkers);
            }

        });
        mRxSubscriptionCollection.add(subscribe);

    }

    private void setBuildingsMarker(List<Map<String, Object>> buildingsMarkers) {

        if(buildingsMarkers != null) {
            for(Map<String, Object> map : buildingsMarkers) {
                Double lat = map.get("lat") == null ? null : Double.valueOf(map.get("lat").toString());
                Double lon = map.get("lon") == null ? null : Double.valueOf(map.get("lon").toString());
                Integer state = map.get("state") == null ? null : (int)Float.parseFloat(map.get("state").toString());
                BitmapDescriptor bitmap;
                if(lat != null && lon != null) {
                    //定义Maker坐标点
                    LatLng point = new LatLng(lat, lon);
                    ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
                    //构建Marker图标
                    if(state == null) {
                        state = 1;
                    }
                    if(state == 2) {
                        giflist.add(bdYellow);
                        giflist.add(bdYellow_b1);
                    } else if(state == 3 || state ==4 || state==5) {
                        giflist.add(bdRed);
                        giflist.add(bdRed_b1);
                    } else {
                        giflist.add(bdGreen);
                        giflist.add(bdGreen_b1);
                    }

                    //构建MarkerOption，用于在地图上添加Marker
                    MarkerOptions option = new MarkerOptions()
                            .position(point).icons(giflist).zIndex(0).period(10);

                    //在地图上添加Marker，并显示
                    Marker marker = (Marker)mBaiduMap.addOverlay(option);
                    //使用marker携带info信息，当点击事件的时候可以通过marker获得info信息
                    Bundle bundle = new Bundle();
                    //info必须实现序列化接口
                    bundle.putSerializable("inspectInfo", formatInspect(map));
                    marker.setExtraInfo(bundle);
                }
            }
        }
    }

    private InspectVo formatInspect(Map<String, Object> map) {
        InspectVo vo = new InspectVo();
        vo.setUuid(map.get("uuid") == null ? "" : map.get("uuid").toString());
        vo.setName(map.get("name") == null ? "" : map.get("name").toString());
        vo.setLat(map.get("lat") == null ? null : Double.valueOf(map.get("lat").toString()));
        vo.setLon(map.get("lon") == null ? null : Double.valueOf(map.get("lon").toString()));
        vo.setUnitName(map.get("unit_name") == null ? "" : map.get("unit_name").toString());
        vo.setAddr(map.get("addr") == null ? "" : map.get("addr").toString());
        vo.setContactPerson(map.get("contact_person") == null ? "" : map.get("contact_person").toString());
        vo.setContactPhone(map.get("contact_phone") == null ? "" : map.get("contact_phone").toString());
        vo.setNormalCount(map.get("normalCount") == null ? "0" : String.valueOf((int)Float.parseFloat(map.get("normalCount").toString())));
        vo.setFaultCount(map.get("faultCount") == null ? "0" : String.valueOf((int)Float.parseFloat(map.get("faultCount").toString())));
        vo.setBrokenCount(map.get("brokenCount") == null ? "0" : String.valueOf((int)Float.parseFloat(map.get("brokenCount").toString())));
        return vo;
    }
}
