package com.example.hp.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hp on 2017/11/11.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;      //县名
    private String weatherId;       //县对应天气Id
    private int cityId;             //县所属城市Id

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getId() {
        return id;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public int getCityId() {
        return cityId;
    }
}
