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

    /**
     * No argument constructor.
     * Set fields with setter methods.
     */
    public Comment() {
        this.timestamp = System.currentTimeMillis();
    }

    public Comment(String author, String text) {
        this();
        this.author = author;
        this.text = text;
    }


    public long getTimestamp() {
        return System.currentTimeMillis();
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
     * Returns a Comment from a request.
     * Fills in appropriate defaults if attributes are null.
     */
    public static Comment makeComment(HttpServletRequest request) {
        Comment c = new Comment();
        c.setAuthor(request.getParameter("author"));
        if (c.getAuthor() == null || c.getAuthor().length() == 0) {
            c.setAuthor("Anonymous");
        }
        c.setText(request.getParameter("text"));
        if (c.getText() == null) {
            c.setText("");
        }
        return c;
    }

    /**
     * Creates a Comment from a Datastore entity.
    */
    public static Comment makeComment(Entity entity) {
        Comment c = new Comment();
        c.setAuthor((String) entity.getProperty("author"));
        c.setText((String) entity.getProperty("text"));
        return c;
    }
}