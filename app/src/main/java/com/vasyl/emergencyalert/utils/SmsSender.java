package com.vasyl.emergencyalert.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.vasyl.emergencyalert.services.GpsService;

import java.util.ArrayList;
import java.util.List;

public class SmsSender {

    private Context mContext;
    private GpsService appLocationService;
    private List<String> phones;

    public SmsSender(Context mContext, List<String> phones) {
        this.mContext = mContext;
        this.phones = phones;
    }

    public void sendSmsWithLocation() {
        appLocationService = new GpsService(
                mContext);
        Location location = appLocationService
                .getLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            getLocationAddress(location);
        }
    }

    private void getLocationAddress(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LocationAddress locationAddress = new LocationAddress();
        locationAddress.getAddressFromLocation(latitude, longitude,
                mContext, new GeocoderHandler());
    }

    private void sendSMSMessage(String message) {
        String sms = "I need help! I'm here: " + message;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String phoneNo : phones) {
                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            }
            Toast.makeText(mContext, "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(mContext,
                    "SMS failed, please try again.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            sendSMSMessage(locationAddress);
        }
    }
}