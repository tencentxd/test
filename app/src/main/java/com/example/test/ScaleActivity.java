package com.example.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import  static com.example.test.Utils.*;

import com.example.test.Details.Round;
import com.example.test.Sessions.GameSessionManager;
import com.example.test.Sessions.SessionChecker;
import com.example.test.adapter.HorizontalAdapter;
import com.example.test.adapter.ScaleAdapter;
import com.example.test.simulation.ScaleSimulation;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.graphics.Rect;

import static com.example.test.UserData.*;
public class ScaleActivity extends AppCompatActivity {
 LinearLayout game_vew , bg1,bg2;
 Button btn_bet;
 TextView txt_blnce,txt_multi,txt_profit;
 ImageView img_add,img_minus;
 EditText edit_amnt;
 Double bet_amnt;
 Double avail_amnt;
 Double base_amnt=10.00;
 Double final_bet_amnt;
 Double min_bet = 10.00;
 Double max_bet = 5000.00;
 Double win_amnt;
 public static  boolean cashout_enable = false;
 private ScaleSimulation scaleSimulation;
 public  static  boolean nn=false;
 SessionChecker sessionChecker;
 public static boolean isBcut =  false;
 boolean isSessionActive;
    DecimalFormat currencyFormat = new DecimalFormat("0.00");
    private FirebaseAuth mauth;
    LinearLayout bg_history;
    HashMap<String, Object> map = new HashMap<>();
    private FirebaseDatabase mdatabase= FirebaseDatabase.getInstance();
    private DatabaseReference mDataref = mdatabase.getReference("users");
    private ChildEventListener mDatabaseListener;
    private DatabaseReference balanceDataref =  mdatabase.getReference("users");
    private ChildEventListener mDatabaseListener2;
    Timer timer;
  RecyclerView recyclerView;
    private List<String> roundIds = new ArrayList<>();
    private List<String> roundIds2 = new ArrayList<>();
    private HorizontalAdapter adapter;
    private  boolean isValueCheck;
    DatabaseReference mref = FirebaseDatabase.getInstance().getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);
         initID();
        FirebaseApp.initializeApp(this);
        mauth = FirebaseAuth.getInstance();
        initDatabase();
        balanceDatabase();
        timer = new Timer();
        startTimerTask();
        populateRoundIds();


         adapter = new HorizontalAdapter(roundIds);
        recyclerView.setAdapter(adapter);
        initDrawables();
        mDataref.addChildEventListener(mDatabaseListener);
         edit_amnt.setText(min_bet.toString());
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bet_amnt = Double.parseDouble(edit_amnt.getText().toString());
                final_bet_amnt = bet_amnt + base_amnt;
             isValueCheck =true;
                if (final_bet_amnt > max_bet) {
                    final_bet_amnt = max_bet;
                    edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                    showMsg(getApplicationContext(), "Max Bet Amount Reached!");
                } else if (final_bet_amnt < min_bet) {
                    final_bet_amnt = min_bet + base_amnt;
                    edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                    showMsg(getApplicationContext(), "Final bet less than minimum bet!");
                } else {
                    edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                }
            }
        });
         img_minus.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 bet_amnt = Double.parseDouble(edit_amnt.getText().toString());
                 final_bet_amnt =   bet_amnt - base_amnt;
                 isValueCheck =true;
                 if (final_bet_amnt > max_bet){
                     final_bet_amnt = max_bet;
                     edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                 } else if (final_bet_amnt < min_bet){
                     final_bet_amnt = min_bet ;
                     edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                 }  else {

                     edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                 }
             }
         });
        btn_bet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (!isValueCheck){
                 isValueCheck = true;
                 bet_amnt = Double.parseDouble(edit_amnt.getText().toString());
                 final_bet_amnt = bet_amnt;
                 if (final_bet_amnt > max_bet){
                     final_bet_amnt = max_bet;
                     edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                 } else if (final_bet_amnt < min_bet){
                     final_bet_amnt = min_bet ;
                     edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                 }  else {

                     edit_amnt.setText(currencyFormat.format(final_bet_amnt));
                 }
             } else {



                     if (!cashout_enable) {
                         Double temp_amnt = avail_amnt - final_bet_amnt;
                         if (temp_amnt >= 0) {
                             avail_amnt = temp_amnt;
                             scaleSimulation = new ScaleSimulation(ScaleActivity.this,txt_multi, txt_profit,btn_bet);
                             scaleSimulation.setUserToken(final_bet_amnt);
                             scaleSimulation.startGame();
                             if (isBcut){
                                 balanceDataref.addChildEventListener(mDatabaseListener2);
                             }

                         } else {
                             showMsg(getApplicationContext(), "Not enough balance");
                         }
                     } else if (!scaleSimulation.isGameOver()) {
                         scaleSimulation.cashOut();
                         win_amnt = scaleSimulation.getCashOutAmount(final_bet_amnt);
                         Double an = avail_amnt + win_amnt;
                         avail_amnt = an;
                         String s = avail_amnt.toString();
                         updateUserBalance(avail_amnt);
                     }
                 }
             }

        });


    }
    private void initID()
    {
        game_vew = findViewById(R.id.game_view);
        bg1 = findViewById(R.id.bg1);
        bg2 = findViewById(R.id.bg2);
        btn_bet = findViewById(R.id.btn_bet);
        txt_blnce = findViewById(R.id.txt_wallet);
        txt_multi = findViewById(R.id.txt_multiper);
        img_add = findViewById(R.id.btn_add);
        img_minus = findViewById(R.id.btn_minus);
        edit_amnt = findViewById(R.id.edit_amnt);
        txt_profit = findViewById(R.id.txt_profit);
       // Assuming you have a ListView with id "listView_rounds"
        bg_history = findViewById(R.id.bg_history);
        recyclerView = findViewById(R.id.recycler_view);
    }
    private void initDrawables(){


        // Load the background image from the drawable folder
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sky);

        // Set corner radius
        float cornerRadius = 20; // Change this value as needed

        // Create a round rectangle shape with the desired corner radius
        float[] radii = {cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
        RoundRectShape roundRectShape = new RoundRectShape(radii, null, null);

        // Create a shape drawable with the round rectangle shape
        ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);

        // Create a bitmap shader from the background image
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        // Set the bitmap shader as the paint shader
        shapeDrawable.getPaint().setShader(shader);

        // Set the shape drawable as the background of the LinearLayout
        game_vew.setBackground(shapeDrawable);


    }

    private void startTimerTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isSessionActive = GameSessionManager.isSessionActive();
                        if (!isSessionActive){
                            btn_bet.setText("Bet");
                            cashout_enable =false;
                        }
                    }
                });
                // Check session status every second


            }
        };

        // Schedule the timer task to run every second
        timer.schedule(timerTask, 0, 1000);
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

                    Double blnc = Double.parseDouble(childValue.get("wallet").toString());

                    txt_blnce.setText(currencyFormat.format(blnc));
                    avail_amnt = blnc;


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
                    Double blnc = Double.parseDouble(childValue.get("wallet").toString());

                    txt_blnce.setText(currencyFormat.format(blnc));
                    avail_amnt = blnc;
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
    private  void balanceDatabase(){
        mDatabaseListener2 = new ChildEventListener() {
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

          if (childkey.equals(mauth.getCurrentUser().getUid())) {
              map = new HashMap<>();

              map.put("wallet",  currencyFormat.format(avail_amnt));

              mDataref.child(mauth.getCurrentUser().getUid()).updateChildren(map);
              map.clear();

          } else {
              Log.d("USER","user not found");
          }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final String childkey = snapshot.getKey();
                GenericTypeIndicator<HashMap<String, Object>> ind = new GenericTypeIndicator<HashMap<String, Object>>() {
                };
                final HashMap<String, Object> childValue = snapshot.getValue(ind);

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
    public  boolean isSessionEnabled(){
        return GameSessionManager.isSessionActive();
    }
    private void populateRoundIds() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(mauth.getCurrentUser().getUid())
                .child("game_history");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roundIds.clear(); // Clear the list before populating it again
                roundIds2.clear(); // Clear the separate list for round IDs
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double roundResult = snapshot.child("roundResult").getValue(Double.class);
                    if (roundResult != null) {// Add the round ID to the separate list
                        roundIds.add(0,Utils.setDoubleToString(roundResult)); // Add only the round result to the main list
                    }
                }
                // Notify adapter that data set has changed

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    // Method to show dialog with round details
    private void showRoundDetailsDialog(String roundId) {
        // Retrieve round details from Firebase using roundId
        DatabaseReference roundRef = mref.child("users").child(mauth.getCurrentUser().getUid()).child("game_history").child(roundId);
        roundRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Extract round details
                    String roundId = dataSnapshot.child("roundId").getValue(String.class);
                    long startTime = dataSnapshot.child("startTime").getValue(Long.class);
                    long endTime = dataSnapshot.child("endTime").getValue(Long.class);
                    double userToken = dataSnapshot.child("userToken").getValue(Double.class);
                    Double roundResult = dataSnapshot.child("roundResult").getValue(Double.class);
                    boolean gameOver = dataSnapshot.child("gameOver").getValue(Boolean.class);
                    double winAmount = dataSnapshot.child("winAmount").getValue(Double.class);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String startTime1 = sdf.format(new Date(startTime));
                    String endTime1 = sdf.format(new Date(endTime));
                    // Create and show dialog with round details
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScaleActivity.this);
                    builder.setTitle("Round Details");
                    builder.setMessage("Round ID: " + roundId + "\n" +
                            "Start Time: " + startTime1 + "\n" +
                            "End Time: " + endTime1 + "\n" +
                            "Bet Amount: " + userToken + "\n" +
                            "Round Result: " +Utils.setDoubleToString( roundResult) + "\n" +
                            "Win Amount: " + Utils.setDoubleToString(winAmount));
                    builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                    builder.show();
                } else {
                    // Handle case where round data doesn't exist
                 //   Toast.makeText(YourActivity.this, "Round data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("Firebase", "Error retrieving round details: " + databaseError.getMessage());
            }
        });
    }

}