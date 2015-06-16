package com.vasyl.emergencyalert.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.adapters.ContactAdapter;
import com.vasyl.emergencyalert.models.Contact;
import com.vasyl.emergencyalert.services.SmsSenderService;
import com.vasyl.emergencyalert.utils.MyContactsManager;

import java.util.List;

/**
 * Created by vasyl on 5/25/15.
 */
public class InfoScreenDialog extends DialogFragment {

    private List<Contact> contacts;
    private String disease;
    private String name;
    private String surname;
    private SharedPreferences sharedPrefs;
    private ContactAdapter adapter;
    private Contact firstContact;
    private MediaPlayer player;
    private Vibrator vibrator;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        init();
        playAlarm();
        Dialog newDialog = getBuilder().create();
        newDialog.setCanceledOnTouchOutside(false);
        setCancelable(false);
        return newDialog;
    }

    private void init() {
        sharedPrefs = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        final MyContactsManager contactsManager = new MyContactsManager(getActivity().getApplicationContext());
        contacts = contactsManager.getSmsContacts();
        adapter = new ContactAdapter(getActivity().getApplicationContext(), contacts);
        disease = sharedPrefs.getString(getString(R.string.disease), "");
        name = sharedPrefs.getString(getString(R.string.name), "");
        surname = sharedPrefs.getString(getString(R.string.surname), "");
        firstContact = contacts.get(0);
        SmsSenderService smsSenderService = new SmsSenderService(getActivity().getApplicationContext());
        smsSenderService.getLocationAndSendSms();
    }

    private void playAlarm() {
        try {
            Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            player = MediaPlayer.create(getActivity().getApplicationContext(), defaultUri);
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = "I am " + name + " " + surname + ".\n"
                + "I have " + disease + ".\n"
                + "Please call to " + firstContact.getName() + ".";
        CharSequence callContact = getString(R.string.call) + " " + firstContact.getName();
        builder.setTitle(getString(R.string.please_help));
        builder.setMessage(message);
        builder.setNegativeButton(callContact, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                InfoScreenDialog.this.getDialog().cancel();
                player.stop();
                getActivity().finish();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + firstContact.getPhone()));
                startActivity(callIntent);
            }
        });
        return builder;
    }
}
