package com.example.test.simulation;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.animation.ObjectAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.test.ScaleActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import com.example.test.Sessions.GameSessionManager;
public class ScaleSimulation {
    private static final double MAX_VALUE = 5000.00;
    private static final double SLOW_THRESHOLD = 5.00;
    private static final double FAST_THRESHOLD = 20.00;
    private static final int SLOW_INTERVAL = 500; // in milliseconds
    private static final int FAST_INTERVAL = 300; // in milliseconds
    private static final int TRIPLE_SPEED_INTERVAL = 200; // in milliseconds
    private double currentMultiplier; // Stores the current multiplier

    private TextView textView;
    private TextView textView2;
    private double userToken; // User's token
    private Handler handler;
    private Random random;
    private double currentValue;
    private boolean gameOver;
    private boolean crashed;
    private double crashedMultiplier;
    private boolean cashedOut; // Flag to indicate whether the user cashed out
    private String roundId; // Store the round ID
    private long startTime;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean roundInProgress;
    private Button bet_btn;
    Context context;
    public String TAG = "ScaleOfFate";
    public ScaleSimulation(Context context, TextView textView, TextView textView2, Button bet_btn) {
        this.textView = textView;
        this.handler = new Handler();
        this.random = new Random();
        this.currentValue = 1.00;
        this.textView2 = textView2;
        this.gameOver = false;
        this.currentMultiplier = 1.00;
        this.cashedOut = false;
        this.roundInProgress = false;
        this.context = context;
        this.bet_btn = bet_btn;
        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void startGame() {
        if (GameSessionManager.isSessionActive()) {
            ScaleActivity.isBcut = false;
            showRoundInProgressDialog();
            return;
        } else {
            GameSessionManager.startSession();
            ScaleActivity.isBcut=true;
            currentValue = 1.00; // Reset current value
            gameOver = false; // Reset game over flag
            crashed = true;
            roundInProgress = true; // Set round in progress when starting a new game
            ScaleActivity.cashout_enable = true;
            textView.setText("1.00x"); // Reset TextView
            textView2.setTextColor(Color.BLACK);
            textView.setTextColor(Color.BLACK);
            // Generate a unique round ID
            bet_btn.setText("Cashout");
            roundId = mDatabase.child("rounds").push().getKey();
            if (roundId == null) {
                Log.e("Firebase", "Failed to generate roundId");
                // Handle the error gracefully, such as displaying a message to the user
            } else {
                // Continue with setting the value using the generated roundId
            }
            startTime = System.currentTimeMillis();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!gameOver) {
                        updateValue();
                        updateTextView();
                        handler.postDelayed(this, getNextInterval());
                    } else {
                        // Record the end time of the round
                        long endTime = System.currentTimeMillis();
                        GameSessionManager.endSession();
                        // Record the game data in Firebase
                        recordGameData(roundId, startTime, endTime, cashedOut);
                        roundInProgress = false; // Set round not in progress when cashing out

                    }
                }
            }, SLOW_INTERVAL);

        }

    }
    public void cashOut() {
        cashedOut = true;
        ScaleActivity.cashout_enable = false;
        bet_btn.setText("Bet");
        endGame();
    }
    public boolean isRoundInProgress() {
        return roundInProgress; // Return the current state of roundInProgress
    }
public boolean isCrashed(){
        return crashed;
}
    private void endGame() {
        // Record the end time of the round
        long endTime = System.currentTimeMillis();

        // Record the game data in Firebase
        recordGameData(roundId, startTime, endTime, cashedOut);
    }
    private void recordGameData(String roundId, long startTime, long endTime ,boolean cashout) {
        // Get the user's ID
        String userId = mAuth.getCurrentUser().getUid();

        // Example of recording game data in Firebase
        double winAmount = 00.00;
        if (cashout){
            winAmount =  getCashOutAmount(userToken); // Implement this method to calculate the win amount

        }

        GameData gameData = new GameData(roundId, startTime, endTime, userToken, crashedMultiplier, gameOver, winAmount);
        mDatabase.child("users").child(userId).child("game_history").child(roundId).setValue(gameData);

    }

    private void updateValue() {
        double increment = generateIncrement();
        currentValue += increment;

        currentMultiplier = currentValue; // Update currentMultiplier

        double multiplier = currentValue - Math.floor(currentValue);
        double probability;

        if (multiplier >= 1 && multiplier < 1.98) {
            probability = 0.2; // 20% chance of crashing
        } else if (multiplier >= 2 && multiplier < 8) {
            probability = 0.4; // 40% chance of crashing
        } else if (multiplier >= 8 && multiplier < 14) {
            probability = 0.18; // 18% chance of crashing
        } else if (multiplier >= 14 && multiplier < 20) {
            probability = 0.12; // 12% chance of crashing
        } else if (multiplier >= 20 && multiplier < 50) {
            probability = 0.05; // 5% chance of crashing
        } else {
            probability = 0.05; // 5% chance of crashing
        }

        if (random.nextDouble() < probability) {
            crashedMultiplier = currentValue;
            currentValue = MAX_VALUE; // Game over
            gameOver = true;
            GameSessionManager.endSession();

        } else if (currentValue >= MAX_VALUE) {
            crashedMultiplier = MAX_VALUE;
            currentValue = MAX_VALUE; // Ensure currentValue is capped at MAX_VALUE
            gameOver = true;

        }
    }

    public void setUserToken(double token) {
        this.userToken = token;
    }

    private void updateTextView() {
        String displayText;
        String displayProfit;
        if (gameOver) {
            displayText = "Crashed at " + String.format("%.2fx", crashedMultiplier);
            textView.setTextColor(Color.RED);
            displayProfit = "00.00";
        } else {
            double displayMultiplier = currentValue - Math.floor(currentValue);
            double displayedToken = userToken * currentValue;
            displayText = String.format("%.2fx", currentValue);
            if (cashedOut) {
                displayProfit = "Cashed out!";
                textView2.setTextColor(Color.parseColor("#00FF00"));
            } else {
                displayProfit = "Profit:" + String.format("%.2f", displayedToken);
            }

        }
        textView.setText(displayText);
        textView2.setText(displayProfit);
        // Check for bounce animation
        if (Math.floor(currentValue) == currentValue) {
            bounceAnimation(textView);
        }
    }

    private void bounceAnimation(TextView textView) {
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(textView, "translationY", 0f, -30f, 0f);
        animatorY.setDuration(1000);
        animatorY.start();
    }

    private boolean shouldCrash() {
        double multiplier = currentValue - Math.floor(currentValue);
        double probability;

        if (multiplier >= 1 && multiplier < 1.98) {
            probability = 0.3; // 30% chance of crashing
        } else if (multiplier >= 2 && multiplier < 8) {
            probability = 0.4; // 40% chance of crashing
        } else if (multiplier >= 8 && multiplier < 14) {
            probability = 0.08; // 8% chance of crashing
        } else if (multiplier >= 14 && multiplier < 20) {
            probability = 0.12; // 12% chance of crashing
        } else if (multiplier >= 20 && multiplier < 50) {
            probability = 0.05; // 5% chance of crashing
        } else {
            probability = 0.05; // 5% chance of crashing
        }

        return random.nextDouble() < probability;
    }

    private double generateIncrement() {
        double increment;
        if (currentValue < SLOW_THRESHOLD) {
            increment = random.nextDouble() * 0.1 + 0.05; // Slowly increasing
        } else if (currentValue < FAST_THRESHOLD) {
            increment = random.nextDouble() * 0.2 + 0.1; // Speeds up
        } else {
            increment = random.nextDouble() * 3.0 + 1.0; // Triples and continues
        }
        return increment;
    }

    private int getNextInterval() {
        if (currentValue < SLOW_THRESHOLD) {
            return SLOW_INTERVAL;
        } else if (currentValue < FAST_THRESHOLD) {
            return FAST_INTERVAL;
        } else {
            return TRIPLE_SPEED_INTERVAL;
        }
    }

    public double getCurrentMultiplier() {
        return currentMultiplier;
    }

    public double getCashOutAmount(double betAmount) {
        return getCurrentMultiplier() * betAmount;
    }

    public boolean isGameOver() {
        return gameOver;
    }
    // Define a data class to represent game data
// Define a data class to represent game data
    private static class GameData {
        private String roundId;
        private long startTime;
        private long endTime;
        private double userToken;
        private double roundResult;
        private boolean gameOver;
        private double winAmount;
        public GameData() {
            // Default constructor required for Firebase
        }

        public GameData(String roundId, long startTime, long endTime, double userToken, double roundResult, boolean gameOver, Double winAmount) {
            this.roundId = roundId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.userToken = userToken;
            this.roundResult = roundResult;
            this.gameOver = gameOver;
            this.winAmount = winAmount;
        }

        public String getRoundId() {
            return roundId;
        }

        public void setRoundId(String roundId) {
            this.roundId = roundId;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public double getUserToken() {
            return userToken;
        }

        public void setUserToken(double userToken) {
            this.userToken = userToken;
        }

        public double getRoundResult() {
            return roundResult;
        }
        public double getWinAmount() {
            return winAmount;
        }

        public void setWinAmount(double winAmount) {
            this.winAmount = winAmount;
        }
        public void setRoundResult(Double roundResult) {
            this.roundResult = roundResult;
        }

        public boolean isGameOver() {
            return gameOver;
        }

        public void setGameOver(boolean gameOver) {
            this.gameOver = gameOver;
        }
    }
    private void showRoundInProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Round in Progress");
        builder.setMessage("A round is already in progress. You cannot place a bet at this time.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing or handle the dialog dismissal
            }
        });
        builder.show();
    }
}
