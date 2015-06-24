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

    private static final double SIGMA = 0.5;
    private static final double HIGH_THRESHOLD = 10;
    private static final double MIDDLE_THRESHOLD = 4;
    private static final double LOW_THRESHOLD = 2.5;
    private static final String NONE = "none";
    private static final String FALL = "fall";
    private static final String FALLING = "falling";

    private String currentState;
    private String previousState;

    private double currentX;
    private double currentY;
    private double currentZ;
    private double prevX;
    private double prevY;
    private double prevZ;
    private Double linearAcceleration;
    private Double prevLinearAcceleration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        previousState = NONE;
        currentState = NONE;

        HandlerThread handlerThread = new HandlerThread("SensorHandlerThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL, handler);

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            currentX = event.values[0];
            currentY = event.values[1];
            currentZ = event.values[2];
            posture_recognition(currentY);
            startFallDetectionAlertDialog(previousState);
            if (!previousState.equalsIgnoreCase(currentState)) {
                previousState = currentState;
                prevX = currentX;
                prevY = currentY;
                prevZ = currentZ;
            }
        }
    }

    private boolean iSFallDetected() {
        calculateLinearAcceleration();
        if (linearAcceleration > 1) {
            currentState = FALL;
        }
        return currentState == FALL;
    }

    private void calculateLinearAcceleration() {
        linearAcceleration = Math.sqrt(currentX * currentX + currentY * currentY + currentZ * currentZ);
        prevLinearAcceleration = Math.sqrt(prevX * prevX + prevY * prevY + prevZ * prevZ);
    }

    private void posture_recognition(double currentY) {
        int zrc = compute_zrc();
        if (zrc == 0 & (Math.abs(currentY) < MIDDLE_THRESHOLD)) {
            currentState = FALLING;
        }
    }

    private int compute_zrc() {
        int count = 0;
        if (prevLinearAcceleration != null) {
            if (Math.abs(linearAcceleration - HIGH_THRESHOLD) < -LOW_THRESHOLD & (prevLinearAcceleration - HIGH_THRESHOLD) > SIGMA) {
                count = count + 1;
            }
        }
        return count;
    }

    private void startFallDetectionAlertDialog(String previousState) {
        if (iSFallDetected() & previousState.equalsIgnoreCase(FALLING)) {
            Intent intent = new Intent(this, FallDetectionDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}