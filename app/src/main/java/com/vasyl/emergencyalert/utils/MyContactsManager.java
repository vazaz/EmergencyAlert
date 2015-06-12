package com.vasyl.emergencyalert.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.models.Contact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sweet Wine on 07.06.2015.
 */
public class MyContactsManager {

    private Context context;
    private SharedPreferences sharedPrefs;

    public MyContactsManager(Context context) {
        this.context = context;
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPrefs() {
        return sharedPrefs;
    }

    public ArrayList<Contact> getAllContactsList(View view) {
        ArrayList<Contact> contactList = new ArrayList<>();
        try {
            Cursor people = view.getContext().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                            new String[] { "com.google" }, null);
            if (people != null) {
                while (people.moveToNext()) {
                    Contact contact = new Contact();
                    contact.setName(people.getString(people.getColumnIndex(ContactsContract
                            .CommonDataKinds
                            .Phone.DISPLAY_NAME)));
                    contact.setPhone(people.getString(people.getColumnIndex(ContactsContract
                            .CommonDataKinds
                            .Phone.NUMBER)));
                    contactList.add(contact);
                }
            }
            assert people != null;
            people.close();
        } catch (NullPointerException e) {
            Log.e("getAllContactsList()", e.getMessage());
        }
        return contactList;
    }

    public void saveSmsContacts(List<Contact> contacts) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String jsonContacts = gson.toJson(contacts);
        editor.putString(context.getString(R.string.contacts), jsonContacts);
        editor.apply();
    }

    public List<Contact> getSmsContacts() {
        String contactsString = sharedPrefs.getString(context.getString(R.string.contacts), "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Contact>>() {
        }.getType();
        return gson.fromJson(contactsString, type);
    }

    public List<String> getPhones(List<Contact> contacts) {
        List<String> phones = new ArrayList<>();
        if (contacts != null) {
            for (Contact item : contacts) {
                phones.add(item.getPhone());
            }
        }
        return phones;
    }
}