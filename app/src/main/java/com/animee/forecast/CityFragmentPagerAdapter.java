package com.animee.forecast;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 实现ViewPager的数据刷新
 * https://blog.csdn.net/mq2856992713/article/details/100180030
 */
//通过构造获取List集合

public class CityFragmentPagerAdapter extends FragmentStatePagerAdapter{
    List<Fragment>fragmentList;
    public CityFragmentPagerAdapter(FragmentManager fm,List<Fragment>fragmentLis) {
        super(fm);
        this.fragmentList = fragmentLis;
    }
//设置每一个的内容
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }
//设置有多少内容
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    int childCount = 0;   //表示ViewPager包含的页数
//    当ViewPager的页数发生改变时，必须要重写两个函数
    @Override
    public void notifyDataSetChanged() {
        this.childCount = getCount();
        //提示适配器更新
        super.notifyDataSetChanged();
    }
//重写PageAdapter的getItemPosition()方法，返回POSITION_NONE实现数据刷新，不重写的话getItemPosition会直接返回-1，数据没有刷新
// public static final int POSITION_UNCHANGED = -1;
// public static final int POSITION_NONE = -2;
    @Override
    public int getItemPosition(@NonNull Object object) {
        if (childCount>0) {
            childCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
