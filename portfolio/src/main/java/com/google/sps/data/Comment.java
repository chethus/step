package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

/**
 * A class for storing a Comment.
 */
public class Comment {

    private String nickname;
    private String text;
    private long timestamp;

    public Comment(long timestamp, String nickname, String text) {
        this.timestamp = timestamp;
        this.nickname = nickname;
        this.text = text;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Creates a Comment from a Datastore entity.
    */
    public static Comment makeComment(Entity entity) {
        long timestamp = (long) entity.getProperty("timestamp");
        String nickname = (String) entity.getProperty("nickname");
        String text = (String) entity.getProperty("text");
        return new Comment(timestamp, nickname, text);
    }
}