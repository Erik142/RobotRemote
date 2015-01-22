package com.example.erikwahlberger.robotremote;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by erikwahlberger on 15-01-22.
 */
public class LoggItemAdapter extends ArrayAdapter<String> {
    public LoggItemAdapter(Context context, ArrayList<String> deviceObjects) {
        super(context, 0, deviceObjects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.logg_list_item, parent, false);
        }

        TextView tmpItem = (TextView)convertView.findViewById(R.id.loggItem);
        tmpItem.setText(item);

        return convertView;


    }
}
