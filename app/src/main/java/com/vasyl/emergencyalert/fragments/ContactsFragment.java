package com.vasyl.emergencyalert.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.adapters.ContactAdapter;
import com.vasyl.emergencyalert.models.Contact;
import com.vasyl.emergencyalert.services.SensorService;
import com.vasyl.emergencyalert.utils.MyContactsManager;
import com.vasyl.emergencyalert.utils.SwipeDismissListViewTouchListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment implements View.OnClickListener, AdapterView
        .OnItemClickListener {

    private View view;
    private ListView listView;
    private AutoCompleteTextView autoCompleteTextView;
    private Button startButton;

    private ContactAdapter listAdapter;

    private List<Contact> smsContactsList;
    private Gson gson;
    private ContactAdapter autoCompleteTextViewAdapter;
    private MyContactsManager myContactsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        init();
        return view;
    }

    private void init() {
        myContactsManager = new MyContactsManager(getActivity().getApplicationContext());

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.add_contact);
        listView = (ListView) view.findViewById(R.id.listview);
        startButton = (Button) view.findViewById(R.id.start_button);
        startButton.setEnabled(false);

        gson = new Gson();
        List<Contact> allContactsList = myContactsManager.getAllContactsList(view);
        smsContactsList = getSmsContactsList();
        autoCompleteTextViewAdapter = getContactAdapter(allContactsList);
        autoCompleteTextView.setOnItemClickListener(this);
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

        autoCompleteTextView.setAdapter(autoCompleteTextViewAdapter);
        listAdapter = getContactAdapter(smsContactsList);
        listView.setAdapter(listAdapter);
        Log.e("listAdapter on start", String.valueOf(listAdapter.getCount()));

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
                                    listAdapter.remove(position);
                                }
                                listAdapter.notifyDataSetChanged();
                                if (listAdapter.isEmpty()) {
                                    startButton.setEnabled(false);
                                }
                            }
                        });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());
        startButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        myContactsManager.saveSmsContacts(smsContactsList);
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
        listAdapter.add(contact);
        listAdapter.removeDuplicates();
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

    private List<Contact> getSmsContactsList() {
        if (myContactsManager.getSharedPrefs().contains(getString(R.string.contacts))) {
            if (!myContactsManager.getSmsContacts().isEmpty()) {
                startButton.setEnabled(true);
            }
            return myContactsManager.getSmsContacts();
        } else {
            startButton.setEnabled(false);
            return new ArrayList<>();
        }
    }
}