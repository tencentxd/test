package com.example.test.Details;

public class Round {
    private String roundId;
    private String roundResult;

    public Round(String roundId, String roundResult) {
        this.roundId = roundId;
        this.roundResult = roundResult;
    }

    public String getRoundId() {
        return roundId;
    }

    public String getRoundResult() {
        return roundResult;
    }
}
