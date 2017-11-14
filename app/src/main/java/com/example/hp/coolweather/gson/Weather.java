package com.example.hp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 整合其他的GSON映射类
 * Created by hp on 2017/11/14.
 */

public class Weather {

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
