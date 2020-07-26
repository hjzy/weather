package com.animee.forecast.city_manager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.animee.forecast.MainActivity;
import com.animee.forecast.R;
import com.animee.forecast.base.BaseActivity;
import com.animee.forecast.bean.Weather;
import com.google.gson.Gson;

public class SearchCityActivity extends BaseActivity implements View.OnClickListener{
    EditText searchEt;//搜索框
    ImageView submitIv;//提交按钮
    GridView searchGv;//热门城市
    String[]hotCitys = {"北京","上海","广州","深圳","珠海","佛山","南京","苏州","厦门","长沙","成都","福州",
            "杭州","武汉","青岛","西安","太原","沈阳","重庆","天津","南宁","桂林"};
    private ArrayAdapter<String> adapter;
    String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    String url2 = "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";
    //String url2 = "&output=json&ak=vDddwKGSdL34ksjDGLyqlKq9QRjGFmpr";
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_city);
        searchEt = findViewById(R.id.search_et);
        submitIv = findViewById(R.id.search_iv_submit);
        searchGv = findViewById(R.id.search_gv);
        submitIv.setOnClickListener(this);
//        设置适配器
        adapter = new ArrayAdapter<>(this, R.layout.item_hotcity, hotCitys);//使用arrayAdapter加载热门城市
        searchGv.setAdapter(adapter);
        setListener();
    }
/* 设置监听事件*/
    //点击热门城市后触发监听事件，调用loadData从百度地图获取天气信息
    private void setListener() {
        searchGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //根据点击事件对应的item的序号判断城市并查询。
                city = hotCitys[position];
                String url = url1+city+url2;
                loadData(url);
            }
        });
    }


    //点击搜索按钮触发的事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv_submit:
                city = searchEt.getText().toString();
                if (!TextUtils.isEmpty(city)) {
//                      判断是否能够找到这个城市
                        String url = url1+city+url2;
                        loadData(url);
                }else {
                    Toast.makeText(this,"输入内容不能为空！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
//get请求成功后调用xUtils的通用回调接口onSuccess处理请求返回值
    @Override
    public void onSuccess(String result) {
        //从result中获取天气情况，result的格式参见WeatherBean
        Weather weather = new Gson().fromJson(result, Weather.class);
        //如果返回值中error为0
        if (weather.getError()==0) {
            Intent intent = new Intent(this, MainActivity.class);
            //在Activity上下文之外启动Activity需要给Intent设置FLAG_ACTIVITY_NEW_TASK标志，不然会报异常。
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("city",city);
            startActivity(intent);
        }else{
            Toast.makeText(this,"暂时未收入此城市天气信息...",Toast.LENGTH_SHORT).show();
        }
    }
}
