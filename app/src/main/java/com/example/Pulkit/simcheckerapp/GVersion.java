package com.example.Pulkit.simcheckerapp;

import android.os.Build;

import java.lang.reflect.Field;

/**
 * Created by Pulkit Jindal on 02,August,2021
 */
public class GVersion {
    String version_release = "";
    String version_code_name = "";
    String sdk = "";
    GVersion(){
        version_release = Build.VERSION.RELEASE+"";
        sdk = Build.VERSION.SDK_INT+"";
        Field[] fields = Build.VERSION_CODES.class.getFields();
        version_code_name = fields[Integer.parseInt(sdk)].getName();
    }
}
