package devibe.com.devibe;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import devibe.com.devibe.Model.Customer;

public class SigninActivity extends AppCompatActivity {

    public EditText userName;
    public EditText loginPassword;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    Button btnSignIn;
    TextView btnForgotPw;
    SharedPreferences shp;
    static String Target_Translate = "Translate";
    static String Target_Alpha = "Alpha";
    String target_op = Target_Translate; //dummy default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);


        userName = (EditText) findViewById(R.id.signin_username);
        loginPassword = (EditText) findViewById(R.id.signin_password);

        //Initialize firebase database and get the instance of the authentication server
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //Setting a button to go to sign up page
        TextView signupTxt = (TextView) findViewById(R.id.signin_signup);
        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_op = Target_Alpha;
                v.startAnimation(animAlpha);
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
            }
        });

        btnForgotPw = (TextView)findViewById(R.id.forgotpw);
        btnForgotPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_op = Target_Alpha;
                v.startAnimation(animAlpha);
                startActivity(new Intent(SigninActivity.this, ForgotPWActivity.class));
            }
        });

        btnSignIn = (Button) findViewById(R.id.signin_button);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                target_op = Target_Alpha;
                v.startAnimation(animAlpha);

            loginUser();

            }
        });
    }

    /**
     * Logs in the user after checking if there is a user with the username in the database
     * If exists, the email registered with that username will be retrieved and sent to the authentication server for login along with the password.
     * The same error message will be shown if either the email or the password is wrong. This is a security issue addressed
     * The toasts shown to the user is an HCI issue addressed - Error Tolerance
     */
    private void loginUser(){

        if(!fieldsEmpty()) {
            final String username = userName.getText().toString();

            mDatabase.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String password = loginPassword.getText().toString();
                        String email = (String) dataSnapshot.child("email").getValue();

                        authSignIn(email, password);
//                        logDialog.dismiss();
                    } else {
                        Toast.makeText(SigninActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
//                        logDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Checks if any of the input fields are empty
     *
     * @return
     */
    private Boolean fieldsEmpty() {
        if (userName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return true;
        } else if (loginPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * Sign in to through firebase authentication
     * After successful login, the details are saved in the local database.
     * The toasts shown is an HCI issue addressed
     * @param email
     * @param password
     */
    private void authSignIn(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveAsLogged(email, password);
                            finish();
                            startActivity(new Intent(SigninActivity.this, Search.class));

                            Toast.makeText(SigninActivity.this, "Signed In", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SigninActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Details are saved to the local database
     * @param email
     * @param password
     */
    private void saveAsLogged(String email, String password){
        shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("loggedIn", true);
        editor.putString("EXTRA_ADDRESS", "");
        editor.apply();
    }
}
