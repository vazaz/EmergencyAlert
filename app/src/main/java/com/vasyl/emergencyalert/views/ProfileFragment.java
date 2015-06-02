package com.vasyl.emergencyalert.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vasyl.emergencyalert.R;

public class ProfileFragment extends Fragment {

    private View view;
    private TextView nameTextView;
    private TextView surnameTextView;
    private String name;
    private String surname;
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
    }

    public void init() {
        sharedPrefs = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        nameTextView = (TextView) view.findViewById(R.id.name_edit_view);
        surnameTextView = (TextView) view.findViewById(R.id.surname_edit_view);
    }

    private void saveProfile() {
        name = nameTextView.getText().toString().trim();
        surname = surnameTextView.getText().toString().trim();
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(getString(R.string.name), name);
        editor.putString(getString(R.string.surname), surname);
        editor.apply();
    }
}
