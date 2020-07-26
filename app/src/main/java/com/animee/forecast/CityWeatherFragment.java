package com.animee.forecast;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.animee.forecast.base.BaseFragment;
import com.animee.forecast.bean.Weather;
import com.animee.forecast.db.DBManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CityWeatherFragment extends BaseFragment implements View.OnClickListener {
    TextView tempTv,
            cityTv,
            conditionTv,
            windTv,
            tempRangeTv,
            dateTv,
            clothIndexTv2,
             carIndexTv2,
            coldIndexTv2,
             sportIndexTv2,
             rayIndexTv2,
            AqiTV, PM25Tv;
    ImageView dayIv, BingPicImg;
    LinearLayout futureLayout,air;
    ScrollView outLayout;
    String url1 = "http://api.map.baidu.com/telematics/v3/weather?location=";
    //String url2 = "&output=json&ak=vDddwKGSdL34ksjDGLyqlKq9QRjGFmpr";
    String url2 = "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";
    private List<Weather.ResultsBean.IndexBean> indexList;//存储解析的Index信息的List，该信息为当前城市最近四天的天气
    String city;//Fragment的当前城市
    private SharedPreferences pref;
    private int bgNum;
    RelativeLayout BING_PIC;

    // 换壁纸的函数
    public void exchangeBg() {
        pref = getActivity().getSharedPreferences("bg_pref", MODE_PRIVATE);
        bgNum = pref.getInt("bg", 2);
        switch (bgNum) {
            case 0:

                outLayout.setBackgroundResource(R.mipmap.bg);

                break;
            case 1:
                outLayout.setBackgroundResource(R.mipmap.bg2);
                break;
            case 2:
                outLayout.setBackgroundResource(R.mipmap.bg3);
                break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city_weather, container, false);
        initView(view);
        // exchangeBg();
        //loadBingPic();
//        可以通过activity传值获取到当前fragment加载的是哪个地方的天气情况
        //这个值是在MainActivity里用setArguments设置的。
        Bundle bundle = getArguments();
        city = bundle.getString("city");
        String url = url1 + city + url2;
//      调用父类获取数据的方法
        String time = loadTime();
        loadData(url);


        return view;
    }

//用于获取系统时间，在右上角显示最后刷新时间
    public String loadTime() {
        Calendar calendar = Calendar.getInstance();
        int TIME = 0;

//获取系统的日期
//年
        int year = calendar.get(Calendar.YEAR);
//月
        int month = calendar.get(Calendar.MONTH) + 1;
//日
        int day = calendar.get(Calendar.DAY_OF_MONTH);
//获取系统时间
//小时
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//分钟
        int minute = calendar.get(Calendar.MINUTE);
//秒
        int second = calendar.get(Calendar.SECOND);

        return "最后刷新于" + hour + ":" + minute + ":" + second;
    }

    //获取数据成功后的操作
    @Override
    public void onSuccess(String result) {
//        解析并展示数据
        parseShowData(result);
//         更新数据
        int i = DBManager.updateInfoByCity(city, result);
        if (i <= 0) {
//            更新数据库失败，说明没有这条城市信息，增加这个城市记录
            DBManager.addCityInfo(city, result);
        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
//        数据库当中查找上一次信息显示在Fragment当中
        String s = DBManager.queryInfoByCity(city);
        if (!TextUtils.isEmpty(s)) {
            parseShowData(s);
        }

    }

    private void parseShowData(String result) {
        //使用gson解析数据
        Weather weather = new Gson().fromJson(result, Weather.class);
        Weather.ResultsBean resultsBean = weather.getResults().get(0);
        //获取指数信息集合列表
        indexList = resultsBean.getIndex();
        //设置TextView
        dateTv.setText(loadTime());//日期
        cityTv.setText(resultsBean.getCurrentCity());//城市
        //获取今日天气情况
        Weather.ResultsBean.WeatherDataBean todayDataBean = resultsBean.getWeather_data().get(0);
        windTv.setText(todayDataBean.getWind());//风力
        tempRangeTv.setText(todayDataBean.getTemperature());//温度区间
        conditionTv.setText(todayDataBean.getWeather());//天气情况
        //获取实时天气温度情况，需要处理字符串
        //使用：分割json值"周二 11月05日 (实时：14℃)"获取到的split[1]为"14℃)"，需要将括号置换为空
        String[] split = todayDataBean.getDate().split("：");
        String todayTemp = split[1].replace(")", "");
        tempTv.setText(todayTemp);//实时天气
        //设置显示的天气情况图片
//        Glide.with(getActivity()).load(todayDataBean.getDayPictureUrl()).into(dayIv);
        //获取未来三天的天气情况，加载到layout当中
        List<Weather.ResultsBean.WeatherDataBean> futureList = resultsBean.getWeather_data();
        futureList.remove(0);//去掉今天的
        for (int i = 0; i < futureList.size(); i++) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_main_center, null);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            futureLayout.addView(itemView);
            TextView idateTv = itemView.findViewById(R.id.item_center_tv_date);//日期
            TextView iconTv = itemView.findViewById(R.id.item_center_tv_con);//天气
            TextView itemprangeTv = itemView.findViewById(R.id.item_center_tv_temp);//温度区间
            ImageView iIv = itemView.findViewById(R.id.item_center_iv);//天气图标
            //获取对应的位置的天气情况
            Weather.ResultsBean.WeatherDataBean dataBean = futureList.get(i);
            idateTv.setText(dataBean.getDate());
            iconTv.setText(dataBean.getWeather());
            itemprangeTv.setText(dataBean.getTemperature());
            Glide.with(getActivity()).load(dataBean.getDayPictureUrl()).into(iIv);
        }


        Weather.ResultsBean.IndexBean indexBean = indexList.get(0);
        String clouthmsg = "穿衣指数：" + indexBean.getZs() + "\n" + indexBean.getDes();
        clothIndexTv2.setText(clouthmsg);


        indexBean = indexList.get(1);
        String carmsg = "洗车指数：" + indexBean.getZs() + "\n" + indexBean.getDes();
        carIndexTv2.setText(carmsg);

        indexBean = indexList.get(2);
        String coolmsg = "感冒指数：" + indexBean.getZs() + "\n" + indexBean.getDes();
        coldIndexTv2.setText(coolmsg);

        indexBean = indexList.get(3);
        String sportmsg = "运动指数：" + indexBean.getZs() + "\n" + indexBean.getDes();
        sportIndexTv2.setText(sportmsg);

        indexBean = indexList.get(4);
        String raymsg = "紫外线指数：" + indexBean.getZs() + "\n" + indexBean.getDes();
        rayIndexTv2.setText(raymsg);

        String Aqimsg = resultsBean.getPm25();


        String Pm25msg = resultsBean.getPm25();
        if (!Pm25msg.isEmpty()) {
            PM25Tv.setText(Pm25msg);
            AqiTV.setText(Aqimsg);
        }
        else {
            air.setVisibility(View.GONE);
        }

    }

    private void initView(View view) {
//        用于初始化控件操作
        tempTv = view.findViewById(R.id.frag_tv_currenttemp);//当前天气情况的布局
        cityTv = view.findViewById(R.id.frag_tv_city);//城市
        conditionTv = view.findViewById(R.id.frag_tv_condition);//天气
        windTv = view.findViewById(R.id.frag_tv_wind);//风力
        tempRangeTv = view.findViewById(R.id.frag_tv_temprange);//温度区间
        dateTv = view.findViewById(R.id.frag_tv_date);//日期
        clothIndexTv2 = view.findViewById(R.id.wear_text);
        carIndexTv2 = view.findViewById(R.id.car_wash_text);
        coldIndexTv2 = view.findViewById(R.id.cool_text);
        sportIndexTv2 = view.findViewById(R.id.sport_text);
        rayIndexTv2 = view.findViewById(R.id.ray_text);
//        dayIv = view.findViewById(R.id.frag_iv_today);//今日天气图标
        futureLayout = view.findViewById(R.id.frag_center_layout);//未来天气布局
        outLayout = view.findViewById(R.id.out_layout);//主界面布局
        BING_PIC = view.findViewById(R.id.Bing_Pic);//换壁纸用的，最后也没有用到
        BingPicImg = view.findViewById(R.id.bing_pic_img);//换壁纸用的，Bing每日壁纸
        AqiTV = view.findViewById(R.id.aqi_text);//空气质量
        PM25Tv = view.findViewById(R.id.pm25_text);//pm2.5
      air =view.findViewById(R.id.air_quality);


    }


    @Override
    public void onClick(View v) {

    }
}
