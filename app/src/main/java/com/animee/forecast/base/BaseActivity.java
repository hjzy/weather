package com.animee.forecast.base;

import android.support.v7.app.AppCompatActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 声明回调方法，使用xUtils请求数据
 */
public class BaseActivity extends AppCompatActivity implements Callback.CommonCallback<String>{
//使用XUtils进行请求，使用参见Github:https://github.com/wyouflf/xUtils3
    public void loadData(String url){
        RequestParams params = new RequestParams(url);
        x.http().get(params,this);
    }
    @Override
    public void onSuccess(String result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }
}
