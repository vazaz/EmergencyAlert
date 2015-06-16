package com.vasyl.emergencyalert.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vasyl.emergencyalert.fragments.ContactsFragment;
import com.vasyl.emergencyalert.fragments.ProfileFragment;

/**
 * Created by vasyl on 5/19/15.
 */
public class SlideViewPagerAdapter extends FragmentPagerAdapter {

    public SlideViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Profile";
            case 1:
                return "Contacts";
            default:
                return "Profile";
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ProfileFragment();
            case 1:
                return new ContactsFragment();
//            case 2:
//                return new MedicineFragment();
            default:
                return new ContactsFragment();
        }
    }
}
