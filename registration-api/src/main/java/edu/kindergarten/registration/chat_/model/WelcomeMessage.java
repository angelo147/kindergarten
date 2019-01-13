package edu.kindergarten.registration.chat_.model;


public class WelcomeMessage {

    private final String forUser;
    
    public WelcomeMessage(String forUser) {
        this.forUser = forUser;
    }
    
    public String getWelcomeMessage(){
        return "Welcome " + forUser;
    }
    
}
