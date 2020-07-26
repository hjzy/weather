package com.animee.forecast.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.animee.forecast.R;
import com.animee.forecast.bean.Weather;
import com.animee.forecast.db.Database;
import com.google.gson.Gson;

import java.util.List;

/**
 *
 */
public class CityManagerAdapter extends BaseAdapter{
    Context context;
    List<Database>mDatas;

    public CityManagerAdapter(Context context, List<Database> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
//getView的重载参照实验指导书和https://blog.csdn.net/xiao_ziqiang/article/details/50812471
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //ViewHolder内部类作用就是缓存子View，让程序找起来更快。
        ViewHolder holder = null;
        //convertView为空则利用xml产生一个convertView，总是要有返回值的嘛
        if (convertView == null) {
           // LayoutInflater 的作用就是将XML布局文件实例化为相应的 View 对象，需要通过Activity.getLayoutInflater() 或 Context.getSystemService(Class) 来获取与当前Context已经关联且正确配置的标准LayoutInflater。
           /* 总共有三种方法来获取 LayoutInflater：
            Activity.getLayoutInflater()；
            Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
            LayoutInflater.from(context);
            事实上，这三种方法之间是有关联的：
            Activity.getLayoutInflater() 最终会调用到 PhoneWindow 的构造方法，实际上最终调用的就是方法三；
            而方法三最终会调用到方法二 Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ；
            */
            //参见https://www.jianshu.com/p/f0f3de2f63e3
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city_manager,null);
            //保存convertView
            holder = new ViewHolder(convertView);
            //setTag虽然写起来像加标签，不过这玩意儿实际上是用来存储额外的信息以便重用的
            //比如这里存储的就是viewholder
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        Database bean = mDatas.get(position);
        holder.cityTv.setText(bean.getCity());
        //直接使用fromJson从result这个Json提取List
        Weather weather = new Gson().fromJson(bean.getContent(), Weather.class);
//        获取今日天气情况
        //databean包含private String date;//日期
        //            private String dayPictureUrl;//天气图标的url，从百度接口获取天气图标
        //            private String nightPictureUrl;//夜晚的天气图标
        //            private String weather;//天气情况
        //            private String wind;//风速情况
        //            private String temperature;//温度
        //这五个对象
        Weather.ResultsBean.WeatherDataBean dataBean = weather.getResults().get(0).getWeather_data().get(0);
        holder.conTv.setText("天气:"+dataBean.getWeather());//天气
        String[] split = dataBean.getDate().split("：");//日期
        String todayTemp = split[1].replace(")", "");
        holder.currentTempTv.setText(todayTemp);//实时温度
        holder.windTv.setText(dataBean.getWind());//风力
        holder.tempRangeTv.setText(dataBean.getTemperature());//气温上下范围
        return convertView;
    }

    class ViewHolder{
        TextView cityTv,//城市
                 conTv,//天气
                 currentTempTv,//实时气温
                 windTv,//风
                 tempRangeTv;//温度范围
        public ViewHolder(View itemView){
            cityTv = itemView.findViewById(R.id.item_city_tv_city);
            conTv = itemView.findViewById(R.id.item_city_tv_condition);
            currentTempTv = itemView.findViewById(R.id.item_city_tv_temp);
            windTv = itemView.findViewById(R.id.item_city_wind);
            tempRangeTv = itemView.findViewById(R.id.item_city_temprange);

        }
    }
}
