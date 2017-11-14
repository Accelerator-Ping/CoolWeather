package com.example.hp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp on 2017/11/14.
 */

public class Now {

    @SerializedName("tmp")
    public String temeperature;

    @SerializedName("cond")
    public More more;

    public class More{

        @SerializedName("txt")
        public String info;

    }
}
