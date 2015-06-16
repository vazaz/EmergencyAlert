package com.vasyl.emergencyalert.broadcastReceivers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by vasyl on 6/16/15.
 */
public class PushBroadCastReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        JSONObject json;
        Double latitude = null;
        Double longitude = null;
        try {
            json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            JSONObject jsonGeoPoint = json.getJSONObject("location");
            String title = json.getString("title");
            latitude = jsonGeoPoint.getDouble("latitude");
            longitude = jsonGeoPoint.getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String uriString = "geo:" + latitude + "," + longitude;
        Uri gmmIntentUri = Uri.parse(uriString);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mapIntent);
    }
}