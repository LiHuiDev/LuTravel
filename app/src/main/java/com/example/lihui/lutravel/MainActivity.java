package com.example.lihui.lutravel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.example.lihui.lutravel.bean.Marker;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private MapView mapView;
    private AMap aMap;
    private List<DistrictItem> list = new ArrayList<>();

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        style();
        control();
        zoomTo(5);
        getMarkerDate();
        addJourney();
        markerClickListener();
//        listener();
//        draw();
    }

    //地图样式设置
    public void style(){
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);

//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）

        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));//设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。
        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
        myLocationStyle.anchor((float) 0.5, (float) 0.5);//设置定位蓝点图标的锚点方法（x, y）。
        myLocationStyle.strokeColor(STROKE_COLOR);//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.strokeWidth(5);//自定义精度范围的圆形边框宽度
        myLocationStyle.radiusFillColor(FILL_COLOR);//设置定位蓝点精度圆圈的填充颜色的方法。

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
    }

    //控件交互
    public void control(){
        //在Activity页面调用startActvity启动离线地图组件
//        startActivity(new Intent(this.getApplicationContext(), com.amap.api.maps.offlinemap.OfflineMapActivity.class));

        UiSettings mUiSettings;//定义一个UiSettings对象
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);//缩放按钮，默认打开
        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

//        mUiSettings.setZoomGesturesEnabled(false);//缩放手势关闭
        mUiSettings.setRotateGesturesEnabled(false);//旋转手势关闭
        mUiSettings.setTiltGesturesEnabled(false);//控制倾斜手势关闭
    }

    //设置希望展示的地图缩放级别，地图的缩放级别一共分为 17 级，从 3 到 19。数字越大，展示的图面信息越精细。
    public void zoomTo(int level){
        aMap.moveCamera(CameraUpdateFactory.zoomTo(level));
    }

    //改变地图的中心点,参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
    public void changeCentre(LatLng latLng, int level, int anglePitch, int yawAngle){
        CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, level, anglePitch, yawAngle));
    }

    //
    public void listener(){
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                //从location对象中获取经纬度信息，地址描述信息，建议拿到位置之后调用逆地理编码接口获取（获取地址描述数据章节有介绍）

            }
        });

        //通过aMap对象设置定位数据源的监听
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {

            }

            @Override
            public void deactivate() {

            }
        });
    }

    //全部标记
    public void getMarkerDate(){
        Marker marker;
        Double latitude;//纬度
        Double longitude;//精度
        LatLng latLng;
        String title;
        String note;

        List<Marker> markers = DataSupport.findAll(Marker.class);
        int size = markers.size();

        for(int i = 0; i < size; i++){
            marker = markers.get(i);
            latitude = marker.getLatitude();
            longitude = marker.getLongitude();
            latLng = new LatLng(latitude, longitude);
            title = marker.getTitle();
            note = marker.getNote();

            addMarker(latLng, title, note);
        }
    }

    //添加标记点
    public void addMarker(LatLng latLng, String title, String note){
        //默认样式点
//        aMap.addMarker(new MarkerOptions().position(latLng).title(city).snippet(remarks).alpha(alpha));

        //自定义样式点
        aMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_64))
                .position(latLng)
                .title(title)
                .snippet(note)
                .alpha((float)0.8));

        //自定义信息窗口
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(com.amap.api.maps.model.Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(com.amap.api.maps.model.Marker marker) {
                View infoContent = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
                render(marker, infoContent);
                return infoContent;
            }
        });
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(com.amap.api.maps.model.Marker marker, View view) {

//        ((ImageView) view.findViewById(R.id.badge)).setImageResource(R.drawable.location_marker);

        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }

        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

    public void markerClickListener(){
        // 定义 Marker 点击事件监听
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.amap.api.maps.model.Marker marker) {
                if(marker.isInfoWindowShown()){
                    marker.hideInfoWindow();//隐藏信息窗口
                } else {
                    marker.showInfoWindow();//显示信息窗口
                }
                return true;
            }
        });

        aMap.setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(com.amap.api.maps.model.Marker marker) {
                Intent intent = new Intent(context, EditJourney.class);
                intent.putExtra("position", marker.getPosition());
                intent.putExtra("title", marker.getTitle());
                intent.putExtra("note", marker.getSnippet());
//                intent.putExtra("city", );
//                intent.putExtra("date", );
                startActivityForResult(intent, 2);
            }
        });
    }

//    //省分边界颜色绘制
    public void draw(){
        DistrictSearch search = new DistrictSearch(context);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords("江西省");//传入关键字
        query.setShowBoundary(true);//是否返回边界值
        search.setQuery(query);
        search.searchDistrictAsyn();//开始搜索
        search.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
            @Override
            public void onDistrictSearched(DistrictResult districtResult) {
                if(districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS){
                    list = districtResult.getDistrict();
                }
                list.get(0).districtBoundary();
                Log.i("查询结果", list.get(0).districtBoundary().toString());
//                Log.i("查询结果", list.toString());
            }
        });//绑定监听器
    }

    //添加行程
    public void addJourney(){
        FloatingActionButton addJourney = (FloatingActionButton) findViewById(R.id.add_journey);
        addJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddJourney.class);
                startActivityForResult(intent, 1);
//                startActivity(intent);
            }
        });
    }

    //接收activity返回数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    String title = data.getStringExtra("title");
                    String note = data.getStringExtra("note");
                    String city = data.getStringExtra("city");
                    String date = data.getStringExtra("date");
                    LatLng latLng = data.getParcelableExtra("latLng");

                    if(latLng != null){
                        addMarker(latLng, title, note);
//                        changeCentre(latLng, 5, 0, 0);//移动到中心点
                    }
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    String title = data.getStringExtra("title");
                    String note = data.getStringExtra("note");
//                    String city = data.getStringExtra("city");
//                    String date = data.getStringExtra("date");
                    LatLng latLng = data.getParcelableExtra("latLng");

                    if(latLng != null){
                        addMarker(latLng, title, note);
//                        changeCentre(latLng, 5, 0, 0);//移动到中心点
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }
}
