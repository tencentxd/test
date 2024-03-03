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
import com.example.test.ScaleActivity;
import com.example.test.Utils;
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
import static com.example.test.Utils.*;

public class CasinoFragment extends Fragment {
    private FirebaseAuth mauth;
    HashMap<String, Object> map = new HashMap<>();
    private FirebaseDatabase mdatabase= FirebaseDatabase.getInstance();
    private DatabaseReference mDataref = mdatabase.getReference("users");
    private ChildEventListener mDatabaseListener;
     LinearLayout game1,game2;
     TextView txt_balnce;
    private  String uid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_casino, container, false);
        longProg(requireContext());
        game1 = view.findViewById(R.id.game1);
        game2 = view.findViewById(R.id.game2);
        txt_balnce= view.findViewById(R.id.txt_blnce);
        Bundle args = getArguments();
        if (args != null) {
            uid = args.getString("userEmail");

        }

        FirebaseApp.initializeApp(requireContext());
        mauth = FirebaseAuth.getInstance();
        initDatabase();
        mDataref.addChildEventListener(mDatabaseListener);

        game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), ScaleActivity.class));
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
                    txt_balnce.setText(Utils.setDoubleToString(db));


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
                    txt_balnce.setText(Utils.setDoubleToString(db));


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