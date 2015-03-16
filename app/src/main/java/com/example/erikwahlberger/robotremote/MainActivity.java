package com.example.erikwahlberger.robotremote;

import android.app.Dialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity implements multiInterface {

    private int REQUEST_ENABLE_BT = 1;
    private String APP_NAME = "RobotRemote";
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData = {"Bluetooth-enheter", "Loggläge"};
    private BluetoothAdapter defaultAdapter;

    private BluetoothListFragment btFragment;
    private LoggFragment loggFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (toolbar != null) {
            toolbar.setTitle("Robot Remote");
            setSupportActionBar(toolbar);
        }
        initDrawer();

        btFragment = BluetoothListFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.content_frame,btFragment).commit();

    }

    private void initView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.ToolBar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter=new ArrayAdapter<String>( MainActivity.this, R.layout.navigation_drawer_item, leftSliderData);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);

        leftDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setFragment(position);
            }
        });
    }

    public void setFragment(int position)
    {

        Fragment returnFragment = null;
        FragmentManager fragmentManager = getFragmentManager();

        Log.i(APP_NAME, "setFragment, position = " + position);

        switch (position) {
            case 0:
                btFragment = BluetoothListFragment.newInstance();
                returnFragment = btFragment;
                break;
            case 1:
                loggFragment = LoggFragment.newInstance();
                returnFragment = loggFragment;
                break;
            default:
                break;
        }

        leftDrawerList.setItemChecked(position, true);
        drawerLayout.closeDrawers();
        fragmentManager.beginTransaction().replace(R.id.content_frame,returnFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT)
        {
            if (resultCode == RESULT_OK)
            {
                btFragment.onBluetoothToggle();
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoggFragmentInteraction(String id) {

    }

    @Override
    public void onBluetoothFragmentInteraction(BluetoothDevice clickedDevice)
    {
        FragmentManager fragmentManager = getFragmentManager();

        loggFragment = LoggFragment.newInstance();
        loggFragment.setDevice(clickedDevice);

        Log.i(APP_NAME, "onBluetoothFragmentInteraction(), clickedDevice");

        fragmentManager.beginTransaction().replace(R.id.content_frame,loggFragment).commit();

    }

    @Override
    public void onBluetoothFragmentInitialized() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if (defaultAdapter != null)
        {
            if (!defaultAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else {
                btFragment.onBluetoothToggle();
            }

        }

    }

    @Override
    public void onLoggFragmentInitialized() {

    }


}
