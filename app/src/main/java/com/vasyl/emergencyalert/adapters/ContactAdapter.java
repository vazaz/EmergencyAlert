package com.vasyl.emergencyalert.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.vasyl.emergencyalert.R;
import com.vasyl.emergencyalert.models.Contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sweet Wine on 29.05.2015.
 */
public class ContactAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private List<Contact> contacts;
    private List<Contact> filteredContacts = new ArrayList<>();
    private ContactFilter filter;
    private LayoutInflater mInflater;

    public ContactAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    public void removeDuplicates() {
        Set<Contact> set = new HashSet<>();
        set.addAll(contacts);
        contacts.clear();
        contacts.addAll(set);
    }

    public void remove(int position) {
        contacts.remove(position);
    }

    public void add(Contact contact) {
        contacts.add(contact);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ContactFilter();
        }
        return filter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_contact, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.name = (TextView) convertView.findViewById(R.id.contact_name);
            viewHolder.phone = (TextView) convertView.findViewById(R.id.contact_phone);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact item = contacts.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.phone.setText(item.getPhone());
        return convertView;
    }

    private static class ViewHolder {

        public ImageView avatar;
        private TextView name;
        private TextView phone;
    }

    private class ContactFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String constraint = charSequence.toString().toLowerCase();

            FilterResults results = new FilterResults();

            List<Contact> list = contacts;

            int count = list.size();
            ArrayList<Contact> nlist = new ArrayList<>(count);

            for (Contact contact : list) {
                String filterableString = contact.toString().toLowerCase();
                if (filterableString.contains(constraint)) {
                    nlist.add(contact);
                }
            }

            results.values = nlist;
            Log.e("results values", results.values.toString());
            results.count = nlist.size();

            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                Log.e("results values", charSequence.toString());
            filteredContacts = (ArrayList<Contact>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}