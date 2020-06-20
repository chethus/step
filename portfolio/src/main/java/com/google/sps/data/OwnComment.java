package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;

/**
 * A comment made by the user who is currently logged in.
 */
public class OwnComment extends Comment {

    private long commentId;
    
    public OwnComment(long timestamp, String nickname, String text, String blobKey, float happyScore, long commentId) {
        super(timestamp, nickname, text, blobKey, happyScore);
        this.commentId = commentId;
    }

    /**
     * Create OwnComment with comment ID given a comment.
     */
    public OwnComment(Comment c, long commentId) throws IOException{
        super(c.getTimestamp(), c.getNickname(), c.getText(), c.getBlobKey(), c.getHappyScore());
        setCommentId(commentId);
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}