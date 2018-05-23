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

public class SignupActivity extends AppCompatActivity {

    private EditText txtsecretCode, txtuserName, txtpassword, txtemail, txtrepassword;
    private ProgressDialog dialog;
    Button registerBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;


    static String Target_Translate = "Translate";
    static String Target_Alpha = "Alpha";
    String target_op = Target_Translate; //dummy default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        txtsecretCode = (EditText) findViewById(R.id.signup_secretcode);
        txtuserName = (EditText) findViewById(R.id.signup_username);
        txtpassword = (EditText) findViewById(R.id.signup_password);
        txtrepassword = (EditText) findViewById(R.id.signup_retypepassword);
        txtemail = (EditText) findViewById(R.id.signup_email);

        //Initialize firebase database and authentication instance
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        TextView signinTxt = (TextView) findViewById(R.id.signup_signin);
        signinTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target_op = Target_Alpha;
                v.startAnimation(animAlpha);
                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
            }
        });

        registerBtn = (Button) findViewById(R.id.signupBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                target_op = Target_Alpha;
                v.startAnimation(animAlpha);

                registerUser();

            }
        });

    }

    /**
     * This method first checks is any fields are empty and if the passwords match
     * Then it checks if there is any other user with the given username
     * Finally an authenticated user is created in the database
     * The toasts shown to the user are HCI issues addressed  - Error Tolerance
     */
    private void registerUser() {

        if (!fieldsEmpty() && passwordMatch()) {

            final String code = txtsecretCode.getText().toString();
            //Checks if the unique code matches
            mDatabase.child("codes").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        final Object data = dataSnapshot.getValue(); //This is to set the devices to the newly created user entry

                        final String username = txtuserName.getText().toString();
                        //Checks if the entered username is already taken
                        mDatabase.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(SignupActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();

                                } else {
                                    String email = txtemail.getText().toString();
                                    String password = txtpassword.getText().toString();

                                    createNewAuthUser(email, password, username, data, code);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    } else {
                        Toast.makeText(SignupActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
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
        if (txtuserName.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return true;
        } else if (txtemail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return true;
        } else if (txtpassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return true;
        } else if (txtrepassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "Please retype password", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * First checks if the password has 6 or more characters
     * Then checks if the two entered passwords match
     *
     * @return
     */
    private Boolean passwordMatch() {
        if (txtpassword.getText().toString().length() < 6) {
            Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (txtpassword.getText().toString().equals(txtrepassword.getText().toString())) {
                return true;
            }
        }

        Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * Creates a new authenticate user with email and password in firebase authentication
     * @param email
     * @param password
     */
    private void createNewAuthUser(final String email, final String password, final String username, final Object devices, final String code) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    User user = new User(username, email, devices);
                    mDatabase.child("users").child(username).setValue(user.getData());
                    mDatabase.child("users").child(username).child("email").setValue(user.getEmail());
                    mDatabase.child("users").child(username).child("username").setValue(user.getUsername());
                    mDatabase.child("codes").child(code).removeValue();

                    Toast.makeText(SignupActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                    saveAsLogged(email, password); //Saves the user as logged in
                    finish();
                    startActivity(new Intent(SignupActivity.this, Search.class));

                } else {
                    Toast.makeText(SignupActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Details are saved to the local database
     * @param email
     * @param password
     */
    private void saveAsLogged(String email, String password) {
        SharedPreferences shp = getSharedPreferences("com.devibe.app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putBoolean("loggedIn", true);
        editor.putString("EXTRA_ADDRESS", "");
        editor.apply();
    }
}
