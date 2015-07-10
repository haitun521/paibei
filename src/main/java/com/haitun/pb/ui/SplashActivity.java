package com.haitun.pb.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.haitun.pb.R;

public class SplashActivity extends Activity {
    public LocationClient mLocationClient = null;
    public BDLocationListener mMyLocationListener ;

    SharedPreferences sp =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        Fresco.initialize(getApplicationContext());
        sp= getSharedPreferences("config", MODE_PRIVATE);
        Handler handler = new Handler();
        handler.postDelayed(new splashHandler(), 2000);
        InitLocation();

    }

    private void InitLocation() {
        mMyLocationListener = new MyLocationListener();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(1000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public class splashHandler implements Runnable {

        @Override
        public void run() {

            Intent intent = new Intent();

            int id = sp.getInt("userid", 0);
            /**
             * 是否已经登录过
             */
            if (id == 0) {
                intent.setClass(SplashActivity.this, LoginActivity.class);
            } else {
                intent.setClass(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {

            String address=location.getAddrStr();
            String province=location.getProvince();
            String city=location.getCity();
            String distric=location.getDistrict();//获取区县信息
            SharedPreferences.Editor editor=  sp.edit();
            editor.putString("address",address);
            editor.putString("province",province);
            editor.putString("city",city);
            editor.putString("distric",distric);
            editor.commit();
        }


    }

    @Override
    protected void onStop() {
        mLocationClient.stop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.unRegisterLocationListener(mMyLocationListener);
        super.onDestroy();
    }
}
