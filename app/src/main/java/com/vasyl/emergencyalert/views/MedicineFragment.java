package com.vasyl.emergencyalert.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.models.Contact;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MedicineFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{

    private RadioGroup mRadioGroup;
    private String disease;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);
        mRadioGroup = (RadioGroup) view.findViewById(R.id.diseases);
        mRadioGroup.setOnCheckedChangeListener(this);
        return view;
    }

    private void saveDiseaseSharedPrefernces() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        editor.putString(getString(R.string.disease), disease);
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveDiseaseSharedPrefernces();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.radio_heart:
                disease = getString(R.string.heart_problems);
                break;
            case R.id.radio_epilepsy:
                disease = getString(R.string.epilepsy);
                break;
            case R.id.radio_parkinson:
                disease = getString(R.string.parkinson);
                break;
            case R.id.radio_alzheimer:
                disease = getString(R.string.alzheimer);
                break;
            case R.id.radio_other:
                disease = getString(R.string.other);
                break;
        }
    }
}
