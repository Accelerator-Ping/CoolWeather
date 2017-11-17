package com.example.hp.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hp.coolweather.gson.Forecast;
import com.example.hp.coolweather.gson.Weather;
import com.example.hp.coolweather.util.HttpUtil;
import com.example.hp.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 从网上下载数据
 * 将数据解析
 * 将解析完的数据缓存
 *
 * 读取缓存中的数据
 * 将缓存数据展示在布局中
 */
public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherlayout;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;                   //必应背景图

    public SwipeRefreshLayout swipeRefreshLayout;   //刷新布局

    private String mWeatherId;                      //记录城市天气ID  全局变量 用于刷新

    public DrawerLayout drawerLayout;              //滑动菜单

    private Button navbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏优化
        if (Build.VERSION.SDK_INT >=21){//如果是Android 5.0以上的版本
            View decorView  =getWindow().getDecorView();        //获得标题栏实例
            decorView.setSystemUiVisibility(                    //将活动布局显示在状态栏上面
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);   //将状态栏设置成透明色
        }


        setContentView(R.layout.activity_weather);

        //初始化控件
        weatherlayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherlayout = findViewById(R.id.weather_layout);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);//设置刷新颜色
        drawerLayout = findViewById(R.id.drawable_layout);
        navbutton = findViewById(R.id.nav_button);

        //判断是否有Weather对象的缓存缓存
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {//有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);//解析
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);//展示
        } else {//没有缓存时去服务器查询数据
            mWeatherId = getIntent().getStringExtra("weather_id");//其实是得到市的天气序号  cityId->weather_id
            weatherlayout.setVisibility(View.INVISIBLE);//在查询时隐藏预报子布局
            requestWeather(mWeatherId);//查询天气
        }

        //监听刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);//重新查询天气
            }
        });

        //监听左侧按钮
        navbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);  //点击打开滑动菜单
            }
        });

        //判断有没有bingPic的缓存 有的话直接展示 没有则下载并缓存
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{//若缓存中没有图片url 则下载至缓存
            loadBingPic();
            //String picMy = getMyPci();得到我的图库中的随机图片
            //Glide.with(this).load(picMy).into(bingPicImg);
        }
    }

    /**
     * 根据天气id请求城市天气信息  请求完成后关闭刷新进度条
     */
    public void requestWeather(final String weatherId) {
        //组装请求URL
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=1bb570234e724030a1bd95faae10458d";
        mWeatherId=weatherId;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {//返回主线程 展示失败通知
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT);
                        swipeRefreshLayout.setRefreshing(false);        //隐藏刷新
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);//将下载缓存的JSON数据解析
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            //将解析的结果缓存到手机中
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);       //打印天气
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);        //隐藏刷新
                    }
                });
            }
        });
    }

    /**
     * 展示Weather中的数据
     */
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree =weather.now.temeperature+"℃";
        String weatherInfo =weather.now.more.info;
        degreeText.setText(degree);
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();

        //处理预报中的项目，将其加入视图中
        for(Forecast forecast: weather.forecastList){
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item,forecastLayout,false);//加载布局进去 参数 布局 item容器数组 false
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText  = view.findViewById(R.id.max_text);
            TextView minText  = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);             //将item布局加载到父布局中
        }

        if(weather.aqi !=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度:"+weather.suggestion.comfort.info;
        String carWash = "洗车指数:"+weather.suggestion.carWarh.info;
        String sport = "运动建议"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherlayout.setVisibility(View.VISIBLE);//预报栏在加载完后可见
    }

    /**
     * 加载Bing每日一图
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic/";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor =PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);//将服务器返回的bingPic存入缓存中
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
}

