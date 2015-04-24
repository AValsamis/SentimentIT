package gr.ntua.ece.sevle.sentimentit.sentimentit.entities;

/**
 * Created by Sevle on 3/27/2015.
 */
public class Data {
    int dataID;
    String tweet;
    String keyword;
    Double initialCalculation;

    public Data(int dataID, String tweet, String keyword, Double initialCalculation) {
        this.dataID = dataID;
        this.tweet = tweet;
        this.keyword = keyword;
        this.initialCalculation = initialCalculation;
    }

    public int getDataID() {
        return dataID;
    }

    public String getTweet() {
        return tweet;
    }

    public String getKeyword() {
        return keyword;
    }

    public Double getInitialCalculation() {
        return initialCalculation;
    }

    public void setDataID(int dataID) {
        this.dataID = dataID;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setInitialCalculation(Double initialCalculation) {
        this.initialCalculation = initialCalculation;
    }
}
