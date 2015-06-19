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

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.SendCallback;
import com.vasyl.emergencyalert.utils.LocationAddress;
import com.vasyl.emergencyalert.utils.MyContactsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

public class SmsSenderService extends Service implements LocationListener {
    protected LocationManager locationManager;
    private Context context;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;
    private MyContactsManager contactsManager;

    public SmsSenderService() {
    }

    public SmsSenderService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
        contactsManager = new MyContactsManager(context);
    }

    public Location getLocation() {
        Location location = null;
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
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
                context, new GeocoderHandler(this));
    }

    public void sendSMSMessage(String message) {
        List<String> phones = contactsManager.getPhones(contactsManager.getSmsContacts());
        String sms = "I need help! I'm here: " + message;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            for (String phoneNo : phones) {
                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            }
           Log.e("TAG", "SMS sent.");
        } catch (Exception e) {
            Log.e("TAG","SMS failed, please try again.");
            e.printStackTrace();
        }
    }

    public void sendPushNotification(String message) {
        String sms = "I need help! I'm here: " + message;
        String personalData = contactsManager.getPersonalData();
        ParseQuery query = ParseInstallation.getQuery();
        query.whereEqualTo("deviceType", "android");
        ParseGeoPoint userLocation = new ParseGeoPoint(getLocation().getLatitude(), getLocation().getLongitude());
        Log.e("Latitude ", String.valueOf(getLocation().getLatitude()));
        JSONObject data = null;
        try {
            data = new JSONObject();
            data.put("title", personalData);
            data.put("alert", sms);
            data.put("location", userLocation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ParsePush push = new ParsePush();
        Log.e("sms", sms);
        push.setData(data);
        push.setQuery(query);
        Log.e("push empty", String.valueOf(push.equals(null)));
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("SEND PUSH", (e == null) ? "SUCCESSFULL" : "FAILED");
            }
        });
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

    private static final class GeocoderHandler extends Handler {

        private final WeakReference<SmsSenderService> serviceWeakReference;

        private GeocoderHandler(SmsSenderService serviceInstance) {
            serviceWeakReference = new WeakReference<>(serviceInstance);
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            String addressString = bundle.getString("address");
            SmsSenderService targetService = serviceWeakReference.get();
            targetService.sendSMSMessage(addressString);
            targetService.sendPushNotification(addressString);
        }
    }
}