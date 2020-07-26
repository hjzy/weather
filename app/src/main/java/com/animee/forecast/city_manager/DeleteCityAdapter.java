package com.animee.forecast.city_manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.animee.forecast.R;

import java.util.List;

public class DeleteCityAdapter extends BaseAdapter{
    Context context;//上下文
    List<String>mDatas;//listview的数据源
    List<String>deleteCitys;//已经被删除的城市

    public DeleteCityAdapter(Context context, List<String> mDatas,List<String>deleteCitys) {
        this.context = context;
        this.mDatas = mDatas;
        this.deleteCitys = deleteCitys;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_deletecity,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final String city = mDatas.get(position);
        holder.tv.setText(city);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatas.remove(city);
                deleteCitys.add(city);
                notifyDataSetChanged();  //删除了提示适配器更新
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView tv;//删除图标
        ImageView iv;//城市名
        public ViewHolder(View itemView){
            tv = itemView.findViewById(R.id.item_delete_tv);
            iv = itemView.findViewById(R.id.item_delete_iv);
        }
    }
}
