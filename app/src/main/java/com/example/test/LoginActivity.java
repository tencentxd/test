package com.example.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import static com.example.test.Utils.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
   LinearLayout bg, l_email,l_pass,l_number,l_pass2;
    EditText edit_email,edit_pass,edit_number,edit_pass2;
    Button loginbtn;
    String user_id;
    TextView txt_forgot,txt_acc,txt_welcome,txt_reg;
    int loginMode=0; // 0 - login , 1-register,2-forgot
    String email,ph,pass;
    private FirebaseAuth mauth;
    HashMap <String, Object> map = new HashMap<>();
    private  FirebaseDatabase mdatabase= FirebaseDatabase.getInstance();
    private DatabaseReference mDataref = mdatabase.getReference("users");
    private ChildEventListener mDatabaseListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initID();
        initDrawables();
        FirebaseApp.initializeApp(this);
        mauth=FirebaseAuth.getInstance();

        l_pass2.setVisibility(View.GONE);
        l_number.setVisibility(View.GONE);

        // listners
        txt_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginMode==0){
                    registerPage();
                }else {
                    loginPage();
                }

            }
        });
        txt_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginMode==0){
                  forgotPage();
                } else {
                    loginPage();
                }

            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginMode==0){
                  email=edit_email.getText().toString();
                  pass=edit_pass.getText().toString();
                loginAccount(email,pass);
                }else {
                    email=edit_email.getText().toString();
                    pass=edit_pass.getText().toString();
                    ph=edit_number.getText().toString();
                   registerAccount(email,pass);


                }
            }
        });

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
                    map.put("email", mauth.getCurrentUser().getEmail());
                    map.put("password", pass);
                    map.put("phone_number", ph);
                    map.put("wallet", 00.00);
                    map.put("uid", generateUserId());
                    map.put("doj", formattedDate);
                    mDataref.child(mauth.getCurrentUser().getUid()).updateChildren(map);
                    map.clear();
                    Launch(getApplicationContext(), mauth.getCurrentUser().getUid().toString());

                    //showMsg(getApplicationContext(),"users not equall");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mauth.getCurrentUser();
        if(currentUser != null){
       Launch(getApplicationContext(),currentUser.getUid());
        } else {

        }
    }
    private void initDrawables()
    {
        //roundBG(bg,clr20,10,0);
        roundBG(l_email,clr18,20,5);
        roundBG(l_pass,clr18,20,5);
        roundBG(l_pass2,clr18,20,5);
        roundBG(l_number,clr18,20,5);
        roundBG(loginbtn,clrAccent,20,0);
    }

    private void initID()
    {
        txt_acc=findViewById(R.id.txt_acc);
        txt_reg=findViewById(R.id.txt_reg);
        txt_forgot=findViewById(R.id.txt_forgot);
        txt_welcome=findViewById(R.id.txt_welcome);
        bg=findViewById(R.id.bg);
        loginbtn=findViewById(R.id.btnlogin);
        l_email=findViewById(R.id.l_email);
        l_pass=findViewById(R.id.l_pass);
        l_pass2=findViewById(R.id.l_pass2);
        l_number=findViewById(R.id.l_number);
        edit_email=findViewById(R.id.edit_email);
        edit_pass=findViewById(R.id.edit_pass);
        edit_pass2=findViewById(R.id.edit_pass2);
        edit_number=findViewById(R.id.edit_number);
    }

    @Override
    protected void onDestroy() {
      if(loginMode==0)
      {
          super.onDestroy();
      } else if(loginMode==1)
      {
        loginPage();
      } else if(loginMode==2)
      {
         loginPage();
      }
    }
    private void loginPage(){
        loginMode=0;
        txt_acc.setVisibility(View.VISIBLE);
        loginbtn.setText("Login");
        txt_forgot.setText("Forgot password?");
        txt_welcome.setText(R.string.welcome_back);
        l_pass2.setVisibility(View.GONE);
        l_number.setVisibility(View.GONE);
        l_pass.setVisibility(View.VISIBLE);
        txt_reg.setVisibility(View.VISIBLE);
        txt_acc.setVisibility(View.VISIBLE);
    }
    private void registerPage(){
        loginMode=1;
        loginbtn.setText("Register");
        txt_welcome.setText(R.string.register_now);
        l_pass2.setVisibility(View.VISIBLE);
        txt_forgot.setText("Continue to login");
        l_number.setVisibility(View.VISIBLE);
        txt_acc.setVisibility(View.GONE);
        txt_reg.setVisibility(View.GONE);
    }
    private void forgotPage(){
        loginMode=2;
        txt_acc.setVisibility(View.GONE);
        loginbtn.setText("Reset");
        txt_forgot.setText("Continue to login");
        txt_welcome.setText(R.string.forgot_password);
        l_pass2.setVisibility(View.GONE);
        l_number.setVisibility(View.GONE);
        l_pass.setVisibility(View.GONE);
        txt_acc.setVisibility(View.GONE);
        txt_reg.setVisibility(View.GONE);
    }
    private  void Launch(Context context,String s){
        Intent intent = new Intent(getApplicationContext(), UserActivity.class);
        intent.putExtra("userEmail",s);
        startActivity(intent);

    }
    private void registerAccount (String email , String password )
    {
        mauth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mauth.getCurrentUser();
                             mDataref.addChildEventListener(mDatabaseListener);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
private void loginAccount(String email , String password)
{
    mauth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mauth.getCurrentUser();
                      Launch(getApplicationContext(),user.getUid());
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                       showMsg(getApplicationContext(),"Error:"+task.getException());
                    }
                }
            });
}


}

