package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

/**
 * A class for storing a Comment.
 */
public class Comment {
    private String author;
    private String text;
    private long timestamp;

    public Comment(String author, String text) {
        this.timestamp = System.currentTimeMillis();
        this.author = author;
        this.text = text;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long time) {
        this.timestamp = time;
    }

    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
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
        String author = (String) entity.getProperty("author");
        String text = (String) entity.getProperty("text");
        return new Comment(author, text);
    }
}