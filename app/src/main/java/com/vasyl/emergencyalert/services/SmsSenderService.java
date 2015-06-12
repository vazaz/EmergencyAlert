package com.vasyl.emergencyalert.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;

import com.vasyl.emergencyalert.utils.LocationAddress;
import com.vasyl.emergencyalert.utils.MyContactsManager;

import java.util.List;

public class SmsSenderService extends Service implements LocationListener {
    protected LocationManager locationManager;
    private Context context;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public SmsSenderService() {
    }

    public SmsSenderService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
    }

    public Location getLocation() {
        Location location = null;
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                //get the location by gps
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void getLocationAndSendSms() {
        double latitude = getLocation().getLatitude();
        double longitude = getLocation().getLongitude();
        LocationAddress location = new LocationAddress();
        location.getAddressFromLocation(latitude, longitude,
                context, new GeocoderHandler());
    }

    public void sendSMSMessage(String message) {
        MyContactsManager contactsManager = new MyContactsManager(context);
        List<String> phones = contactsManager.getPhones(contactsManager.getSmsContacts());
        String sms = "I need help! I'm here: " + message;
        Log.e("sms", sms);
//        try {
//            SmsManager smsManager = SmsManager.getDefault();
//            for (String phoneNo : phones) {
//                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
//            }
//           Log.e("TEG", "SMS sent.");
//        } catch (Exception e) {
//            Log.e("TEG","SMS failed, please try again.");
//            e.printStackTrace();
//        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            sendSMSMessage(bundle.getString("address"));
        }
    }
}