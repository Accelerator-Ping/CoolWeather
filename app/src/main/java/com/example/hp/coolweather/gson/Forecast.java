package com.example.hp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 映射daily_forecase数组
 * Created by hp on 2017/11/14.
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class Temperature{

        public String max;

        public String min;
    }

    public class More{

        @SerializedName("txt_d")
        public String info;
    }
}
