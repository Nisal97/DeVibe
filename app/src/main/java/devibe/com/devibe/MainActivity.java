package devibe.com.devibe;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.*;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.support.v7.app.AlertDialog.*;

public class MainActivity extends AppCompatActivity {

    private static int splash_timeout = 1500;
    SharedPreferences shp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!haveNetworkConnection()) {
            showDialog();
        } else {
            shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);

            //Connecting to the splack screen
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //If logged in previously, the user will automatically be signed in and directed to the search device class.
                    //This is an HCI issue addressed
                    if (loggedIn()) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        String email = shp.getString("email", "");
                        String password = shp.getString("password", "");
                        automaticSignIn(email, password);

                        startActivity(new Intent(MainActivity.this, Search.class));
                        finish();

                    } else {
                        Intent home_intent = new Intent(MainActivity.this, SigninActivity.class);
                        startActivity(home_intent);
                        finish();
                    }
                }
            }, splash_timeout);
        }

    }

    /**
     * Checks from the local database if the user has logged out or not before
     * @return - returns if logged in or not
     */
    private Boolean loggedIn() {
        shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
        Boolean loggedIn = shp.getBoolean("loggedIn", false);

        return loggedIn;
    }

    /**
     * This is the method that will sign in the user with the firebase authentication.
     * The local sql database will be checked for details if the person has not logged out before.
     * The password of the user is not stored in the firebase database
     * This makes sure that the password is not visible to anyone who accesses the database
     * This is a security issue addressed
     * @param email - email of the user
     * @param password - password taken from the local database
     */
    private void automaticSignIn(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(MainActivity.this, Search.class));

                        } else {
                        }
                    }
                });
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = connManager.getAllNetworkInfo();
        for (NetworkInfo n : netInfo) {
            if (n.getTypeName().equalsIgnoreCase("WIFI"))
                if (n.isConnected()) haveConnectedWifi = true;

            if (n.getTypeName().equalsIgnoreCase("MOBILE"))
                if (n.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void showDialog() {
        Builder builder = new Builder(this);
        builder.setMessage("No internet connection")
                .setCancelable(false)
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
