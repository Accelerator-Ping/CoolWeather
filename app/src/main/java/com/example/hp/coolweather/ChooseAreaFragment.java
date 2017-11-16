package com.example.hp.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hp.coolweather.db.City;
import com.example.hp.coolweather.db.County;
import com.example.hp.coolweather.db.Province;
import com.example.hp.coolweather.util.HttpUtil;
import com.example.hp.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 *
 * 将省市县的数据存放在数据库中
 * Created by hp on 2017/11/11.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;    //省列表

    private List<City> cityList;    //市列表

    private List<County> countyList;    //县列表

    private Province   selectedPrivince;//保存选中的省

    private City   selectedCity;//保存选中的省

    private int currentLevel;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.choose_area,container,false);//加载视图 三个参数（布局，父类布局组，false）
        titleText =view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList); //在碎片中第一个参数就写为就写为getContext();
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//listView监听
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "等级为"+currentLevel);
                if(currentLevel == LEVEL_PROVINCE){
                    selectedPrivince = provinceList.get(i);  //获得选中的省的实例
                    queryCities();                           //下面展示所选省的市实例
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);          //获得选中市的实例
                    queryCounties();
                }else if(currentLevel ==LEVEL_COUNTY){
                    String weatherID = countyList.get(i).getWeatherId();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",weatherID);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTY){//返回上层
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();  //onActivityCreated先执行这一步 上面为监听，异步  作用为展示省的数据
    }



    /**
     * 查询所有的省，从数据库中，若数据库没有则从网上下载到数据库中
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//设置按钮隐藏
        provinceList = DataSupport.findAll(Province.class); //LitPal查询省
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());       //将省的名字储存进去
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);           //表示将列表移动到指定的Position处。（0）
            currentLevel = LEVEL_PROVINCE;
        }else{//若数据库中没有数据则到网络上下载
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询选中省中的所有市，没有到网上下
     */
    private void queryCities() {
        titleText.setText(selectedPrivince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);         //展示返回按钮
        cityList = DataSupport.where("provinceId = ?",String.valueOf(selectedPrivince.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else { //若数据库中没有数据则到网络上下载
            int provinceCode = selectedPrivince.getProvinceCode();//得到所属省的编码发送到网上查询数据
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询选中市中的所有县，没有则下载
     */
    private void queryCounties(){
        Log.d("001", "进入乡级查询");
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList =DataSupport.where("cityId = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int provinceCode = selectedPrivince.getProvinceCode();
            int cityCode     = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 从传入地址的类型从服务器上下载数据到数据库
     */
    private void queryFromServer(String address,final String type) {
        Log.d("contextLog", "url: "+address);
        showProgressDialog();//展示下载中进度条
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { //下载失败 关闭下载提示(ui 主线程)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {//根据类型下载数据
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);//将下载的数据做解析
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedPrivince.getId());//将下载的数据做解析
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if(result){//如果解析成功
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示正在加载对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
