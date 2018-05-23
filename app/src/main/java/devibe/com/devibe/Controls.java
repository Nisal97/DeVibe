package devibe.com.devibe;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.UUID;

/**
 * Allows the user to connect the mobile app and the device through bluetooth
 * This will allow him to control the device
 *
 * HCI issues addressed
 *  Self Descriptiveness
 *  Controlability
 */
public class Controls extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Button btnSnooze, btnDisconnect, btnTest;
    Switch switchOutdoor;
    SharedPreferences shp;
    static String Target_Translate = "Translate";
    static String Target_Alpha = "Alpha";
    String target_op = Target_Translate; //dummy default


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        btnDisconnect = (Button)findViewById(R.id.off);
        btnSnooze = (Button)findViewById(R.id.snooze);
        btnTest = (Button)findViewById(R.id.test);
        switchOutdoor = (Switch)findViewById(R.id.outdoor_switch);

        switchOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchOutdoor.isChecked()) {
                    int x = shp.getInt("outdoorValue", 0);
                    passOutdoorVal(String.valueOf(x));
                }else{
                    passOutdoorVal(String.valueOf(0));
                }
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                target_op = Target_Alpha;
                v.startAnimation(animAlpha);

                Disconnect();
            }
        });

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                target_op = Target_Alpha;
                v.startAnimation(animAlpha);

                testMotors();
            }
        });

        btnSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_op = Target_Alpha;
                v.startAnimation(animAlpha);

                Snooze();
            }
        });

        //receive the address of the bluetooth device
        Intent newInt = getIntent();
        address = newInt.getStringExtra("EXTRA_ADDRESS");

        if(address.equalsIgnoreCase("")){
            address = shp.getString("EXTRA_ADDRESS", "");
        }

        if(address.equalsIgnoreCase("")){
            Toast.makeText(Controls.this, "No previous devices paired", Toast.LENGTH_SHORT).show();
            finish();
        }else {

            ConnectBT cbt = new ConnectBT();
            cbt.execute();

            //navigation stuff
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void Disconnect() {
        if (btSocket != null) { //If the btSocket is busy
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                msg("Error");
            }
        }
        startActivity(new Intent(Controls.this, Search.class));
        finish(); //return to the first layout
    }

    private void Snooze() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("8".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void testMotors(){
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("7".getBytes());
                Toast.makeText(Controls.this, "Test", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void passOutdoorVal(String x){
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(x.getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }


    //navigation stuff
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
        getMenuInflater().inflate(R.menu.controls, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addDevice) {
            Intent add = new Intent(Controls.this,Search.class);
            startActivity(add);
        } else if (id == R.id.faq) {
            Intent FAQ = new Intent(Controls.this,FAQ.class);
            startActivity(FAQ);
        } else if (id == R.id.advanced_settings) {
            Intent settings = new Intent(Controls.this,Settings.class);
            startActivity(settings);
        } else if (id == R.id.feedback) {
            Intent feedback = new Intent(Controls.this,Feedback.class);
            startActivity(feedback);
        } else if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();
            editor.putBoolean("loggedIn", false);
            editor.apply();
            finish();
            startActivity(new Intent(Controls.this, SigninActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    Connect with device using a background thread

    HCI issues addressed
        error control
        Error tolerance

     */

    private class ConnectBT extends AsyncTask<Void, Void, Void> { // UI thread
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Controls.this, "Connecting...", "Please wait!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) {//while the progress dialog is shown, the connection is done in background
            try {
                    if (btSocket == null || !isBtConnected) {
                        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM connection
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                        btSocket.connect();//start connection
                    }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {//after the doInBackground, it checks if everything went fine

            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Please turn on the device.");
                Toast.makeText(Controls.this, address, Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(Controls.this, Search.class));
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}


