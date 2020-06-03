package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;

/**
 * A class for storing a Comment.
 */
public class Comment {
    private String author;
    private String subject;
    private String text;

    /**
     * No argument constructor.
     * Set fields with setter methods.
     */
    public Comment() {
    }

    public Comment(String author, String subject, String text) {
        this.author = author;
        this.subject = subject;
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
        if (c.getAuthor() == null) {
            c.setAuthor("Anonymous");
        }
        c.setSubject(request.getParameter("subject"));
        if (c.getSubject() == null) {
            c.setSubject("No subject");
        }
        c.setText(request.getParameter("text"));
        if (c.getText() == null) {
            c.setText("");
        }
        return c;
    }
}