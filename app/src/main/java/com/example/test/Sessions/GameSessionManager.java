package com.example.test.Sessions;

public class GameSessionManager {
    public static boolean sessionActive = false;
    public  static  GameSessionManager instance;
    public static void startSession() {
        sessionActive = true;
    }

    public static void endSession() {
        sessionActive = false;
    }

    public static boolean isSessionActive() {
        return sessionActive;
    }
}
