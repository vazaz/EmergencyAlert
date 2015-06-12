package com.vasyl.emergencyalert.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.utils.MyContactsManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vasyl on 5/25/15.
 */
public class OkDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private static final long SHOW_DIALOG_VIBRATE_TIME = 15000;
    private DialogFragment dialog;
    private Timer timer;
    private Vibrator vibrator;
    private Ringtone ringtone;

    public OkDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        timer = new Timer();
        playAlarm();
        alarmTimer();
        Dialog newDialog = getBuilder().create();
        newDialog.setCanceledOnTouchOutside(false);
        return newDialog;
    }

    private void playAlarm() {
        try {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(),
                    alarmSound);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alarmTimer() {
        dialog = new InfoScreenDialog();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dismissDialog();
                dialog.show(getFragmentManager(), "info");
            }
        }, SHOW_DIALOG_VIBRATE_TIME);
    }

    private AlertDialog.Builder getBuilder() {
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.fall_detected).setTitle(R.string.are_you_ok);
        builder.setNegativeButton(R.string.i_am_ok, this);
        builder.setPositiveButton(R.string.call_for_help, this);
        return builder;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        MyContactsManager contactsManager = new MyContactsManager(getActivity().getApplicationContext());
        switch (i) {
            case (Dialog.BUTTON_NEGATIVE): {
                dismissDialog();
                getActivity().finish();
                break;
            }
            case (Dialog.BUTTON_POSITIVE): {
                dismissDialog();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String firstContactPhone = contactsManager.getSmsContacts().get(0).getPhone();
                callIntent.setData(Uri.parse("tel:" + firstContactPhone));
                startActivity(callIntent);
                break;
            }
        }
    }

    private void dismissDialog() {
        dismiss();
        timer.cancel();
        vibrator.cancel();
        ringtone.stop();
    }
}
