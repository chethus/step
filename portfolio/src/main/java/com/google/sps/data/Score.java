package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

/**
 * A class for storing a Score.
 */
public class Score {
    private String name;
    private int score;
    private int rank;

    public Score(String name, int score, int rank) {
        this.name = name;
        this.score = score;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        String name = (String) entity.getProperty("name");
        int score = Integer.parseInt(entity.getProperty("score").toString());
        Score s = new Score(name, score, rank);
        return s;
    }
}