package com.google.sps.data;

import javax.servlet.http.HttpServletRequest;
import com.google.appengine.api.datastore.Entity;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.io.IOException;

/**
 * A class for storing a Comment.
 */
public class Comment {
    private String nickname;
    private String text;
    private long timestamp;
    private String imageSrc;
    private float happyScore;

    public Comment(long timestamp, String nickname, String text, String imageSrc) throws IOException{
        this.timestamp = timestamp;
        this.nickname = nickname;
        this.text = text;
        this.imageSrc = imageSrc;
        // Calculate sentiment score of text.
        this.happyScore = getSentimentScore(text);
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
    public static Comment makeComment(Entity entity) throws IOException{
        long timestamp = (long) entity.getProperty("timestamp");
        String nickname = (String) entity.getProperty("nickname");
        String text = (String) entity.getProperty("text");
        String imageSrc = (String) entity.getProperty("imageSrc");
        Comment c = new Comment(timestamp, nickname, text, imageSrc);
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

    /**
     * Calculate the sentiment score for a Comment.
     */
    private static float getSentimentScore(String message) throws IOException{
        float score = 0;

        // Set up sentiment services.
        Document doc =
            Document.newBuilder().setContent(message).setType(Document.Type.PLAIN_TEXT).build();
        LanguageServiceClient languageService = LanguageServiceClient.create();

        // Create sentiment object.
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        
        // Get and return sentiment score.
        score = sentiment.getScore();
        languageService.close();
        return score;
    }
}