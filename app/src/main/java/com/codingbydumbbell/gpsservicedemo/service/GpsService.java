package com.codingbydumbbell.gpsservicedemo.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.codingbydumbbell.gpsservicedemo.MainActivity;
import com.codingbydumbbell.gpsservicedemo.R;
import com.codingbydumbbell.gpsservicedemo.tools.Gps;
import com.codingbydumbbell.gpsservicedemo.userInfo.UserInfo;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class GpsService extends Service {

    private static final String TAG = GpsService.class.getSimpleName();
    private static final int NotifyID = 98;
    private boolean isTrackingGPS;

    public GpsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isTrackingGPS = true;
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");

        // 建立常駐通知欄位(提高優先級, 避免 service 被殺死)
        Notification.Builder builder =
                new Notification.Builder(this.getApplicationContext());  // 建議取得應用層級的 context

        // 設定通知的 Intent
        Intent nfIntent = new Intent(this, MainActivity.class);

        // 建立常駐通知樣式
        builder.setContentIntent(PendingIntent
                .getActivity(this, 0, nfIntent, 0)) // 設置PendingIntent
                .setContentTitle("GPS Demo") // 設置下拉列表裏的標題
                .setContentText("定位追蹤中") // 設置上下文內容
                .setSmallIcon(R.mipmap.ic_launcher) // 設置狀態欄內的小圖標
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 設置下拉列表中的圖標(大圖標)
                .setWhen(new Date().getTime()); // 設置該通知發生的時間

        // 獲取構建好的 Notification 物件
        Notification notification = builder.build();

        // 設置為默認的聲音
        notification.defaults = Notification.DEFAULT_SOUND;

        // 開啟常駐通知
        startForeground(NotifyID, notification);

        final Gps gps = new Gps(this);

        new Thread(new Runnable() {
            @Override
            public void run() {

                int i = 0;

                while (isTrackingGPS) {
                    Location loc = gps.getLocation();

                    Log.d(TAG, "lng: " + loc.getLongitude());
                    Log.d(TAG, "lat: " + loc.getLatitude());

                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (loc == null) continue;

                    float[] f = new float[1];

                    if (UserInfo.originLat != 0 && UserInfo.originLng != 0) {
                        Location.distanceBetween(UserInfo.originLat, UserInfo.originLng, loc.getLatitude(), loc.getLongitude(), f);
                        Log.d(TAG, "distance: " + f[0]);
                    }

                    String str = String.format("record-%d", i = i++ % 5);

                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(UserInfo.userUid)
                            .child("LngLat")
                            .child(str)
                            .setValue(loc.getLongitude() + "," + loc.getLatitude());

                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(UserInfo.userUid)
                            .child("LngLat")
                            .child("now")
                            .setValue(loc.getLongitude() + "," + loc.getLatitude());

                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(UserInfo.userUid)
                            .child("LngLat")
                            .child("distance")
                            .setValue(f[0]);
                }
            }
        }).start();
        return START_STICKY;  // 當 service 被殺死時, 自動重啟
    }

    @Override
    public void onDestroy() {
        isTrackingGPS = false;
        stopForeground(true);
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
