package com.Potato.hellocash.Menu;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.Potato.hellocash.GlobalVariables;
import com.Potato.hellocash.QR.QrReadActivity;
import com.Potato.hellocash.R;
import com.Potato.hellocash.phonenumberauthentication.SigninMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private Button mLogoutBtn;
    private Button mScanqrBtn;
    private Button mCurrentstatusBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

        mLogoutBtn = findViewById(R.id.logOut);
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendUserToLogin();
            }
        });

        mCurrentstatusBtn = findViewById(R.id.currentStatus);
        mCurrentstatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayStatus();
            }
        });

        mScanqrBtn = findViewById(R.id.readQr);
        mScanqrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readQR();
            }
        });
    }

    private void readQR() {
        Intent readQR = new Intent(MainActivity.this, QrReadActivity.class);
        readQR.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        readQR.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(readQR);
        finish();
    }

    private void displayStatus() {

        final String result =  Long.toString(((GlobalVariables) getApplicationContext()).currentUser.getCredit());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setMessage(result);
        AlertDialog alert1 = builder.create();
        alert1.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(mCurrentUser != null){
//
//        }else{
//            sendUserToLogin();
//        }
    }

    private void sendUserToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, SigninMenuActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


}
