package com.vasyl.emergencyalert.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        result = getAddressString(addressList);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    setResultToHandleMessage(result, message);
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    private static void setResultToHandleMessage(String result, Message message) {
        if (result != null) {
            message.what = 1;
            Bundle bundle = new Bundle();
            bundle.putString("address", result);
            message.setData(bundle);
        } else {
            message.what = 1;
            Bundle bundle = new Bundle();
            result = "Please call me!";
            bundle.putString("address", result);
            message.setData(bundle);
        }
    }

    private static String getAddressString(List<Address> addressList) {
        Address address = addressList.get(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            sb.append(address.getAddressLine(i)).append("\n");
        }
        return sb.toString();
    }
}