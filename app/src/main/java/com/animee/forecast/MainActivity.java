package com.animee.forecast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.animee.forecast.base.HttpUtil;
import com.animee.forecast.city_manager.CityManagerActivity;
import com.animee.forecast.city_manager.CityManagerAdapter;
import com.animee.forecast.db.DBManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.view.View;

import static org.xutils.common.util.IOUtil.copy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView addCityIv,//用于添加城市的按钮ImageView
            moreIv,//用于展开更多功能的按钮ImageView
            BingPicImg;//导航栏小圆点Layout
    LinearLayout pointLayout;//用于将Bing每日图片加载到Layout的ImageView
    LinearLayout frag_layout;//未来天气、生活指数、空气质量指数等的布局
    RelativeLayout outLayout;//用于展示当前城市天气的布局
    RelativeLayout BING_PIC;

    ScrollView out_layout;

    Bitmap bitmap = null;//存储壁纸的bitmap
    InputStream in = null;
    BufferedOutputStream out = null;

    ViewPager mainVp;//界面Viewpager
    //    ViewPager的数据源
    List<Fragment> fragmentList;
    //    表示需要显示的城市的集合
    List<String> cityList;
    //    表示ViewPager的页数指数器显示集合
    List<ImageView> imgList;
    private CityFragmentPagerAdapter adapter;
    private SharedPreferences pref;//存储壁纸信息的SharedPreferences
    private int bgNum;//壁纸序号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addCityIv = findViewById(R.id.main_iv_add);
        moreIv = findViewById(R.id.main_iv_more);
        pointLayout = findViewById(R.id.main_layout_point);
        outLayout = findViewById(R.id.main_out_layout);
        // BING_PIC=findViewById(R.id.Bing_Pic);
        BingPicImg = findViewById(R.id.bing_pic_img);
        out_layout = findViewById(R.id.out_layout);
        frag_layout = findViewById(R.id.frag_layout);


        mainVp = findViewById(R.id.main_vp);
//        添加点击事件
        addCityIv.setOnClickListener(this);
        moreIv.setOnClickListener(this);

        fragmentList = new ArrayList<>();
        cityList = DBManager.queryAllCityName();  //获取数据库包含的城市信息列表
        imgList = new ArrayList<>();

        if (cityList.size() == 0) {
            cityList.add("桂林");
        }
        /* 因为可能搜索界面点击跳转此界面，会传值，所以此处获取一下*/
        try {
            Intent intent = getIntent();
            String city = intent.getStringExtra("city");
            if (!cityList.contains(city) && !TextUtils.isEmpty(city)) {
                cityList.add(city);
            }
        } catch (Exception e) {
            Log.i("animee", "程序出现问题了！！");
        }
//        初始化ViewPager页面的方法
        initPager();
        adapter = new CityFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mainVp.setAdapter(adapter);
//        创建小圆点指示器
        initPoint();
//        设置最后一个城市信息
        mainVp.setCurrentItem(fragmentList.size() - 1);
//        设置ViewPager页面监听
        setPagerListener();
        exchangeBg();
        //loadBingPic();

    }


    //从Bing获取图片作为壁纸
    public void loadBingPic() {

        String requestBingPic = "http://guolin.tech/api/bing_pic";//这个接口会返回一个图片的url
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();

                //将图片转化为bitmap
                try {
                    in = new BufferedInputStream(new URL(bingPic).openStream(), 1920 * 1080);
                    final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                    out = new BufferedOutputStream(dataStream, 1920 * 1080);
                    copy(in, out);
                    out.flush();
                    byte[] data = dataStream.toByteArray();
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    data = null;
                    //由于获取到的是1920x1080的横屏图片，需要裁剪以适应竖屏
                   int  bitmapHeigh=bitmap.getHeight();
                   int  ax=bitmap.getHeight();
                   int bitmapWidth=ax/16*9;
                   int x=bitmapWidth/3;
                    bitmap = Bitmap.createBitmap(bitmap, x, 0, bitmapWidth, bitmapHeigh);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View view;
                        view = LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_city_weather, null);
                        BING_PIC = view.findViewById(R.id.Bing_Pic);
                        //使用Glide加载图片到outLayout，作为背景
                        Glide.with(MainActivity.this)
                                .asBitmap()
                                .load(bingPic)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                                        Drawable drawable = new BitmapDrawable(resource);
                                        //outLayout.setBackground(drawable);
                                         //BING_PIC.setBackground(drawable);
                                    }
                                });
                        Drawable drawable = new BitmapDrawable(bitmap);
                        outLayout.setBackground(drawable);
                       // BING_PIC.setBackground(drawable);

//                              View view;
//                              view = LayoutInflater.from(MainActivity.this).inflate(R.layout.fragment_city_weather,null);
//                                Bing_Pic=view.findViewById(R.id.bing_pic_img);
//                                Bing_Pic.setImageBitmap(bitmap);
                        //  Glide.with(MainActivity.this).load(bingPic).into(Bing_Pic);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }


    //换壁纸的函数
    public void exchangeBg() {
        pref = getSharedPreferences("bg_pref", MODE_PRIVATE);
        bgNum = pref.getInt("bg", 2);
        switch (bgNum) {
            case 0:
                outLayout.setBackgroundResource(R.mipmap.bg4);
                break;
            case 1:
                outLayout.setBackgroundResource(R.mipmap.bg6);
                break;
            case 2:
                outLayout.setBackgroundResource(R.mipmap.bg5);
                break;
            case 3:
                loadBingPic();
                break;
        }

    }
    // 页面监听器设置，用于页面滑动切换
    private void setPagerListener() {
        /* 设置监听事件*/
        mainVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < imgList.size(); i++) {
                    imgList.get(i).setImageResource(R.mipmap.a1);
                }
                imgList.get(position).setImageResource(R.mipmap.a2);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
    //初始化导航栏小圆点
    private void initPoint() {
//        创建小圆点 ViewPager页面指示器的函数
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView pIv = new ImageView(this);
            pIv.setImageResource(R.mipmap.a1);
            pIv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pIv.getLayoutParams();
            lp.setMargins(0, 0, 20, 0);
            imgList.add(pIv);
            pointLayout.addView(pIv);
        }
        imgList.get(imgList.size() - 1).setImageResource(R.mipmap.a2);


    }

    private void initPager() {
        /* 创建Fragment对象，添加到ViewPager数据源当中*/
        for (int i = 0; i < cityList.size(); i++) {
            CityWeatherFragment cwFrag = new CityWeatherFragment();
            //绝了，怎么这么多要注意的
//             Activity重新创建时，会重新构建它所管理的Fragment，
//            根据Android文档说明，当一个fragment重新创建的时候，系统会再次调用 Fragment中的默认构造函数。
            //注意是默认的，覆写的不算
//            原先的Fragment的字段值将会全部丢失，
//            但是通过Fragment.setArguments(Bundle bundle)方法设置的bundle会保留下来。
//            所以尽量使用Fragment.setArguments(Bundle bundle)方式来传递参数
            //而且还必须给出默认的无参构造函数
            //https://blog.csdn.net/tu_bingbing/article/details/24143249
            //https://blog.csdn.net/Small_Lee/article/details/50553881?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.edu_weight&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-2.edu_weight
            Bundle bundle = new Bundle();
            bundle.putString("city", cityList.get(i));
            cwFrag.setArguments(bundle);
            fragmentList.add(cwFrag);
        }
    }
//跳转到更多界面和
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.main_iv_add:
                intent.setClass(this, CityManagerActivity.class);
                break;
            case R.id.main_iv_more:
                intent.setClass(this, MoreActivity.class);
                break;
        }
        startActivity(intent);
    }

    /* 当页面重写加载时会调用的函数，这个函数在页面获取焦点之前进行调用，此处完成ViewPager页数的更新*/
    @Override
    protected void onRestart() {
        super.onRestart();
//        获取数据库当中还剩下的城市集合
        List<String> list = DBManager.queryAllCityName();
        if (list.size() == 0) {
            list.add("桂林");
        }
        cityList.clear();    //重写加载之前，清空原本数据源
        cityList.addAll(list);
//        剩余城市也要创建对应的fragment页面
        fragmentList.clear();
        initPager();
        adapter.notifyDataSetChanged();
//        页面数量发生改变，指示器的数量也会发生变化，重写设置添加指示器
        imgList.clear();
        pointLayout.removeAllViews();   //将布局当中所有元素全部移除
        initPoint();
        //设定数据库中的最后一个城市为当前页面
        mainVp.setCurrentItem(fragmentList.size() - 1);
        // loadBingPic();
    }
}
