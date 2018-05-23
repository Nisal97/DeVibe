package devibe.com.devibe;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class FAQ extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);


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
        getMenuInflater().inflate(R.menu.faq, menu);
        return true;
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addDevice) {
            Intent add = new Intent(FAQ.this,Search.class);
            startActivity(add);
        } else if (id == R.id.controls) {
            SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
            String macAddress = shp.getString("EXTRA_ADDRESS", "");

            if(macAddress.equalsIgnoreCase("")){
                Toast.makeText(this, "No Bluetooth device connected", Toast.LENGTH_SHORT).show();
            }else{
                Intent controls = new Intent(FAQ.this,Controls.class);
                controls.putExtra("EXTRA_ADDRESS", macAddress);
                startActivity(controls);
            }
        } else if (id == R.id.advanced_settings) {
            Intent settings = new Intent(FAQ.this,Settings.class);
            startActivity(settings);
        } else if (id == R.id.feedback) {
            Intent feedback = new Intent(FAQ.this,Feedback.class);
            startActivity(feedback);
        } else if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();
            editor.putBoolean("loggedIn", false);
            editor.apply();
            finish();
            startActivity(new Intent(FAQ.this, SigninActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
