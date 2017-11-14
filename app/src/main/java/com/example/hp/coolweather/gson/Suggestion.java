package com.example.hp.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hp on 2017/11/14.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWarh;

    public Sport sport;

    public class Comfort{

        @SerializedName("txt")
        public String info;
    }

    public class CarWash{

        @SerializedName("txt")
        public String info;
    }

    public class Sport{

        @SerializedName("txt")
        public String info;
    }
}
