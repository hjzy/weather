package com.animee.forecast.city_manager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.animee.forecast.R;
import com.animee.forecast.db.DBManager;
import com.animee.forecast.db.Database;

import java.util.ArrayList;
import java.util.List;

/**
 * 城市管理
 */
public class CityManagerActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView addIv,backIv,deleteIv;
    ListView cityLv;//城市列表
    List<Database>mDatas;  //列表的数据源
    private CityManagerAdapter adapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manager);
        addIv = findViewById(R.id.city_iv_add);
        backIv = findViewById(R.id.city_iv_back);
        deleteIv = findViewById(R.id.city_iv_delete);
        cityLv = findViewById(R.id.city_lv);
        mDatas = new ArrayList<>();
//        添加点击监听事件
        addIv.setOnClickListener(this);//讲道理老师给的实验代码抄一下还挺好用
        deleteIv.setOnClickListener(this);
        backIv.setOnClickListener(this);
//        设置适配器
        adapter = new CityManagerAdapter(this, mDatas);
        cityLv.setAdapter(adapter);
    }
/*  获取数据库当中真实数据源，添加到原有数据源当中，提示适配器更新*/
    //将数据库里存储的城市更新到适配器中
    @Override
    protected void onResume() {
        super.onResume();//调用onResume()将pause转化为running
        List<Database> list = DBManager.queryAllInfo();
        mDatas.clear();
        mDatas.addAll(list);
        //通知Activity更新ListView
        adapter.notifyDataSetChanged();
    }
//城市管理页面添加按钮的监听器
    //监听器，差不多照着实验5的监听器写的
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_iv_add:
                int cityCount = DBManager.getCityCount();
                //如果城市数量没有达到上限，跳转到城市搜索页面
                if (cityCount<5) {
                    Intent intent = new Intent(this, SearchCityActivity.class);
                    startActivity(intent);
                    //城市数量达到上限
                }else {
                    Toast.makeText(this,"存储城市数量已达上限，请删除后再增加",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.city_iv_back:
                finish();
                break;
            case R.id.city_iv_delete:
                //跳转到城市删除界面
                Intent intent1 = new Intent(this, DeleteCityActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
