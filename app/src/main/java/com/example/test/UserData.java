package com.example.test;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserData {
    public static void updateUserBalance(double newBalance) {
        // Get the current user's authentication UID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get a reference to the Firebase database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        // Update the user's balance in the database
        userRef.child("wallet").setValue(newBalance)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "User balance updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firebase", "Failed to update user balance: " + e.getMessage());
                    }
                });
    }

}
