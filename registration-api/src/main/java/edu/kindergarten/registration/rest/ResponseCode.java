package edu.kindergarten.registration.rest;

public enum ResponseCode {
    OK(0, "OK", "OK"),
    ERRSENDMSG(100, "Error Sending Email or SMS", "OK"),
    ERRCREATELIFERAYUSER(200, "Error Creating Liferay User", "OK"),
    LIFERAYUSEREXIST(210, "Liferay User exist.", "OK"),
    PROCESSGUID(220, "Add LiferayUser is processing current guid.", "OK"),
    TECHERROR(300, "Technical error", "OK"),
    NOMATCH(400, "User Not Found", "OK"),
    IVRERROR(500, "IVR Callback Error", "OK"),
    INVTOKEN(600, "Invalid Token", "OK"),
    NOSUCHADDRESS(700, "No such address", "OK"),
    NOUNIQUECUSTOMER(800, "Cannot find unique customer", "OK"),
    NOCUSTOMER(810, "Cannot find customer", "OK"),
    IDMLOGINFAILED(900, "IDM login failed", "OK"),
    AUTHFAILED(110, "Authorization failed", "OK"),
    IDMREGFAILED(120, "IDM registration failed", "OK"),
    INVALIDDATA(130, "Invalid data", "OK"),
    PARTIALGDPRUPDATE(140, "Some UCMIds were not updated", "OK"),
    GDPRUPDATEFAILED(150, "UCMIds were not updated", "OK"),
    GDPRUPDATEDENIED(160, "UCMIds are not authorized for the provided guid", "OK"),
    IMAGESIZE(170, "Image max size allowed exceeded", "OK"),
	ERRSDPTOKEN(180, "SdpToken validation error.", "OK");

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
