package com.quanyou.xilqp;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * Created by MrLovelyCbb on 2017/11/14.
 */

public class QuanyouLocation {
    public static LocationClient mLocationClient;
    public static MyBDLocationListener mBDLocationListener;

    public QuanyouLocation(Context context)
    {
        mLocationClient = new LocationClient(context);
        mBDLocationListener = new MyBDLocationListener();
        mLocationClient.registerLocationListener(mBDLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

        // 需要地址信息，设置为其他任何值（string类型，且不能为null）时，都表示无地址信息。
//		option.setAddrType("all");
//		// 设置是否返回POI的电话和地址等详细信息。默认值为false，即不返回POI的电话和地址信息。
//		option.setPoiExtraInfo(true);
//		// 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
//		option.setProdName("定位我当前的位置");
//		// 打开GPS，使用gps前提是用户硬件打开gps。默认是不打开gps的。
//		option.setOpenGps(true);
//		// 定位的时间间隔，单位：ms
//		// 当所设的整数值大于等于1000（ms）时，定位SDK内部使用定时定位模式。
//		option.setScanSpan(50);
//		// 查询范围，默认值为500，即以当前定位位置为中心的半径大小。
//		option.setPoiDistance(500);
//		// 禁用启用缓存定位数据
//		option.disableCache(true);
//		// 坐标系类型，百度手机地图对外接口中的坐标系默认是bd09ll
//		option.setCoorType("bd09ll");
//		// 设置最多可返回的POI个数，默认值为3。由于POI查询比较耗费流量，设置最多返回的POI个数，以便节省流量。
//		option.setPoiNumber(3);
//		// 设置定位方式的优先级。
//		// 即使有GPS，而且可用，也仍旧会发起网络请求。这个选项适合对精确坐标不是特别敏感，但是希望得到位置描述的用户。
//		option.setServiceName("com.baidu.location.service_v2.9");
//		option.setPriority(LocationClientOption.GpsFirst);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

    }

    final class MyBDLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                String province = location.getProvince(); // 获取省份信息
                String city = location.getCity(); // 获取城市信息
                String district = location.getDistrict(); // 获取区县信息
                String street = location.getStreet();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

//                Log.i("MyBDLocationListener", "province = " + province);
//                Log.i("MyBDLocationListener", "city = " + city);
//                Log.i("MyBDLocationListener", "district = " + district);
//                Log.i("MyBDLocationListener", "street = " + street);
//                Log.i("MyBDLocationListener", "latitude = " + latitude);
//                Log.i("MyBDLocationListener", "longitude = " + longitude);
//                System.out.println("zx-------------location:"+Utils.location);

                QuanyouActivity.Cbb_CallUnityFunc("QyAndroidPlug","RespQyLocations","1|"+latitude + "|" + longitude + "|" + province + city + district + street);
            }else
            {
                QuanyouActivity.Cbb_CallUnityFunc("QyAndroidPlug","RespQyLocations","2|"+"获取坐标失败...");
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
}
