package com.codingbydumbbell.gpsservicedemo;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Context context;

    private EditText currentLng;
    private EditText currentLat;
    private Switch switchInternet;
    private Switch switchLocation;
    private Switch switchLocListening;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
    }

    // findViews
    private void findViews() {
        context = this;

        currentLng = findViewById(R.id.editText_currentGPS_lng);
        currentLat = findViewById(R.id.editText_currentGPS_lat);
        switchInternet = findViewById(R.id.switch_internet);
        switchLocation = findViewById(R.id.switch_location);
        switchLocListening = findViewById(R.id.switch_loc_listening);

        switchLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        switchInternet.setChecked(isOnline());
        switchLocation.setChecked(isLocOpen());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void setLocation(View view) {
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
}
