package com.example.lihui.lutravel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.district.DistrictItem;
import com.amap.api.services.district.DistrictResult;
import com.amap.api.services.district.DistrictSearch;
import com.amap.api.services.district.DistrictSearchQuery;
import com.example.lihui.lutravel.bean.Marker;
import com.example.lihui.lutravel.util.AddressPickTask;
import com.example.lihui.lutravel.util.Util;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.util.ConvertUtils;

import static com.example.lihui.lutravel.R.id.city;
import static com.example.lihui.lutravel.util.SnackbarUtil.setSnackBarColor;
import static com.example.lihui.lutravel.util.SnackbarUtil.snackBarAddView;
import static com.example.lihui.lutravel.util.Util.getEditTextFocus;

public class AddJourney extends AppCompatActivity {

    private Context context;

    private Toolbar toolbar;
    private TextView complete;
    private EditText title;
    private EditText note;
    private LinearLayout cityLinearLayout;
    private TextView cityText;
    private LinearLayout dateLinearLayout;
    private TextView dateText;

    private String titleString;
    private String noteString;
    private String cityString;
    private String dateString;

    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        context = getApplicationContext();//全局获取上下文路径

        init();
        actionBar();
        pickerListener();
        submit();
    }

    //初始化
    public void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        complete = (TextView)findViewById(R.id.complete);
        title = (EditText) findViewById(R.id.title);
        note = (EditText) findViewById(R.id.note);
        cityLinearLayout = (LinearLayout) findViewById(city);
        cityText = (TextView)findViewById(R.id.city_text);
        dateLinearLayout = (LinearLayout) findViewById(R.id.date);
        dateText = (TextView)findViewById(R.id.date_text);
    }

    //标题栏
    public void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);//返回按钮
            actionbar.setTitle("添加行程");
        }
    }

    //位置、日期选择
    public void pickerListener(){
        //位置
        cityLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddressPicker();
            }
        });

        //日期
        dateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDate(view);
                onYearMonthDayPicker();
            }
        });
    }

    //位置选择弹窗
    public void onAddressPicker( ) {
        AddressPickTask task = new AddressPickTask(this);
        task.setHideCounty(true);
        task.setCallback(new AddressPickTask.Callback() {
            @Override
            public void onAddressInitFailed() {
                Util.showToast(context, "数据初始化失败");
            }

            @Override
            public void onAddressPicked(Province province, City city, County county) {
                searchDistrict(context, city.getAreaName());
                cityText.setText(city.getAreaName());
            }
        });
        task.execute();
    }

    //查询位置经纬度
    public void searchDistrict(Context context, String keyword){
        DistrictSearch search = new DistrictSearch(context);
        DistrictSearchQuery query = new DistrictSearchQuery();
        query.setKeywords(keyword);//传入关键字
        query.setShowBoundary(true);//是否返回边界值
        search.setQuery(query);
        search.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
            @Override
            public void onDistrictSearched(DistrictResult districtResult) {
                if (districtResult.getAMapException().getErrorCode() == AMapException.CODE_AMAP_SUCCESS) {
                    List<DistrictItem> district = districtResult.getDistrict();
                    DistrictItem currentDistrictItem = district.get(0);
                    LatLonPoint center = currentDistrictItem.getCenter();
                    latLng = new LatLng(center.getLatitude(), center.getLongitude());
                }
            }
        });//绑定监听器
        search.searchDistrictAsyn();//开始搜索
    }

    //日期选择弹窗
    public void onYearMonthDayPicker() {
        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = calendar.get(Calendar.MONTH) + 1;//当前月从0开始所以加一
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日

        final cn.qqtheme.framework.picker.DatePicker picker = new cn.qqtheme.framework.picker.DatePicker(this);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(this, 10));
        picker.setRangeEnd(year, month, day);
        picker.setRangeStart(2000, 1, 1);
        picker.setSelectedItem(year, month, day);
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new cn.qqtheme.framework.picker.DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                dateText.setText(year + "-" + month + "-" + day);
            }
        });
        picker.setOnWheelListener(new cn.qqtheme.framework.picker.DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
    }

    //原生日期控件
    public void showDate(View view) {
        //获取当前年月日
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//当前年
        int month = calendar.get(Calendar.MONTH);//当前月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//当前日
        //new一个日期选择对话框的对象,并设置默认显示时间为当前的年月日时间.
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateText.setText(year + "-" + (++month) + "-" + day);//月份是从0开始的,所以加1就是实际月份了.
            }
        }, year, month, day);
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //保存
    public void submit(){
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleString = title.getText().toString();
                noteString = note.getText().toString();
                cityString = cityText.getText().toString();
                dateString = dateText.getText().toString();

                hintKeyboard();

                if(titleString.equals("")){
                    title.setError("请输入标题");
                    getEditTextFocus(title);//获取焦点
                } else if(noteString.equals("")){
                    showSnackbar(view, "请记录你的感受");
                    getEditTextFocus(note);//获取焦点
                } else if(cityString.equals("")){
                    Util.showToast(context, "请选择地址");
                } else if(dateString.equals("")){
                    Util.showToast(context, "请选择时间");
                } else {
                    Marker marker = new Marker(latLng.latitude, latLng.longitude, cityString, titleString, noteString, dateString);
                    List<Marker> markerList = DataSupport.where("city == ?", cityString).find(Marker.class);//查询是否已存在
                    if(markerList.size() == 0){
                        if(marker.save()){
                            Util.showToast(context, "保存成功！");
                            Intent intent = new Intent();
                            intent.putExtra("title", titleString);
                            intent.putExtra("note", noteString);
                            intent.putExtra("city", cityString);
                            intent.putExtra("date", dateString);
                            intent.putExtra("latLng", latLng);
                            setResult(RESULT_OK, intent);

                            finish();
                        }else{
                            Util.showToast(context, "保存失败！");
                        }
                    }else{
                        Util.showToast(context, "目的地已存在，可前往编辑！");
                    }
                }
            }
        });
    }

    //关闭软键盘
    public void hintKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //提示弹出
    public void showSnackbar(View view, String text){
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        snackBarAddView(snackbar, R.layout.snackbar_icon, 0);//设置图标
        setSnackBarColor(snackbar, ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.black));//设置背景和文字颜色
        snackbar.show();
    }
}
