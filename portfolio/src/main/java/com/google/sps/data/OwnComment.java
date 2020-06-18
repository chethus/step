package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

/**
 * A comment made by the user who is currently logged in.
 */
public class OwnComment extends Comment {

    private long commentId;

    public OwnComment(long timestamp, String nickname, String text, String imageSrc, long commentId) {
        super(timestamp, nickname, text, imageSrc);
        this.commentId = commentId;
    }

    public OwnComment(Comment c, long commentId) {
        super(c.getTimestamp(), c.getNickname(), c.getText(), c.getImageSrc());
        setCommentId(commentId);
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}