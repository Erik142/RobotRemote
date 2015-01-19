package com.example.erikwahlberger.robotremote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import javax.xml.datatype.Duration;

/**
 * Created by erikwahlberger on 2015-01-16.
 */
public class BluetoothCOM {

    public int REQUEST_ENABLE_BT = 1;
    private String APP_NAME = "RobotRemote";

    private BluetoothAdapter defaultAdapter;
    private BluetoothSocket bluetoothConnection;
    private Activity appActivity;
    private BluetoothCOMInterface mInterface;

    public BluetoothCOM (Activity a)
    {
        try
        {
            appActivity = a;
            defaultAdapter = BluetoothAdapter.getDefaultAdapter();

            if (defaultAdapter != null)
            {
                if (!defaultAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    appActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
            else
            {
                throw new NullPointerException("Device does not support bluetooth");
            }
        }
        catch (Exception e)
        {
            Log.e(APP_NAME, e.toString());
        }

    }



    public boolean initConnection()
    {
        try
        {

            return true;

        }
        catch (Exception e)
        {
            return false;
        }
    }

    public String[] getDevices()
    {

        return null;
    }

    public void readCOMData()
    {

    }

    public boolean writeCOMData(String Data)
    {

        return false;
    }

    public ArrayList<BluetoothDevice> getPairedDevices()
    {
        return (ArrayList<BluetoothDevice>)defaultAdapter.getBondedDevices();
    }

    public ArrayList<BluetoothDevice> getAllAvailableDevices()
    {
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
                {

                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                {

                }
                else if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice)intent.getParcelableExtra(action);

                    showToast("Found device " + bluetoothDevice.getName());

                    mInterface.foundDevice(bluetoothDevice, false);
                }
                else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
                {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice)intent.getParcelableExtra(action);

                    showToast("Device connected: " + bluetoothDevice.getName());

                    mInterface.foundDevice(bluetoothDevice, true);
                }
            }
        };

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        appActivity.registerReceiver(mReceiver, filter);

        defaultAdapter.startDiscovery();

        return null;
    }

    private void showToast(String message)
    {
        Toast toast = new Toast(appActivity);
        toast.setText(message);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public interface BluetoothCOMInterface {
        public void foundDevice(BluetoothDevice device, boolean paired);
    }

}
