package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

public class OwnComment extends Comment {

    private long commentId;

    /**
     * A comment made by the user who is currently logged in.
     */
    public OwnComment() {
        super();
    }

    public OwnComment(String nickname, String text, String imageSrc, long commentId) {
        super(nickname, text, imageSrc);
        this.commentId = commentId;
    }

    public OwnComment(Comment c, long commentId) {
        super(c.getNickname(), c.getText(), c.getImageSrc());
        setCommentId(commentId);
    }

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
}