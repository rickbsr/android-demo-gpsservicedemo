package com.codingbydumbbell.gpsservicedemo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.codingbydumbbell.gpsservicedemo.service.GpsService;
import com.codingbydumbbell.gpsservicedemo.tools.Utils;
import com.codingbydumbbell.gpsservicedemo.userInfo.UserInfo;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOCATION = 98;
    private Context context;

    private EditText editText_currentLng;
    private EditText editText_currentLat;
    private EditText editText_distance;
    private GpsReceiver gpsReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
    }

    // findViews
    private void findViews() {
        context = this;

        editText_currentLng = findViewById(R.id.editText_currentGPS_lng);
        editText_currentLat = findViewById(R.id.editText_currentGPS_lat);
        editText_distance = findViewById(R.id.editText_distance);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            openGpsService();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        gpsReceiver = new GpsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.codingbydumbbell.gpsservicedemo");
        registerReceiver(gpsReceiver, filter);

    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(gpsReceiver);
    }

    private boolean openGpsService() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
            return false;
        }

        startService(new Intent(context, GpsService.class));

        return false;
    }

    public void openLocationTracking(View view) {

        if (!isOnline() || !isLocOpen()) return;

        if (!Utils.isServiceRunning(context, GpsService.class.getName()))
            openGpsService();
    }

    public void closeLocationTracking(View view) {
        stopService(new Intent(context, GpsService.class));
    }

    public void setLocation(View view) {

        String lng = editText_currentLng.getText().toString();
        String lat = editText_currentLat.getText().toString();

        try {
            UserInfo.originLng = Double.parseDouble(lng);
            UserInfo.originLat = Double.parseDouble(lat);
        } catch (Exception e) {
        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public boolean isLocOpen() {
        LocationManager locMgr
                = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    private class GpsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editText_currentLng.setText(String.valueOf(UserInfo.currentLng));
                    editText_currentLat.setText(String.valueOf(UserInfo.currentLat));
                    editText_distance.setText(String.valueOf(UserInfo.distance));
                }
            });
        }
    }
}
