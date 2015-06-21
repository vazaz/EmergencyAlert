package com.vasyl.emergencyalert.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vasyl.emergencyalert.services.SensorService;

public class StartServiceOnBoot extends BroadcastReceiver {
    public StartServiceOnBoot() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       Intent service = new Intent(context, SensorService.class);
       context.startService(service);
    }
}
