package com.vasyl.emergencyalert.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.vasyl.emergencyalert.R;

import java.util.Timer;

/**
 * Created by vasyl on 5/25/15.
 */
public class OkDialog extends DialogFragment {

    private Timer timer;
    private Vibrator vibrator;
    private Ringtone ringtone;

    public OkDialog() {
    }

    public OkDialog(Timer timer, Vibrator vibrator) {
        this.timer = timer;
        this.vibrator = vibrator;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        playAlarm();
        return getBuilder().create();
    }

    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.are_you_ok).setTitle(R.string.fall_detected);
        builder.setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OkDialog.this.getDialog().cancel();
                timer.cancel();
                vibrator.cancel();
                ringtone.stop();
                getActivity().finish();
            }
        });
        return builder;
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
}
