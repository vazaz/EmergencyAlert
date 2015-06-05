package com.vasyl.emergencyalert.views;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.dialogs.OkDialog;

public class FallDetectionDialogActivity extends FragmentActivity {

    private KeyguardManager.KeyguardLock keyguardLock;
    private PowerManager.WakeLock wakeLock;
    private DialogFragment dialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detection);
        init();
        wakeUpScreen();
        showDialog("ok");
    }

    private void init() {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("TAG");
        dialog = new OkDialog();
    }

    private void wakeUpScreen() {
        wakeLock.acquire();
        keyguardLock.disableKeyguard();
    }

    private void showDialog(String id) {
        dialog.show(getSupportFragmentManager(), id);
    }
}