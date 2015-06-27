package com.vasyl.emergencyalert.services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.vasyl.emergencyalert.views.FallDetectionDialogActivity;

public class SensorService extends Service implements SensorEventListener {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        startFallDetectionAlertDialog(isFallDetected());
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        HandlerThread handlerThread = new HandlerThread("SensorHandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, handler);
        s
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    private void startFallDetectionAlertDialog(boolean isFall) {
            Intent intent = new Intent(this, FallDetectionDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
    }
    
    private boolean isFallDetected() {
//      Fall detection algorithm must implement here
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
