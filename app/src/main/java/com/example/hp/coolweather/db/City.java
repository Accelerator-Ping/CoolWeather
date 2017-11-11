package com.example.hp.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hp on 2017/11/11.
 */

public class City extends DataSupport {

    private int id;             //主键id
    private String cityName;    //城市名字
    private int cityCode;       //城市代码
    private int provinceId;     //所属省的id

    public int getId() {
        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
