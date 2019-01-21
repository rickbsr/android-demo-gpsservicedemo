package com.codingbydumbbell.gpsservicedemo.tools;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class Utils {
    public static boolean isServiceRunning(Context context, String className) {

        System.out.println(className);
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(100);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}