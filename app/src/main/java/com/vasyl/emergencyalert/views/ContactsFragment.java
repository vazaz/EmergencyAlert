package com.vasyl.emergencyalert.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.adapters.ContactAdapter;
import com.vasyl.emergencyalert.models.Contact;
import com.vasyl.emergencyalert.services.SensorService;
import com.vasyl.emergencyalert.utils.SwipeDismissListViewTouchListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements View.OnClickListener, AdapterView
        .OnItemClickListener {

    private View view;
    private ListView listView;
    private AutoCompleteTextView autoCompleteTextView;
    private Button startButton;

    private ContactAdapter smsContactsAdapter;

    private List<Contact> smsContactsList;
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
        listView = (ListView) view.findViewById(R.id.listview);
        startButton = (Button) view.findViewById(R.id.start_button);

        gson = new Gson();
        startButton.setEnabled(false);
        smsContactsList = getSmsContactsList();
        List<Contact> allContactsList = getAllContactsList(view);

        final ContactAdapter autoCompleteTextViewAdapter = getContactAdapter(allContactsList);
        smsContactsAdapter = getContactAdapter(smsContactsList);

        autoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        listView.setAdapter(smsContactsAdapter);
        autoCompleteTextView.setOnItemClickListener(this);
        startButton.setOnClickListener(this);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                autoCompleteTextViewAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    smsContactsAdapter.remove(position);
                                }
                                smsContactsAdapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());
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
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        startButton.setEnabled(true);
        autoCompleteTextView.setText("");
    }

    private ContactAdapter getContactAdapter(List<Contact> list) {
        return new ContactAdapter(getActivity().getApplicationContext(), list);
    }

    private ArrayList<Contact> getAllContactsList(View view) {
        ArrayList<Contact> contactList = new ArrayList<>();
        try {
            Cursor people = view.getContext().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (people != null) {
                while (people.moveToNext()) {
                    Contact contact = new Contact();
//                    String image_uri = people
//                            .getString(people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
//                    contact.setAvatar(MediaStore.Images.Media
//                            .getBitmap(getActivity().getApplicationContext().getContentResolver(),
//                                    Uri.parse(image_uri)));
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
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        }
        return contactList;
    }

    private void saveSmsContacts(List<Contact> contacts) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String jsonContacts = gson.toJson(contacts);
        editor.putString(getString(R.string.contacts), jsonContacts);
        editor.apply();
    }

    private List<Contact> getSmsContacts() {
        String contactsString = sharedPrefs.getString(getString(R.string.contacts), "");
        gson = new Gson();
        Type type = new TypeToken<ArrayList<Contact>>() {
        }.getType();
        return gson.fromJson(contactsString, type);
    }

    private List<Contact> getSmsContactsList() {
        if (sharedPrefs.contains(getString(R.string.contacts))) {
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