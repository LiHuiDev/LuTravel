package com.example.lihui.lutravel;

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
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.example.lihui.lutravel.bean.Marker;
import com.example.lihui.lutravel.util.Util;

import org.litepal.crud.DataSupport;

import java.util.List;

import static com.example.lihui.lutravel.util.SnackbarUtil.setSnackBarColor;
import static com.example.lihui.lutravel.util.SnackbarUtil.snackBarAddView;
import static com.example.lihui.lutravel.util.Util.getEditTextFocus;

public class EditJourney extends AppCompatActivity {

    private Context context;

    private Toolbar toolbar;
    private TextView complete;
    private EditText titleUi;
    private EditText noteUi;
    private TextView cityText;
    private TextView dateText;

    private String titleString;
    private String noteString;
    private String cityString;
    private String dateString;

    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        context = getApplicationContext();//全局获取上下文路径

        init();
        actionBar();

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");
        latLng = intent.getParcelableExtra("position");
        titleUi.setText(title);
        noteUi.setText(note);

        submit();
    }

    //初始化
    public void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        complete = (TextView)findViewById(R.id.complete);
        titleUi = (EditText) findViewById(R.id.title);
        noteUi = (EditText) findViewById(R.id.note);
        cityText = (TextView)findViewById(R.id.city_text);
        dateText = (TextView)findViewById(R.id.date_text);
    }

    //标题栏
    public void actionBar(){
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null){
            actionbar.setDisplayHomeAsUpEnabled(true);//返回按钮
            actionbar.setTitle("编辑行程");
        }
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
                titleString = titleUi.getText().toString();
                noteString = noteUi.getText().toString();
//                cityString = cityText.getText().toString();
//                dateString = dateText.getText().toString();

                hintKeyboard();

                if(titleString.equals("")){
                    titleUi.setError("请输入标题");
                    getEditTextFocus(titleUi);//获取焦点
                } else if(noteString.equals("")){
                    showSnackbar(view, "请记录你的感受");
                    getEditTextFocus(noteUi);//获取焦点
                } else {
                    Marker marker = new Marker(latLng.latitude, latLng.longitude, cityString, titleString, noteString, dateString);
                    String latitude = String.valueOf(latLng.latitude);
                    String longitude = String.valueOf(latLng.longitude);
                    List<Marker> markerList = DataSupport.where("latitude == ? and longitude == ?", latitude, longitude).find(Marker.class);//查询是否已存在
                    if(marker.saveOrUpdate()){
                        Util.showToast(context, "修改成功！");
                        Intent intent = new Intent();
                        intent.putExtra("title", titleString);
                        intent.putExtra("note", noteString);
//                        intent.putExtra("city", cityString);
//                        intent.putExtra("date", dateString);
                        intent.putExtra("latLng", latLng);
                        setResult(RESULT_OK, intent);

                        finish();
                    }else{
                        Util.showToast(context, "修改失败！");
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
