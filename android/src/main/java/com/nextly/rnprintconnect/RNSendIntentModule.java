package com.nextly.rnprintconnect;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.SecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class RNSendIntentModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;
    private Callback mCallback;

    public RNSendIntentModule(ReactApplicationContext reactContext) {
      super(reactContext);
      this.reactContext = reactContext;
      this.reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
      return "SendIntentAndroid";
    }


    private boolean parseExtras(ReadableMap extras, Intent intent) {
        ReadableMapKeySetIterator it = extras.keySetIterator();

        while(it.hasNextKey()) {
            String key = it.nextKey();
            ReadableType type = extras.getType(key);

            switch (type) {
                case Boolean:
                    intent.putExtra(key, extras.getBoolean(key));
                    break;
                case Map:
                    //because in js, there is no distinction between double, int, short, etc. we use an object indicating the type
                    ReadableMap map = extras.getMap(key);
                    if (map.hasKey("type")) {
                        String actualType = map.getString("type").toLowerCase();
                        switch (actualType) {
                            case "int":
                                intent.putExtra(key, map.getInt("value"));
                                break;
                            case "short":
                                intent.putExtra(key, (short)map.getInt("value"));
                                break;
                            case "byte":
                                intent.putExtra(key, (byte)map.getInt("value"));
                                break;
                            case "char":
                                intent.putExtra(key, (char)map.getInt("value"));
                                break;
                            case "long":
                                intent.putExtra(key, (long)map.getDouble("value"));
                                break;
                            case "float":
                                intent.putExtra(key, (float)map.getDouble("value"));
                                break;
                            case "double":
                                intent.putExtra(key, map.getDouble("value"));
                                break;
                        }
                    }
                    else { //not parsing real maps for now
                        return false;
                    }
                    break;
                case Number:
                    intent.putExtra(key, (double) extras.getDouble(key));
                    break;
                case String:
                    intent.putExtra(key, extras.getString(key));
                    break;
                case Array:
                    ReadableArray array = extras.getArray(key);
                    if (array.size() == 0) { //cannot guess the type of an empty array
                        return false;
                    }

                    //try to infer the type of the array from the first element
                    ReadableType arrayType = array.getType(0);
                    switch (arrayType) {
                        case Boolean:
                            boolean[] bArray = new boolean[array.size()];
                            for (int i = 0; i < array.size(); ++i)
                                bArray[i] = array.getBoolean(i);
                            intent.putExtra(key, bArray);
                            break;
                        case Map:
                            ReadableMap aMap = extras.getMap(key);
                            if (aMap.hasKey("type")) {
                                String actualType = aMap.getString("type").toLowerCase();
                                switch (actualType) {
                                    case "int":
                                        int[] iArray = new int[array.size()];
                                        for (int i = 0; i < array.size(); ++i)
                                            iArray[i] = array.getInt(i);
                                        intent.putExtra(key, iArray);
                                        break;
                                    case "short":
                                        short[] shArray = new short[array.size()];
                                        for (int i = 0; i < array.size(); ++i)
                                            shArray[i] = (short)array.getInt(i);
                                        intent.putExtra(key, shArray);
                                        break;
                                    case "byte":
                                        byte[] byArray = new byte[array.size()];
                                        for (int i = 0; i < array.size(); ++i)
                                            byArray[i] = (byte)array.getInt(i);
                                        intent.putExtra(key, byArray);
                                        break;
                                    case "char":
                                        char[] cArray = new char[array.size()];
                                        for (int i = 0; i < array.size(); ++i)
                                            cArray[i] = (char)array.getInt(i);
                                        intent.putExtra(key, cArray);
                                        break;
                                    case "long":
                                        long[] lArray = new long[array.size()];
                                        for (int i = 0; i < array.size(); ++i)
                                            lArray[i] = (long)array.getInt(i);
                                        intent.putExtra(key, lArray);
                                        break;
                                    case "float":
                                        float[] fArray = new float[array.size()];
                                        for (int i = 0; i < array.size(); ++i)
                                            fArray[i] = (float)array.getDouble(i);
                                        intent.putExtra(key, fArray);
                                        break;
                                    case "double":
                                        double[] doArray = new double[array.size()];
                                        for (int i = 0; i < array.size(); ++i)
                                            doArray[i] = array.getDouble(i);
                                        intent.putExtra(key, doArray);
                                        break;
                                }
                            }
                            else { //not parsing real maps for now
                                return false;
                            }
                            break;
                        case Number:
                            double[] dArray = new double[array.size()];
                            for (int i = 0; i < array.size(); ++i)
                                dArray[i] = array.getDouble(i);
                            intent.putExtra(key, dArray);
                            break;
                        case String:
                            String[] sArray = new String[array.size()];
                            for (int i = 0; i < array.size(); ++i)
                                sArray[i] = array.getString(i);
                            intent.putExtra(key, sArray);
                            break;
                    }

                    break;
                  //ignore everything else
            }
        }

        return true;
    }

    @ReactMethod
    public void print(String templateName, ReadableMap variables, final Promise promise) {
        HashMap<String, String> variableData = new HashMap<>();
        ReadableMapKeySetIterator it = variables.keySetIterator();
        while(it.hasNextKey()) {
            String key = it.nextKey();
            variableData.put(key, variables.getString(key));
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.zebra.printconnect",
          "com.zebra.printconnect.print.TemplatePrintService"));
        intent.putExtra("com.zebra.printconnect.PrintService.TEMPLATE_FILE_NAME", templateName);
        intent.putExtra("com.zebra.printconnect.PrintService.VARIABLE_DATA", variableData);
        intent.putExtra("com.zebra.printconnect.PrintService.RESULT_RECEIVER", buildIPCSafeReceiver(new
            ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == 0) { // Result code 0 indicates success
                    promise.resolve(true);
                } else {
                    promise.resolve(false);
                    // String errorMessage = resultData.getString("com.zebra.printconnect.PrintService.ERROR_MESSAGE");
                }
            }
        }));
        this.reactContext.startActivity(intent);
    }

}
