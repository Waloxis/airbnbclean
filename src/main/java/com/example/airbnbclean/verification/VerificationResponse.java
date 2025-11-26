package com.example.airbnbclean.verification;



public class VerificationResponse {
    private boolean received;
    private String status;
    private double score;
    private String message;

    public VerificationResponse() {}

    public VerificationResponse(boolean received, String status, double score, String message) {
        this.received = received;
        this.status = status;
        this.score = score;
        this.message = message;
    }

    public VerificationResponse(boolean b, String s) {
    }

    public boolean isReceived() { return received; }
    public void setReceived(boolean received) { this.received = received; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
