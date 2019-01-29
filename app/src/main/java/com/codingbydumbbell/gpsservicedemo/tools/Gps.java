package com.codingbydumbbell.gpsservicedemo.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Gps {

    private LocationManager locationManager;
    private Location location;

    @SuppressLint("MissingPermission")
    public Gps(Context context) {
        this.locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        this.location = locationManager.getLastKnownLocation(getProvider());
        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
    }

    // 获取Location Provider
    private String getProvider() {

        // 构建位置查询条件
        Criteria criteria = new Criteria();

        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);

        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);

        // 是否允许付费：是
        criteria.setCostAllowed(true);

        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        // 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
        return locationManager.getBestProvider(criteria, true);
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location l) {
            // 當位置發生改變
            if (l != null) location = l;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle bundle) {
            // 當用戶狀態發生改變
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 當用戶開啟調用
            @SuppressLint("MissingPermission") Location l = locationManager.getLastKnownLocation(provider);
            if (l != null) {
                location = l;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // 當用戶關閉調用
            location = null;
        }
    };

    public Location getLocation() {
        return location;
    }
}
