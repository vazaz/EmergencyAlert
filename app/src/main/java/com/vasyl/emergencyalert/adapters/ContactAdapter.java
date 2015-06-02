package com.vasyl.emergencyalert.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.models.Contact;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sweet Wine on 29.05.2015.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {

    private final Context context;
    List<Contact> items;

    public ContactAdapter(Context context, int resourceId, List<Contact> items) {
        super(context, resourceId, items);
        this.context = context;
        this.items = items;
    }

    public void removeDuplicates() {
        Set<Contact> set =new HashSet<>();
        set.addAll(items);
        items.clear();
        items.addAll(set);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_contact, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.contact_name);
            viewHolder.phone = (TextView) convertView.findViewById(R.id.contact_number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact item = getItem(position);
        viewHolder.name.setText(item.getName());
        viewHolder.phone.setText(item.getPhone());
        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private TextView phone;
    }
}
