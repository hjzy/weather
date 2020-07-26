package com.animee.forecast.base;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 实现的用于http通信的类，使用XUtils后废弃该类
 */

public class HttpUtil {

    /**
     * 用 HttpURLConnection 发送请求
     * @param address
     * @param listener
     */
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try{
                    URL url = new URL(address);
                    // 1. 获取 HttpURLConnection 实例
                    connection = (HttpURLConnection) url.openConnection();
                    // 2. 设置请求方法
                    connection.setRequestMethod("GET");
                    // 3. 自由定制，如设置连接超时、读取超时等
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    // 4. 获取服务器返回的输入流
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!= null){
                        response.append(line);
                    }
                    if (listener != null){
                        // 回调 onFinish() 方法
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    if (listener != null){
                        // 回调 onError() 方法
                        listener.onError(e);
                    }
                }finally {
                    if (connection != null){
                        // 5.把 HTTP 连接关掉
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }

    /**
     * 用 OKHttp 发送请求
     * @param address
     * @param callback
     */
    public static void sendOKHttpRequest(String address, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    public interface HttpCallbackListener {
        void onFinish(String response);// 请求成功时调用
        void onError(Exception e);// 请求失败时调用
    }
}