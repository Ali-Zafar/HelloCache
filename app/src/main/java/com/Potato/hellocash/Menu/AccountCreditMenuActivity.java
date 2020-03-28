package com.Potato.hellocash.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.Potato.hellocash.GlobalVariables;
import com.Potato.hellocash.R;
import com.Potato.hellocash.databaseobjects.AccountInformation;
import com.Potato.hellocash.phonenumberauthentication.SigninMenuActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class AccountCreditMenuActivity extends AppCompatActivity {

    Button withdraw;
    Button send;
    Button change;
    Button logout;

    Long currentAmmount = 0l;

    //send
    String reciver = "";
    Long sentAmmount = 0l;
    Long reciverCurrentStatus = 0l;
    boolean exists = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);
        //-----------------------
        AccountInformation i = ((GlobalVariables)getApplicationContext()).currentUser;
        i.setPhoneNumber("+923312205777");

        Button sendDialogueBox = (Button) findViewById(R.id.send);
        sendDialogueBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(AccountCreditMenuActivity.this);
                View aview = getLayoutInflater().inflate(R.layout.activity_send_credit,null);
                final EditText mphone  = (EditText) aview.findViewById(R.id.editText2);
                final EditText amount  = (EditText) aview.findViewById(R.id.amountNumber);
                Button msend  = (Button) aview.findViewById(R.id.sendCreditBtn);

                msend.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if(!mphone.getText().toString().isEmpty() && !amount.getText().toString().isEmpty()){
                            sentAmmount = Long.valueOf(amount.getText().toString());
                            reciver = mphone.getText().toString();
                            userExists(reciver);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            recieverCurrentCredit();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sendCredit(sentAmmount);
                            Toast.makeText(AccountCreditMenuActivity.this, R.string.success,Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(AccountCreditMenuActivity.this, R.string.faliure,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mbuilder.setView(aview);
                AlertDialog dialog = mbuilder.create();
                dialog.show();
            }
        });
        //------------------------




        change = findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(AccountCreditMenuActivity.this);
                View aview = getLayoutInflater().inflate(R.layout.activity_withdraw_credit,null);
                final EditText amount  = (EditText) aview.findViewById(R.id.amountNumber);
                Button msend  = (Button) aview.findViewById(R.id.withdrawCreditBtn);

                msend.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if( !amount.getText().toString().isEmpty()){
                            withdrawAmmount(Long.valueOf(amount.getText().toString()));
                            Toast.makeText(AccountCreditMenuActivity.this, R.string.success,Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(AccountCreditMenuActivity.this, R.string.faliure,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mbuilder.setView(aview);
                AlertDialog dialog = mbuilder.create();
                dialog.show();
            }
        });
        //------------------------
        withdraw = findViewById(R.id.withDraw);
        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mbuilder = new AlertDialog.Builder(AccountCreditMenuActivity.this);
                View aview = getLayoutInflater().inflate(R.layout.activity_withdraw_credit,null);
                final EditText amount  = (EditText) aview.findViewById(R.id.withdrawAmount);
                Button msend  = (Button) aview.findViewById(R.id.withdrawCreditBtn);

                msend.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if( !amount.getText().toString().isEmpty()){
                            withdrawAmmount(Long.valueOf(amount.getText().toString()));
                            Toast.makeText(AccountCreditMenuActivity.this, R.string.success,Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Toast.makeText(AccountCreditMenuActivity.this, R.string.faliure,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mbuilder.setView(aview);
                AlertDialog dialog = mbuilder.create();
                dialog.show();
            }
        });

        logout = findViewById(R.id.logOut);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(AccountCreditMenuActivity.this,SigninMenuActivity.class );
                startActivity(homeIntent);
                finish();
            }
        });

        currentCredit();
    }

    private void sendAmmount(Long ammount) {
        if(verifyCredit(ammount))
            ;
    }

    private boolean verifyCredit(Long ammount) {
        if(ammount>currentAmmount){
            Toast.makeText(getApplicationContext(),"You don't have enough credit to" +
                    " process this transaction", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void changeAmmount(Long ammount) {
        if(verifyCredit(ammount))
            transaction(ammount);
    }

    private void withdrawAmmount(Long ammount) {
        if(verifyCredit(ammount))
            transaction(ammount);
    }

    private void takeInput() {

    }

    private void transaction(Long ammount){
        DatabaseReference mDBref = FirebaseDatabase.getInstance().getReference("AccountInformation").
                child(((GlobalVariables)getApplicationContext()).currentUser.getPhoneNumber());
        mDBref.child("credit").setValue(currentAmmount-ammount);
    }

    private void sendCredit(Long ammount){

        if(exists && verifyCredit(ammount)){
            //get the current amount of this user and then update his balance
             DatabaseReference mDBref = FirebaseDatabase.getInstance().getReference("AccountInformation");
            mDBref.child(reciver).child("credit").setValue(reciverCurrentStatus+ammount);
            //then update current user credit
            transaction(ammount);
        }
    }

    private void userExists(String number) {
        DatabaseReference mDBref = FirebaseDatabase.getInstance().getReference("AccountInformation");

        mDBref.orderByChild("phoneNumber").equalTo(number)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            exists = true;
                        } else {
                            Toast.makeText(getApplicationContext(),"This number is not" +
                                    " registered", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
        });
    }

    private void currentCredit(){
        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("AccountInformation").orderByChild("phoneNumber").
                equalTo(((GlobalVariables)getApplicationContext()).currentUser.getPhoneNumber());
        query.addValueEventListener( new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    //TODO get the data here
                    currentAmmount = (Long)postSnapshot.child("credit").getValue();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
//                mLoginFeedbackText.setText("Invalid mobile number.");
//                mLoginFeedbackText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void recieverCurrentCredit(){
        if(exists) {
            DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
            Query query = mFirebaseDatabaseReference.child("AccountInformation").orderByChild("phoneNumber").
                    equalTo(reciver);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        //TODO get the data here
                        reciverCurrentStatus = (Long) postSnapshot.child("credit").getValue();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("fail");
//                mLoginFeedbackText.setText("Invalid mobile number.");
//                mLoginFeedbackText.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
