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

import java.util.ArrayList;

public class SensorService extends Service implements SensorEventListener {


    public static final double FREE_fALL = 9.8;
    public static final double SIGMA = 0.5;
    public static final double HIGH_THRESHOLD = 10;
    public static final double MIDDLE_THRESHOLD = 5;
    public static final double LOW_THRESHOLD = 2.5;
    public static final int BUFF_SIZE = 50;
    public static final String NONE = "none";
    public static final String FALL = "fall";
    public static final String WALKING = "walking";
    public static final String SITTING = "sitting";
    public static final String STANDING = "standing";

    private String currentState;
    private String previousState;
    public double[] window;

    public double currentX;
    public double currentY;
    public double currentZ;
    public double previousX;
    public double previousY;
    public double previousZ;
    public Double linearAcceleration;

    private SensorManager sensorManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        window = new double[BUFF_SIZE];
        for (int i = 0; i < BUFF_SIZE; i++) {
            window[i] = 0;
        }
        previousState = NONE;
        currentState = NONE;
        previousX = FREE_fALL;
        previousY = FREE_fALL;
        previousZ = FREE_fALL;

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
            posture_recognition(window, currentY);
            startFallDetectionAlertDialog(previousState);
            if (!previousState.equalsIgnoreCase(currentState)) {
                previousState = currentState;
            }
        }
    }

    private boolean iSFallDetected() {
        calculateLinearAcceleration();
        if (linearAcceleration > 2) {
            currentState = FALL;
        }
        return currentState == FALL;
    }

    private void calculateLinearAcceleration() {
        linearAcceleration = Math.sqrt(currentX * currentX + currentY * currentY + currentZ * currentZ);
    }

    private void posture_recognition(double[] window2, double currentY) {
        int zrc = compute_zrc(window2);
        if (zrc == 0) {
            staticPostureRecognition(currentY);
        } else {
            dynamicPostureRecognition(zrc);
        }
    }

    private void dynamicPostureRecognition(int zrc) {
        if (zrc > LOW_THRESHOLD) {
            currentState = WALKING;
        } else {
            currentState = NONE;
        }
    }

    private void staticPostureRecognition(double currentY) {
        if (Math.abs(currentY) < MIDDLE_THRESHOLD) {
            currentState = SITTING;
        } else {
            currentState = STANDING;
        }
    }

    private int compute_zrc(double[] window) {
        int count = 0;
        for (int i = 1; i <= BUFF_SIZE - 1; i++) {
            if ((window[i] - HIGH_THRESHOLD) < SIGMA && (window[i - 1] - HIGH_THRESHOLD) > SIGMA) {
                count = count + 1;
            }
        }
        return count;
    }

    private void startFallDetectionAlertDialog(String previousState) {
        if (iSFallDetected() & (previousState.equalsIgnoreCase(SITTING) ||
                previousState.equalsIgnoreCase(STANDING))) {
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
