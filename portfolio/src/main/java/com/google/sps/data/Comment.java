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
    private String imageSrc;

    /**
     * No argument constructor.
     * Set fields with setter methods.
     */
    public Comment() {
    }

    public Comment(String nickname, String text, String imageSrc) {
        this.timestamp = System.currentTimeMillis();
        this.nickname = nickname;
        this.text = text;
        this.imageSrc = imageSrc;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long time) {
        this.timestamp = time;
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

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    /**
     * Creates a Comment from a Datastore entity.
    */
    public static Comment makeComment(Entity entity) {
        Comment c = new Comment();
        c.setTimestamp((long) entity.getProperty("timestamp"));
        c.setNickname((String) entity.getProperty("nickname"));
        c.setText((String) entity.getProperty("text"));
        c.setImageSrc((String) entity.getProperty("imageSrc"));
        return c;
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