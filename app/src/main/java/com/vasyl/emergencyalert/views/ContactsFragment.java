package com.vasyl.emergencyalert.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vasyl.emergencyalert.services.SensorService;
import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.adapters.ContactAdapter;
import com.vasyl.emergencyalert.models.Contact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.timroes.android.listview.EnhancedListView;

public class ContactsFragment extends Fragment implements View.OnClickListener, AdapterView
        .OnItemClickListener{

    private View view;
    private EnhancedListView recyclerView;
    private AutoCompleteTextView autoCompleteTextView;
    private Button startButton;

    private ContactAdapter smsContactsAdapter;
    private ContactAdapter autoCompleteTextViewAdapter;

    private List<Contact> smsContactsList;
    private List<Contact> allContactsList;
    private SharedPreferences sharedPrefs;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPrefs = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.add_contact);
        recyclerView = (EnhancedListView) view.findViewById(R.id.listview);
        startButton = (Button) view.findViewById(R.id.start_button);

        gson = new Gson();
        startButton.setEnabled(false);
        smsContactsList = getSmsContactsList();
        allContactsList = getAllContactsList(view);

        autoCompleteTextViewAdapter = getContactAdapter(allContactsList);
        smsContactsAdapter = getContactAdapter(smsContactsList);

        autoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        recyclerView.setAdapter(smsContactsAdapter);
        autoCompleteTextView.setOnItemClickListener(this);
        startButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        saveSmsContacts(smsContactsList);
        startIntent();
    }

    private void startIntent() {
        Intent intent = new Intent(getActivity(), SensorService.class);
        getActivity().startService(intent);
        getActivity().finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Contact contact = (Contact) autoCompleteTextView.getAdapter().getItem(position);
        smsContactsAdapter.add(contact);
        smsContactsAdapter.removeDuplicates();
        startButton.setEnabled(true);
        autoCompleteTextView.setText("");
    }

    private ContactAdapter getContactAdapter(List<Contact> list) {
        return new ContactAdapter(getActivity().getApplicationContext(), R.layout.single_contact,
                list);
    }

    private ArrayList<Contact> getAllContactsList(View view) {
        ArrayList<Contact> contactList = new ArrayList<>();
        try {
            Cursor people = view.getContext().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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

    private void saveSmsContacts (List<Contact> contacts){
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String jsonContacts = gson.toJson(contacts);
        editor.putString(getString(R.string.contacts), jsonContacts);
        editor.apply();
    }

    private List<Contact> getSmsContacts(){
        String contactsString = sharedPrefs.getString(getString(R.string.contacts), "");
        gson = new Gson();
        Type type = new TypeToken<ArrayList<Contact>>(){}.getType();
        return gson.fromJson(contactsString, type);
    }

    private List<Contact> getSmsContactsList() {
        if (sharedPrefs.contains(getString(R.string.contacts))){
            startButton.setEnabled(true);
            return getSmsContacts();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveSmsContacts(smsContactsList);
    }
}