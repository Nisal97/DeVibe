package devibe.com.devibe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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

public class ForgotPWActivity extends AppCompatActivity {

    Button btnResetPw;
    EditText forgotPwUsername;

    static String Target_Translate = "Translate";
    static String Target_Alpha = "Alpha";
    String target_op = Target_Translate; //dummy default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pw);


        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

        btnResetPw = (Button)findViewById(R.id.btn_reset_pw);
        forgotPwUsername = (EditText)findViewById(R.id.forgot_pw_username);
        btnResetPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(forgotPwUsername.getText().toString().equalsIgnoreCase("")){
                   AlertDialog.Builder alertdialog = new AlertDialog.Builder(ForgotPWActivity.this);
                   alertdialog.setTitle("Error");
                   alertdialog.setMessage("Please enter username");
                   alertdialog.show();
                }else{
                    target_op = Target_Alpha;
                    v.startAnimation(animAlpha);
                    sendResetPasswordEmail(forgotPwUsername.getText().toString());
                }

            }
        });
    }
    /**
     * This will send a password reset email to the email ties with the username and the user will be notified
     * Even if the username doesnt exist, the user will be notified as an email was sent to the username's email
     * So the user will not know if the username exists or not
     * This is a security issue addressed
     * @param username
     */
    private void sendResetPasswordEmail(String username){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mDatabase.child("users").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = (String) dataSnapshot.child("email").getValue();

                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ForgotPWActivity.this, "Password reset mail sent to email", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(new Intent(ForgotPWActivity.this, SigninActivity.class));
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ForgotPWActivity.this, "Password reset mail sent to email", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
