package com.example.hp.coolweather.gson;

/**
 * Created by hp on 2017/11/14.
 */

public class AQI {

    public AQICity city;

    public class AQICity{

        public String aqi;

        public String pm25;     //没有加注解连接
    }
}
