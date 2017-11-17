package com.example.hp.coolweather.service;

/**
 * 自动更新天气
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.example.hp.coolweather.WeatherActivity;
import com.example.hp.coolweather.gson.Weather;
import com.example.hp.coolweather.util.HttpUtil;
import com.example.hp.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//服务启动时用
        updateWeather();        //更新天气
        updeteBingPic();        //更新背景图片

        //半小时更新一次
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 80*80*1000/2;
        long triggerAtTime = SystemClock.elapsedRealtime()+anHour;      //从系统开机到现在的时间 + 半小时的毫秒数
        Intent i = new Intent(this,AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi); //设置新任务,半小时后启动
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新背景 将更新后的数据储存到缓存中
     */
    private void updeteBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPic = response.body().string();
                SharedPreferences.Editor editor =PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
            }
        });
    }

    /**
     * 更新天气 将更新后的数据储存到缓存中
     */
    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);
        if(weatherString != null){//有缓存时解析出来所选城市  重新申请数据并缓存
            Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;

            String weatherUrl ="http://guolin.tech/api/weather?cityid="+weatherId+"&key=1bb570234e724030a1bd95faae10458d";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText); //重新解析返回的数据
                    if (weather != null && "ok".equals(weather.status)) {
                        //将解析的结果缓存到手机中
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
