package edu.kindergarten.registration.rest;

public enum ResponseCode {
    OK(0, "OK", "OK"),
    ERRSENDMSG(100, "Error Sending Email or SMS", "OK"),
    INVTOKEN(600, "Invalid Token", "OK"),
    LOGINFAILED(900, "Login failed", "OK");

    private int responseCode;

    private String responseMessage;

    private String userMessage;

    ResponseCode(int responseCode, String responseMessage, String userMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.userMessage = userMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

}
