package com.codingbydumbbell.gpsservicedemo.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

public class Utils {
    public static boolean isServiceRunning(Context context, String className) {
        List<ActivityManager.RunningServiceInfo> serviceList =
                ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE);
        if (serviceList.size() <= 0) return false; // 代表沒有正在使用的 service
        for (int i = 0; i < serviceList.size(); i++)
            if (serviceList.get(i).service.getClassName().equals(className)) return true;
        return false;
    }

    // 判斷網路是否開啟
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    // 判斷定位是否開啟
    public static boolean isLocOpen(Context context) {
        LocationManager locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }
}