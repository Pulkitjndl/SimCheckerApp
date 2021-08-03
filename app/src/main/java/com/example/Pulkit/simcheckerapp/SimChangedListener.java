package com.example.Pulkit.simcheckerapp;

import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.view.View;

public class SimChangedListener extends PhoneStateListener {
    private static String TAG = SimChangedListener.class.getSimpleName();
    public ViewModel ViewModel;
    public SimChangedListener(ViewModel ViewModel){
        this.ViewModel = ViewModel;
    }
    public void onServiceStateChanged(ServiceState serviceState){
        super.onServiceStateChanged(serviceState);
        ViewModel.checkSimState();
    }
    public void onSignalStrengthsChanged(SignalStrength signalStrength){
        super.onSignalStrengthsChanged(signalStrength);
        ViewModel.checkSimState();
    }
}
