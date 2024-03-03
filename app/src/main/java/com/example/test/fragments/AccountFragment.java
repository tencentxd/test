package com.example.test.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.test.R;

import com.example.test.Utils;
import com.example.test.WalletActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class AccountFragment extends Fragment {
    private FirebaseAuth mauth;
    HashMap<String, Object> map = new HashMap<>();
    private FirebaseDatabase mdatabase= FirebaseDatabase.getInstance();
    private DatabaseReference mDataref = mdatabase.getReference("users");
    private ChildEventListener mDatabaseListener;
    TextView txt_id,txt_email,txt_balance,txt_doj;
    LinearLayout l_transcations,l_wallet,l_support,l_cpass,l_logout;
    String userEmail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        txt_id = view.findViewById(R.id.txt_userid);
        txt_email  = view.findViewById(R.id.txt_email);
        txt_balance  = view.findViewById(R.id.txt_balance);
        txt_doj  = view.findViewById(R.id.txt_doj);
        l_cpass = view.findViewById(R.id.l_cpass);
        l_logout = view.findViewById(R.id.l_logout);
        l_support = view.findViewById(R.id.l_support);
        l_wallet= view.findViewById(R.id.l_wallet);
        l_logout = view.findViewById(R.id.l_logout);
        l_transcations = view.findViewById(R.id.l_transcations);
        Utils.roundBG(l_cpass,Utils.clr20,50,5);
        Utils.roundBG(l_logout,Utils.clr20,50,5);
        Utils.roundBG(l_support,Utils.clr20,50,5);
        Utils.roundBG(l_wallet,Utils.clr20,50,5);
        Utils.roundBG(l_transcations,Utils.clr20,50,5);

        Utils.roundBG(txt_balance,Utils.clrAccent,20,5);
        Bundle args = getArguments();


        FirebaseApp.initializeApp(requireContext());
        mauth = FirebaseAuth.getInstance();
        initDatabase();
        mDataref.addChildEventListener(mDatabaseListener);


        if (args != null) {
             userEmail = args.getString("userEmail");
            Utils.showMsg(requireContext(),args.getString("userEmail"));

        } else{
            Utils.showMsg(requireContext(),"Null");
        }
        l_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getContext(),WalletActivity.class);
                i.putExtra("email",userEmail);
                startActivity(i);
            }
        });

        return view;
    }

    private  void initDatabase(){
        mDatabaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final String childkey = snapshot.getKey();
                GenericTypeIndicator<HashMap<String, Object>> ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final HashMap<String, Object> childValue = snapshot.getValue(ind);
                Date currentDate = new Date();

                // Format the date as "dd-MM-yy"
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
                String formattedDate = sdf.format(currentDate);
                map = new HashMap<>();

                if (childkey.equals(mauth.getCurrentUser().getUid())){
                   txt_email.setText(childValue.get("email").toString());
                    txt_id.setText(childValue.get("uid").toString());
                    Double db = Double.parseDouble(childValue.get("wallet").toString());
                    txt_balance.setText(Utils.setDoubleToString(db));
                    txt_doj.setText(childValue.get("doj").toString());
                } else {

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final String childkey = snapshot.getKey();
                GenericTypeIndicator<HashMap<String, Object>> ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final HashMap<String, Object> childValue = snapshot.getValue(ind);
                Date currentDate = new Date();

                // Format the date as "dd-MM-yy"
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
                String formattedDate = sdf.format(currentDate);
                map = new HashMap<>();

                if (childkey.equals(mauth.getCurrentUser().getUid())){
                    txt_email.setText(childValue.get("email").toString());
                    txt_id.setText(childValue.get("uid").toString());
                    Double db = Double.parseDouble(childValue.get("wallet").toString());
                    txt_balance.setText(Utils.setDoubleToString(db));
                    txt_doj.setText(childValue.get("doj").toString());
                } else {

                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

}