package com.Potato.hellocash.phonenumberauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.Potato.hellocash.R;
import com.Potato.hellocash.phonenumberauthentication.LoginActivity;

public class SigninMenuActivity extends AppCompatActivity {

    private Button mLogin;
    private Button mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_menu);

        mLogin = findViewById(R.id.login_btn);
        mSignup = findViewById(R.id.signup_btn);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLogin();
            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToSignup();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(mCurrentUser == null){
//            sendUserToLogin();
//        }
    }
    
    private void sendUserToSignup() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("signup",true);
        startActivity(intent);
        finish();
    }

    private void sendUserToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("signup",false);
        startActivity(intent);
        finish();
    }
}
