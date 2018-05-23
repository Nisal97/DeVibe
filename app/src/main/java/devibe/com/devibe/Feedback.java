package devibe.com.devibe;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class Feedback extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button submit;
    Button goWeb;
    TextView to;
    EditText name;
    EditText email;
    EditText message;


    static String Target_Translate = "Translate";
    static String Target_Alpha = "Alpha";
    String target_op = Target_Translate; //dummy default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        submit = (Button) findViewById(R.id.submit);

        to = (TextView) findViewById(R.id.to);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        message = (EditText) findViewById(R.id.message);
        goWeb = (Button)findViewById(R.id.visitweb);

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                target_op = Target_Alpha;
                view.startAnimation(animAlpha);

                String sendS = to.getText().toString();
                String nameS = name.getText().toString();
                String emailS = email.getText().toString();
                String messageS = message.getText().toString();

                Intent emailfinal = new Intent(Intent.ACTION_SEND);

                emailfinal.putExtra(Intent.EXTRA_EMAIL, new String[]{sendS});
                emailfinal.putExtra(Intent.EXTRA_TEXT, nameS);
                emailfinal.putExtra(Intent.EXTRA_SUBJECT, emailS);
                emailfinal.putExtra(Intent.EXTRA_TEXT, messageS);
                emailfinal.setType("message/rfc822");
                startActivity(Intent.createChooser(emailfinal, "Choose an app to send the Email"));


            }
        });

        goWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse("https://teamleviathansiit.wixsite.com/devibe"));
                    startActivity(intent);
                } catch (ActivityNotFoundException exception) {
                    Toast.makeText(Feedback.this,"Web SIte Error", Toast.LENGTH_LONG).show();
                }
            }
        });


        //Toolbar Stuff
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
        getMenuInflater().inflate(R.menu.feedback, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.addDevice) {
            Intent add = new Intent(Feedback.this,Search.class);
            startActivity(add);
        } else if (id == R.id.faq) {
            Intent FAQ = new Intent(Feedback.this,FAQ.class);
            startActivity(FAQ);
        } else if (id == R.id.advanced_settings) {
            Intent settings = new Intent(Feedback.this,Settings.class);
            startActivity(settings);
        } else if (id == R.id.controls) {
            SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
            String macAddress = shp.getString("EXTRA_ADDRESS", "");

            if(macAddress.equalsIgnoreCase("")){
                Toast.makeText(this, "No Bluetooth device connected", Toast.LENGTH_SHORT).show();
            }else{
                Intent controls = new Intent(Feedback.this,Controls.class);
                controls.putExtra("EXTRA_ADDRESS", macAddress);
                startActivity(controls);
            }
        } else if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = shp.edit();
            editor.putBoolean("loggedIn", false);
            editor.apply();
            finish();
            startActivity(new Intent(Feedback.this, SigninActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
