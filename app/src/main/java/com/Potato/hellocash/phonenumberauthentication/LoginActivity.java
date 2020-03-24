package com.Potato.hellocash.phonenumberauthentication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Potato.hellocash.R;
import com.Potato.hellocash.databaseobjects.AccountInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    String complete_phone_number;
    String password;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private DatabaseReference mDBref;

    private EditText mCountryCode;
    private EditText mPhoneNumber;
    private EditText mPassword;

    private Button mGenerateBtn;
    private ProgressBar mLoginProgress;

    private TextView mLoginFeedbackText;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mCountryCode = findViewById(R.id.country_code_text);
        mPhoneNumber = findViewById(R.id.phone_number_text);
        mGenerateBtn = findViewById(R.id.generate_btn);
        mLoginProgress = findViewById(R.id.login_progress_bar);
        mLoginFeedbackText = findViewById(R.id.login_form_feedback);
        mPassword = findViewById(R.id.password_text);

        final boolean isSignup = getIntent().getBooleanExtra("signup",true);


        mGenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String country_code = mCountryCode.getText().toString();
                String phone_number = mPhoneNumber.getText().toString();
                password = mPassword.getText().toString();

                complete_phone_number = "+" + country_code + phone_number;

                if (country_code.isEmpty() || phone_number.isEmpty() || password.isEmpty()) {
                    mLoginFeedbackText.setText("Please fill in the form to continue.");
                    mLoginFeedbackText.setVisibility(View.VISIBLE);
                } else if (password.length() < 7) {
                    mLoginFeedbackText.setText("Password length should be greater than 7.");
                    mLoginFeedbackText.setVisibility(View.VISIBLE);
                } else {
                    if(isSignup){
                        mLoginProgress.setVisibility(View.VISIBLE);
                        mGenerateBtn.setEnabled(false);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                complete_phone_number,
                                60,
                                TimeUnit.SECONDS,
                                LoginActivity.this,
                                mCallbacks
                        );
                    }else{
                        verifyUser();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mLoginFeedbackText.setText("Verification Failed, please try again.");
                mLoginFeedbackText.setVisibility(View.VISIBLE);
                mLoginProgress.setVisibility(View.INVISIBLE);
                mGenerateBtn.setEnabled(true);
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent otpIntent = new Intent(LoginActivity.this, OtpActivity.class);
                                otpIntent.putExtra("AuthCredentials", s);
                                startActivity(otpIntent);
                            }
                        },
                        10000);
            }
        };


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mCurrentUser != null){
            sendUserToHome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUserToDataBase();
                            sendUserToHome();
                            // ...
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mLoginFeedbackText.setVisibility(View.VISIBLE);
                                mLoginFeedbackText.setText("There was an error verifying OTP");
                            }
                        }
                        mLoginProgress.setVisibility(View.INVISIBLE);
                        mGenerateBtn.setEnabled(true);
                    }
                });
    }

    private void sendUserToHome() {
        Intent homeIntent = new Intent(LoginActivity.this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void addUserToDataBase(){
        AccountInformation newAccount = new AccountInformation();
        newAccount.setCredit(0);
        newAccount.setPaired(false);
        newAccount.setPhoneNumber(complete_phone_number);
        newAccount.setPassword(password);
        mDBref = FirebaseDatabase.getInstance().getReference().child("AccountInformation");

        mDBref.push().setValue(newAccount);
    }

    private void verifyUser(){
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("AccountInformation").orderByChild("phoneNumber").equalTo(complete_phone_number);
        query.addValueEventListener( new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    //TODO get the data here
                    String found = (String)postSnapshot.child("password").getValue();
                    if(!password.equals(found)){
                        mLoginFeedbackText.setText("Invalid password");
                        mLoginFeedbackText.setVisibility(View.VISIBLE);
                    }
                    else{
                        sendUserToHome();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                mLoginFeedbackText.setText("Invalid mobile number.");
                mLoginFeedbackText.setVisibility(View.VISIBLE);
            }
        });
    }
}
