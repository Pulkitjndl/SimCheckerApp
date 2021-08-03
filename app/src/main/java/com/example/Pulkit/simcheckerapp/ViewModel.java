package com.example.Pulkit.simcheckerapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private static String TAG = ViewModel.class.getSimpleName();
    private TelephonyManager telephonyManager;
    private SubscriptionManager subscriptionManager;
    private SubscriptionInfo subscriptionInfo;
    private SimChangedListener simChangedListener;
    private MutableLiveData<Boolean> liveSimState = new MutableLiveData<>();
    private MutableLiveData<String> liveSimICCID = new MutableLiveData<>();
    private MutableLiveData<Integer> liveSimIMSI = new MutableLiveData<Integer>();
    private MutableLiveData<String> liveISOCode = new MutableLiveData<>();
    private MutableLiveData<String> liveSerialNumber = new MutableLiveData<>();
    private static Integer MY_PERMISSIONS_REQUEST_PHONE_STATE = 0;

    public ViewModel(Application application) {
        super(application);
        Log.d(TAG, "constructor ");
        telephonyManager = (TelephonyManager) getApplication().getSystemService(Context.TELEPHONY_SERVICE);
        simChangedListener = new SimChangedListener(this);
        //setSubscription
    }
    //regoin Subscription method for LOLLIPOP_MRI

    //region register Unregister Sim State Detection Callback
    public void registerSimState() {
        telephonyManager.listen(simChangedListener, PhoneStateListener.LISTEN_SERVICE_STATE | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public void unregisterSimState() {
        telephonyManager.listen(simChangedListener, PhoneStateListener.LISTEN_NONE);
    }

    //endregion
    public void checkSimState() {
        liveSimState.postValue(isSimMounted());
    }

    //region Telephony managerMethod
    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isSimMounted() {
        Log.d(TAG, "isSimMounted: ");
        boolean isAvailable;
        int simState;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            simState = telephonyManager.getSimState(0);
        } else {
            simState = telephonyManager.getSimState(1);
        }
        switch (simState) {
            case TelephonyManager.SIM_STATE_READY:
                isAvailable = true;
                Log.d(TAG, "TelephonyManager.SIM_STATE_READY");
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                isAvailable = false;
                Log.d(TAG, "TelephonyManager.SIM_STATE_ABSENT");
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                isAvailable = false;
                Log.d(TAG, "TelephonyManager.SIM_STATE_NETWORK_LOCKED");
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                isAvailable = false;
                Log.d(TAG, "TelephonyManager.SIM_STATE_PIN_REQUIRED");
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                isAvailable = false;
                Log.d(TAG, "TelephonyManager.SIM_STATE_PUK_REQUIRED");
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                isAvailable = false;
                Log.d(TAG, "telephonyManager.SIM_STATE_UNKNOWN");
                break;
            default:
                isAvailable = false;
                Log.d(TAG, "TelephonyManager.default");
                break;
        }
        return isAvailable;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void getISMI(Context context) {
        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);

        if (ActivityCompat.checkSelfPermission((Activity) context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<SubscriptionInfo> localList = localSubscriptionManager.getActiveSubscriptionInfoList();
        SubscriptionInfo info = (SubscriptionInfo) localList.get(0);
        info.getSubscriptionId();

        liveSimIMSI.postValue(info.getSubscriptionId());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public void getICCID(Context context) {
        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
        if (ActivityCompat.checkSelfPermission((Activity)context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<SubscriptionInfo> localList = localSubscriptionManager.getActiveSubscriptionInfoList();
        SubscriptionInfo info = (SubscriptionInfo) localList.get(0);
        info.getIccId();

        liveSimICCID.postValue(info.getIccId());
    }
    @SuppressLint("NewApi")
    public void getSimSerialNumber(Context context) {

        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<SubscriptionInfo> localList = localSubscriptionManager.getActiveSubscriptionInfoList();
        SubscriptionInfo info = (SubscriptionInfo) localList.get(0);
        info.getCardId();

        liveSimIMSI.postValue(info.getCardId());
    }
    public void getISOCode(){
        liveISOCode.postValue(telephonyManager.getNetworkCountryIso());
        Log.d(TAG, "getISOCode: "+telephonyManager.getNetworkCountryIso());
    }

    public void getSimSerialNumber(){
        liveSerialNumber.postValue(telephonyManager.getSimSerialNumber());
    }

    public LiveData<String>observeLiveSimICCID(){
        return liveSimICCID;
    }
    public LiveData<Integer> observeLiveSimISMI(){
        return liveSimIMSI;
    }
    public LiveData<String>observeLiveISOCode(){
        return liveISOCode;
    }
    public LiveData<String>observeLiveSerialNumber(){return liveSerialNumber;}

    //region manifestPermission
    public boolean checkPermission() {
        boolean value = false;
        Log.d(TAG,"checkPermission()");
        if (isReadPhoneStatePermissionGranted()) {
            Log.d(TAG,"isReadPhoneStatePermissionGranted()");
            value = true;
        }
        return value;
    }
    public LiveData<Boolean>observeLiveSimState(){
        return liveSimState;
    }
    private boolean isReadPhoneStatePermissionGranted(){
        return ActivityCompat
                .checkSelfPermission(
                        getApplication(),
                        Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCleared(){
        super.onCleared();
    }
}
