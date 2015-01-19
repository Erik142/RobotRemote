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
 * Created by erikwahlberger on 2015-01-16.
 */
public class BluetoothItemAdapter extends ArrayAdapter<BluetoothDevice> {

    public BluetoothItemAdapter(Context context, ArrayList<BluetoothDevice> deviceObjects) {
        super(context, 0, deviceObjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BluetoothDevice device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_list_item, parent, false);
        }

        TextView deviceName = (TextView)convertView.findViewById(R.id.bluetoothName);
        TextView deviceMAC = (TextView)convertView.findViewById(R.id.bluetoothMac);

        deviceName.setText(device.getName());
        deviceMAC.setText(device.getAddress());

        return convertView;


    }
}
