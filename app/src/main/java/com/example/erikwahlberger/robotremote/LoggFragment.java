package com.example.erikwahlberger.robotremote;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;


import com.example.erikwahlberger.robotremote.dummy.DummyContent;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnLoggFragmentInteractionListener}
 * interface.
 */
public class LoggFragment extends Fragment implements ListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;

    private OnLoggFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private String APP_NAME = "RobotRemote";
    private ArrayList<String> adapterList;
    private ListView mListView;
    private Button sendButton;
    private static BluetoothDevice bluetoothDevice;
    private OutputStream writeStream;
    private EditText sendData;

    private Thread readThread;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private LoggItemAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static LoggFragment newInstance() {
        LoggFragment fragment = new LoggFragment();
        //Bundle args = new Bundle();
       // args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);

        return fragment;
    }

    public void setDevice(BluetoothDevice device)
    {
        bluetoothDevice = device;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LoggFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (getArguments() != null) {
       //     mParam1 = getArguments().getString(ARG_PARAM1);
        //    mParam2 = getArguments().getString(ARG_PARAM2);
        //}

        adapterList = new ArrayList<String>();
        // TODO: Change Adapter to display your content
        mAdapter = new LoggItemAdapter(getActivity().getBaseContext(), adapterList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logg_list, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(R.id.fragmentList);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        sendData = (EditText)view.findViewById(R.id.editData);

        sendButton = (Button)view.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        writeData(writeStream, sendData.getText().toString());
                    }
                }).start();

                sendData.setText("");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sendData.getWindowToken(), 0);

            }
        });

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        mListener.onLoggFragmentInitialized();

        if (bluetoothDevice != null)
        {
            setupConnection();
        }
        else
        {
            Log.e(APP_NAME, "bluetoothDevice was null");
            showToast("bluetoothDevice was null");
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoggFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoggFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onLoggFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public void setupConnection()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    final BluetoothSocket socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
                    socket.connect();
                    Log.i(APP_NAME, "socket.connect()");

                    readThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try
                            {

                                readData(new BufferedReader(new InputStreamReader(socket.getInputStream())));

                            }
                            catch (Exception e)
                            {
                                Log.e(APP_NAME, "Could not start readThread.");
                                showToast("Could not start readThread");
                            }

                        }
                    });

                    readThread.start();

                }
                catch (Exception e)
                {
                    Log.e(APP_NAME, "Could not establish connection");
                    showToast("Could not establish connection");
                }

            }
        }).start();

    }

    public void readData(BufferedReader inStream)
    {
        String inData = null;
        final String sendToUI = null;

        while(true) {
            try {
                Log.i(APP_NAME, "beginRead");

                inData = inStream.readLine();

                //Log.i("RobotRemote", "inData = " + inData);

                if (inData != null && inData != "") {
                    updateAdapter(inData);

                }


            } catch (Exception e) {
                Log.e(APP_NAME, "Could not read data");
                showToast("Could not read data");
            }
        }

    }

    public void writeData(OutputStream stream, String data)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream));
            writer.write(data,0,data.length());
            writer.flush();
            updateAdapter(data);
            //byte[] sendData = data.getBytes();
            //stream.write(sendData);
            //stream.flush();
        }
        catch (Exception e)
        {
            Log.e(APP_NAME, "Could not write data.");
            showToast("Could not write data");
        }


    }

    public void updateAdapter(String data)
    {
        final String sendToUi = data;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.add(sendToUi);
            }
        });
    }

    public void showToast(final String message)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity().getBaseContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoggFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onLoggFragmentInteraction(String id);
        public void onLoggFragmentInitialized();
    }

}
