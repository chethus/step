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
     * Returns a Comment from a request.
     * Fills in appropriate defaults if attributes are null.
     */
    public static Comment makeComment(HttpServletRequest request) {
        String author = getParamOrDefault(request, "author", "Anonymous");
        String text = getParamOrDefault(request, "text", "");
        return new Comment(author, text);
    }

    /**
     * Creates a Comment from a Datastore entity.
    */
    public static Comment makeComment(Entity entity) {
        String author = (String) entity.getProperty("author");
        String text = (String) entity.getProperty("text");
        return new Comment(author, text);
    }
    /*
     * Gets a the parameter's value from the request or a default value if the request 
     * does not contain the parameter.
     */
    private static String getParamOrDefault(HttpServletRequest request, String paramName, String revert) {
        final String paramValue = request.getParameter(paramName);
        if (paramValue == null) {
            return revert;
        }
        return paramValue;
    }
}