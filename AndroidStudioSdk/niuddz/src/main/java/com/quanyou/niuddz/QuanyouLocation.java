package com.quanyou.niuddz;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;


/**
 * Created by MrLovelyCbb on 2017/11/14.
 */

public class QuanyouLocation {

    public static AMapLocationClient mlocationClient;
    public static AMapLocationClientOption mLocationOption = null;

    // QuanyouActivity.Cbb_CallUnityFunc("Logic","RespQyLocations","1|"+latitude + "|" + longitude + "|" + province + city + district + street);
    // QuanyouActivity.Cbb_CallUnityFunc("Logic","RespQyLocations","2|"+"获取坐标失败...");

    public QuanyouLocation(Context context)
    {
        mlocationClient = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();

        //初始化定位参数
        resetLocationOption();
        //设置定位监听
        mlocationClient.setLocationListener(locationListener);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    public static void resetLocationOption()
    {
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        // 设置返回地址信息,默认为true
        mLocationOption.setNeedAddress(true);

        mLocationOption.setGpsFirst(false);             //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mLocationOption.setOnceLocationLatest(false);   //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mLocationOption.setSensorEnable(false);         //可选，设置是否使用传感器。默认是false
        mLocationOption.setWifiScan(true);              //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.setLocationCacheEnable(true);   //可选，设置是否使用缓存定位，默认为true
    }

    AMapLocationListener locationListener = new AMapLocationListener()
    {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if(aMapLocation != null)
            {
                StringBuffer sb = new StringBuffer();
                if(aMapLocation.getErrorCode() == 0)
                {
                    sb.append(1);
                    sb.append("|");
                    sb.append(aMapLocation.getLatitude());
                    sb.append("|");
                    sb.append(aMapLocation.getLongitude());
                    sb.append("|");
                    sb.append(aMapLocation.getProvince());
                    sb.append("|");
                    sb.append(aMapLocation.getCity());
                    sb.append("|");
                    sb.append(aMapLocation.getDistrict());
                    sb.append("|");
                    sb.append(aMapLocation.getStreet());

                    QuanyouActivity.Cbb_CallUnityFunc("Logic","RespQyLocations",sb.toString());
//                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                    aMapLocation.getLatitude();//获取纬度
//                    aMapLocation.getLongitude();//获取经度
//                    aMapLocation.getAccuracy();//获取精度信息
//                    aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                    aMapLocation.getCountry();//国家信息
//                    aMapLocation.getProvince();//省信息
//                    aMapLocation.getCity();//城市信息
//                    aMapLocation.getDistrict();//城区信息
//                    aMapLocation.getStreet();//街道信息
//                    aMapLocation.getStreetNum();//街道门牌号信息
//                    aMapLocation.getCityCode();//城市编码
//                    aMapLocation.getAdCode();//地区编码
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                    Date date = new Date(aMapLocation.getTime());
//                    df.format(date);//定位时间
                }else
                {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                    sb.append(2);
                    sb.append("|");
                    sb.append(aMapLocation.getErrorCode());
                    sb.append("|");
                    sb.append(aMapLocation.getErrorInfo());
                    QuanyouActivity.Cbb_CallUnityFunc("Logic","RespQyLocations",sb.toString());
                }
            }
        }
    };
}
