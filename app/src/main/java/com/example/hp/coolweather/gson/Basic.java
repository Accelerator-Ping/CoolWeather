package com.example.hp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 用GSON解析json数据
 * Created by hp on 2017/11/14.
 */

public class Basic {

    @SerializedName("city") //JSON中的一些字段不适合用来做java字段  所以用注解建立映射关系
    public  String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{

        @SerializedName("loc")
        public String updateTime;

    }
}
