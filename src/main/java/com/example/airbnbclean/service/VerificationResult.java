package com.example.airbnbclean.service;


public class VerificationResult {
    private final String status;
    private final double score;

    public VerificationResult(String status, double score) {
        this.status = status;
        this.score = score;
    }

    public String getStatus() { return status; }
    public double getScore() { return score; }
}
