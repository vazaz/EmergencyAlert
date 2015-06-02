package com.vasyl.emergencyalert.views;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.dialogs.InfoScreenDialog;
import com.vasyl.emergencyalert.dialogs.OkDialog;
import com.vasyl.emergencyalert.models.Contact;
import com.vasyl.emergencyalert.utils.SmsSender;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FallDetectionDialogActivity extends FragmentActivity {

    private static final int SHOW_DIALOG_VIBRATE_TIME = 15000;
    private List<String> phones;
    private Timer timer;
    private Vibrator vibrator;
    private KeyguardManager.KeyguardLock keyguardLock;
    private KeyguardManager keyguardManager;
    private PowerManager.WakeLock wakeLock;
    private PowerManager powerManager;
    private DialogFragment dialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fall_detection);
        init();
        wakeUpScreen();
        showDialog("ok");
    }

    private void init() {
        timer = new Timer();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        phones = getPhones();
        powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "TAG");
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("TAG");
    }

    private void wakeUpScreen() {
        wakeLock.acquire();
        keyguardLock.disableKeyguard();
    }

    private void showDialog(String id) {
        checkDialogType(id);
        dialog.show(getSupportFragmentManager(), id);
    }

    private void checkDialogType(String id) {
        if (id.equals("info")) {
            dialog = new InfoScreenDialog();
        } else {
            dialog = new OkDialog(timer, vibrator);
            vibrator.vibrate(SHOW_DIALOG_VIBRATE_TIME);
            alarmTimer();
        }
    }

    private void alarmTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
                timer.cancel();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new SmsSender(getApplicationContext(), phones).sendSmsWithLocation();
                    }
                });
                showDialog("info");
            }
        }, SHOW_DIALOG_VIBRATE_TIME);
    }

    private List<String> getPhones() {
        SharedPreferences sharedPrefs = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String contactsString = sharedPrefs.getString(getString(R.string.contacts), "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Contact>>() {
        }.getType();
        List<String> phones = new ArrayList<>();
        List<Contact> contacts = gson.fromJson(contactsString, type);
        if (contacts != null) {
            for (Contact item : contacts) {
                phones.add(item.getPhone());
            }
        }
        return phones;
    }
}