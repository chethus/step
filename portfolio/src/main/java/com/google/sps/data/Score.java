package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

/**
 * A class for storing a Score.
 */
public class Score {
    private String nickname;
    private int score;
    private int rank;

    /**
     * No argument constructor.
     * Set fields with setter methods.
     */
    public Score() {
    }

    public Score(String nickname, int score, int rank) {
        this.nickname = nickname;
        this.score = score;
        this.rank = rank;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    /**
     * Creates a Score from a Datastore entity and a given rank.
    */
    public static Score makeScore(Entity entity, int rank) {
        Score s = new Score();
        s.setNickname((String) entity.getProperty("nickname"));
        s.setScore(Integer.parseInt(entity.getProperty("score").toString()));
        s.setRank(rank);
        return s;
    }
}