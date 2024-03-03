package com.example.test.fragments;

import android.app.ProgressDialog;
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

public class HomeFragment extends Fragment {
    private FirebaseAuth mauth;
    HashMap<String, Object> map = new HashMap<>();
    private FirebaseDatabase mdatabase= FirebaseDatabase.getInstance();
    private DatabaseReference mDataref = mdatabase.getReference("users");
    private ChildEventListener mDatabaseListener;
    LinearLayout l_balancebg;
    TextView bgnot,txt_blnc,t1,t2;
    String userEmail , balance;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        View view=inflater.inflate(R.layout.fragment_home, container, false);

        l_balancebg = view.findViewById(R.id.l_balancebg);
        txt_blnc=view.findViewById(R.id.txt_bln);

        bgnot = view.findViewById(R.id.bgnot);
        Utils.roundBG(l_balancebg,Utils.clr30,10,5);
        Utils.roundBG(bgnot,Utils.clrAccent,10,5);
       Utils.longProg(requireContext());
        Bundle args = getArguments();
        if (args != null) {
             userEmail = args.getString("userEmail");
        }
        FirebaseApp.initializeApp(requireContext());
        mauth = FirebaseAuth.getInstance();
     initDatabase();
     mDataref.addChildEventListener(mDatabaseListener);
     txt_blnc.setText(balance);
        l_balancebg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(getContext(),WalletActivity.class);
                i.putExtra("email",userEmail);
                startActivity(i);
            }
        });

        return  view;

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
                    Double db = Double.parseDouble(childValue.get("wallet").toString());
                    txt_blnc.setText(Utils.setDoubleToString(db));
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
                    Double db = Double.parseDouble(childValue.get("wallet").toString());
                    txt_blnc.setText(Utils.setDoubleToString(db));
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