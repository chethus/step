package com.google.sps.data;

public class User {
    private String email;
    private String nickname = null;
    
    public User(String email) {
        this.email = email;
    }

    public User(String email, String nickname) {
        this(email);
        this.nickname = nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getNickname() {
        return nickname;
    }
}