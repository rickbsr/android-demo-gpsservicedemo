package com.codingbydumbbell.gpsservicedemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;

import com.codingbydumbbell.gpsservicedemo.service.GpsService;
import com.codingbydumbbell.gpsservicedemo.tools.Utils;
import com.codingbydumbbell.gpsservicedemo.userInfo.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION = 11;
    private Context context;
    private EditText editText_currentLng;
    private EditText editText_currentLat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        findViews();

        FirebaseDatabase.getInstance().getReference("users")
                .child("LngLat")
                .child("now")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String[] loc = dataSnapshot.getValue().toString().split(",");
                            editText_currentLng.setText(loc[0]);
                            editText_currentLat.setText(loc[1]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    // findViews
    private void findViews() {
        editText_currentLng = findViewById(R.id.editText_currentGPS_lng);
        editText_currentLat = findViewById(R.id.editText_currentGPS_lat);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openGpsService();
    }

    // 開啟 GPS
    public void openLocationTracking(View view) {

        // 如果網路或定位尚未開啟
        if (!Utils.isOnline(context) || !Utils.isLocOpen(context)) return;

        // 如果 GpsService 尚未執行
        if (!Utils.isServiceRunning(context, GpsService.class.getName())) openGpsService();
    }

    // 開啟 GpsService
    private void openGpsService() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
            return;
        }
        startService(new Intent(context, GpsService.class));

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        return;
    }

    // 關閉 GPS
    public void closeLocationTracking(View view) {
        stopService(new Intent(context, GpsService.class));
    }
}
