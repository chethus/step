package com.google.sps.data;

/**
 * Stores login status of the user and login/logout url.
 */

public class Login {

    private boolean loggedIn;
    private String url;

    public Login(boolean loggedIn, String url) {
        this.loggedIn = loggedIn;
        this.url = url;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    public boolean getLoggedIn() {
        return loggedIn;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}