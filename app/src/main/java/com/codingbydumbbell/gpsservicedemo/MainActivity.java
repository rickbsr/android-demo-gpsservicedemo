package com.codingbydumbbell.gpsservicedemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.codingbydumbbell.gpsservicedemo.service.GpsService;
import com.codingbydumbbell.gpsservicedemo.tools.Utils;
import com.codingbydumbbell.gpsservicedemo.userInfo.UserInfo;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOCATION = 98;
    private static final int REQUEST_CODE_SIGN_IN = 20;
    private Context context;


    private FirebaseAuth auth;

    private EditText editText_currentLng;
    private EditText editText_currentLat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        findViews();

        // Firebase auth：要傾聽登入狀態需要實作 FirebaseAuth.AuthStateListener
        auth = FirebaseAuth.getInstance();
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

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(this); // 加入傾聽，在 onStart() 可以避免無時無刻都在傾聽
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(this); // 移除傾聽
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
    return;
}

    // 關閉 GPS
    public void closeLocationTracking(View view) {
        stopService(new Intent(context, GpsService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_signout: // 登出
                auth.signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d(TAG, "onAuthStateChanged: ");

        // 得到一個 firebase user
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            // 可以透過 FirebaseUser 取得登入使用者的資訊
            final String displayName = user.getDisplayName();
            UserInfo.userUid = user.getUid();

            FirebaseDatabase.getInstance()
                    .getReference("users") // 第一層為 Reference
                    .child(UserInfo.userUid)
                    .child("diplayName").setValue(displayName);
        } else {
            // 代表使用者尚未登入
            startActivityForResult(
                    // 透過 AuthUI 建立 intent
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build()
                            ))
                            .setIsSmartLockEnabled(false) // 關閉 SmartLock
                            .build()
                    , REQUEST_CODE_SIGN_IN);
        }

        FirebaseDatabase.getInstance().getReference("users")
                .child(UserInfo.userUid)
                .child("LngLat")
                .child("origin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            String[] loc = dataSnapshot.getValue().toString().split(",");
                            UserInfo.originLng = Double.parseDouble(loc[0]);
                            UserInfo.originLat = Double.parseDouble(loc[1]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance().getReference("users")
                .child(UserInfo.userUid)
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
}
