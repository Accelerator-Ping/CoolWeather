package com.example.hp.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hp on 2017/11/11.
 */

public class Province extends DataSupport{

    private  int id;        //主键id
    private  String provinceName;  //省名字
    private  int provinceCode;     //省json编码

    public int getId() {
        return id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
