package com.vasyl.emergencyalert.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.adapters.ContactAdapter;
import com.vasyl.emergencyalert.models.Contact;
import com.vasyl.emergencyalert.utils.SmsSender;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vasyl on 5/25/15.
 */
public class InfoScreenDialog extends DialogFragment {

    private TextView profile;
    private TextView medicine;
    private TextView callTo;
    private ListView contactListView;
    private List<Contact> contacts;
    private String disease;
    private String name;
    private String surname;
    private View view;
    private LayoutInflater inflater;
    private SharedPreferences sharedPrefs;
    private ContactAdapter adapter;
    private Ringtone ringtone;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        init();
        playAlarm();
        return getBuilder().create();
    }

    private void init() {
        sharedPrefs = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_alert, null);
        profile = (TextView) view.findViewById(R.id.alert_dialog_profile);
        medicine = (TextView) view.findViewById(R.id.alert_dialog_medicine);
        callTo = (TextView) view.findViewById(R.id.alert_dialog_first);
        contactListView = (ListView) view.findViewById(R.id.dialog_contact_list);
        contacts = getSmsContacts();
        adapter = new ContactAdapter(getActivity().getApplicationContext(), contacts);
        disease = sharedPrefs.getString(getString(R.string.disease), "");
        name = sharedPrefs.getString(getString(R.string.name), "");
        surname = sharedPrefs.getString(getString(R.string.surname), "");
        profile.setText("I am " + name + " " + surname);
        medicine.setText("I have " + disease);
        callTo.setText("You can call to");
        contactListView.setAdapter(adapter);
        new SmsSender(getActivity().getApplicationContext(), getPhones(contacts))
                .sendSmsWithLocation();
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

    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle(getString(R.string.myprofile));
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                InfoScreenDialog.this.getDialog().cancel();
                ringtone.stop();
                getActivity().finish();
            }
        });
        return builder;
    }

    private List<Contact> getSmsContacts() {
        String contactsString = sharedPrefs.getString(getString(R.string.contacts), "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Contact>>() {
        }.getType();
        return gson.fromJson(contactsString, type);
    }


    private List<String> getPhones(List<Contact> contacts) {
        List<String> phones = new ArrayList<>();
        if (contacts != null) {
            for (Contact item : contacts) {
                phones.add(item.getPhone());
            }
        }
        Log.e("TAG", phones.toString());
        return phones;
    }
}
