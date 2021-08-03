package com.example.Pulkit.simcheckerapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int PHONE_STATE = 0;
    private static final String TAG = "";
    private ViewModel viewModel;
    private TextView textView;
    private TextView text2View;
    private TextView text3View;
    private TextView text4View;
    private TextView text5View;
    private TextView text6View;
    private TextView text7View;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyPermissions(Manifest.permission.READ_PHONE_STATE, 1);
        verifyStoragePermissions(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
        textView = findViewById(R.id.text1);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        text2View = findViewById(R.id.text2);
        text3View = findViewById(R.id.text3);
        text4View = findViewById(R.id.text4);
        text5View = findViewById(R.id.text5);
        text6View = findViewById(R.id.text6);
        text7View = findViewById(R.id.text7);
        observeData();
        observeICCIDDate();
        viewModel.getICCID(this);
        observeISMI();
        viewModel.getISMI(this);
        observeISO();
        viewModel.getISOCode();
        GVersion gVersion = new GVersion();
        text6View.setText(
                "SDK-API Level" + gVersion.sdk
        );
        observeLiveSerialNumber();
        if (android.os.Build.VERSION.SDK_INT <= 26)
            viewModel.getSimSerialNumber();
        text7View.setText(
                gVersion.version_release
        );
    }

    private void verifyPermissions(String Permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
        } else {

        }
    }

    private void verifyStoragePermissions(String Permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else {

        }
    }


    private void observeData(){
        viewModel.observeLiveSimState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if(value){
                    textView.setText("Sim is inserted in Slot 1");
                }else{
                    textView.setText("Sim is either inserted in Slot 2 or Not inserted");
                }
            }
        });
    }
    private void observeICCIDDate(){
        Log.d(TAG, "observeICCIDDate: ");
        viewModel.observeLiveSimICCID().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null){
                    text2View.setText(s);
                }
            }
        });
    }
    private void observeISMI(){
        viewModel.observeLiveSimISMI().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer!=null){
                    text3View.setText(integer+"");
                }
            }
        });
    }
    private void observeISO(){
        viewModel.observeLiveISOCode().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null){
                    text4View.setText(s);
                }
            }
        });
    }
    private void observeLiveSerialNumber(){
        viewModel.observeLiveSerialNumber().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null)
                    text5View.setText(s);
            }
        });
    }

    @Override
    protected void onPause() {

        super.onPause();
        viewModel.unregisterSimState();
    }
    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions(viewModel.checkPermission());
        viewModel.checkPermission();
        viewModel.registerSimState();
    }

    private void requestPermissions(boolean permissionGranted) {
        if(!permissionGranted){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE
                    },
                    PHONE_STATE
            );
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}