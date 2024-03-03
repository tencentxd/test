package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WalletActivity extends AppCompatActivity {
    LinearLayout l_wallet;
    String email;
    TextView txt_balance,t1,t2;
    Button btn_deposit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        l_wallet=findViewById(R.id.l_wallet);
        txt_balance=findViewById(R.id.txt_blnc);
        btn_deposit=findViewById(R.id.btn_deposit);
        t1=new TextView(this);
        t2=new TextView(this);
        email=getIntent().getStringExtra("email");




        btn_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


    }
    void showDialog(){
        CustomDepositDialog customDepositDialog = new CustomDepositDialog(this);

        // Show the dialog
        customDepositDialog.show();
    }



}