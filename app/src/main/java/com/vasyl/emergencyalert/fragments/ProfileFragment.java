package com.vasyl.emergencyalert.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.vasyl.emergencyalert.R;

public class ProfileFragment extends Fragment {

    private View view;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText diseaseEditText;
    private String name;
    private String surname;
    private String disease;
    private SharedPreferences sharedPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveProfile();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.name), name);
        outState.putString(getString(R.string.surname), surname);
        outState.putString(getString(R.string.surname), disease);
    }

    public void init() {
        sharedPrefs = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        nameEditText = (EditText) view.findViewById(R.id.name_edit_view);
        surnameEditText = (EditText) view.findViewById(R.id.surname_edit_view);
        diseaseEditText = (EditText) view.findViewById(R.id.disease);
    }

    private void saveProfile() {
        name = nameEditText.getText().toString().trim();
        surname = surnameEditText.getText().toString().trim();
        disease = diseaseEditText.getText().toString().trim();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(getString(R.string.name), name);
        editor.putString(getString(R.string.surname), surname);
        editor.putString(getString(R.string.disease), disease);
        editor.apply();
    }
}
