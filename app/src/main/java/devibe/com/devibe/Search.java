package devibe.com.devibe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Set;

/*
    get the already paired devices of the phone
    Show them to hte user
    When the user clicks on a paired device, its mac address is sent to the controls class for pairing

    HCI issues addressed
        Asks for user permission to access bluetooth
 */

public class Search extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button pairedDevices, btnPair;
    ListView devicelist;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> devices;

    static String Target_Translate = "Translate";
    static String Target_Alpha = "Alpha";
    String target_op = Target_Translate; //dummy default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        devicelist = (ListView)findViewById(R.id.pairedList);
        pairedDevices = (Button)findViewById(R.id.pairedDevice);
        btnPair = (Button)findViewById(R.id.btnPair);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a message. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            finish();
        }
        else
        {
            if (myBluetooth.isEnabled())
            { }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }

        pairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_op = Target_Alpha;
                v.startAnimation(animAlpha);
                pairedDevicesList(); //method that will be called
            }
        });

        btnPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_op = Target_Alpha;
                v.startAnimation(animAlpha);
                SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
                String address = shp.getString("EXTRA_ADDRESS", "");
                startActivity(new Intent(Search.this, Controls.class).putExtra("EXTRA_ADDRESS", address));

            }
        });


        //Drawer Stuff
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void pairedDevicesList() {
        devices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (devices.size() > 0) {
            for (BluetoothDevice bt : devices) {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //Save the address to a sharedPreference file
            SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();
            editor.putString("EXTRA_ADDRESS", address);
            editor.commit();

            // Make an intent to start next activity.
            Intent i = new Intent(Search.this, Controls.class);
            //Change the activity.
            i.putExtra("EXTRA_ADDRESS", address); //this will be received at Controls Activity
            finish();
            startActivity(i);
        }
    };


    //Drawer Stuff
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

      if (id == R.id.controls) {
          SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
          String macAddress = shp.getString("EXTRA_ADDRESS", "");

          if(macAddress.equalsIgnoreCase("")){
              Toast.makeText(this, "No Bluetooth device connected", Toast.LENGTH_SHORT).show();
          }else{
              Intent controls = new Intent(Search.this, Controls.class);
              controls.putExtra("EXTRA_ADDRESS", macAddress);
              startActivity(controls);
          }
        } else if (id == R.id.faq) {
          Intent FAQ = new Intent(Search.this,FAQ.class);
          startActivity(FAQ);
        } else if (id == R.id.advanced_settings) {
          Intent settings = new Intent(Search.this,Settings.class);
          startActivity(settings);
        } else if (id == R.id.feedback) {
          Intent feedback = new Intent(Search.this,Feedback.class);
          startActivity(feedback);
        } else if (id == R.id.signout) {
          FirebaseAuth.getInstance().signOut();
          SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
          SharedPreferences.Editor editor = shp.edit();
          editor.putBoolean("loggedIn", false);
          editor.apply();
          finish();
          startActivity(new Intent(Search.this, SigninActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
