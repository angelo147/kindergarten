package edu.kindergarten.registration.chat_.model;



public class DuplicateUserNotification {
    private String username;

    public DuplicateUserNotification(String username) {
        this.username = username;
    }
    
    public String getDuplicateUserNotificationMsg(){
        return "Username " + username + " has been taken. Retry with a different name";
    }
}
