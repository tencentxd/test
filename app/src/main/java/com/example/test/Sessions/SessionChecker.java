package com.example.test.Sessions;

import android.os.Handler;
import android.util.Log;

public class SessionChecker {

    private static final long CHECK_INTERVAL = 1000; // 1 second
    private static final String TAG = "SessionChecker";

    private Handler handler;
    private Runnable sessionCheckRunnable;

    public SessionChecker() {
        handler = new Handler();
        sessionCheckRunnable = new Runnable() {
            @Override
            public void run() {
                // Check session status here
                boolean isSessionActive = checkSessionStatus();
                Log.d(TAG, "Session is active: " + isSessionActive);

                // Post the same Runnable again after the interval
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
    }

    // Method to start the session checking
    public void startSessionCheck() {
        // Post the Runnable immediately
        handler.post(sessionCheckRunnable);
    }

    // Method to stop the session checking
    public void stopSessionCheck() {
        // Remove the posted Runnable callbacks
        handler.removeCallbacks(sessionCheckRunnable);
    }

    // Method to simulate checking session status (replace with your actual logic)
    private boolean checkSessionStatus() {
        // Simulated logic to determine if session is active
        return System.currentTimeMillis() % 2 == 0; // Example: Session is active on even seconds
    }
}
