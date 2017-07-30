package com.josephcostlow.jotme;

/**
 * Created by Joseph Costlow on 29-Jul-17.
 */

public class Jot {

//    TODO possibly add unique push id to constructor

    private String title, tagOne, tagTwo, tagThree, message;

    public Jot() {}

    public Jot(String title, String tagOne, String tagTwo, String tagThree) {
        this.title = title;
        this.tagOne = tagOne;
        this.tagTwo = tagTwo;
        this.tagThree = tagThree;
    }

    public Jot(String title, String tagOne, String tagTwo, String tagThree, String message) {
        this.title = title;
        this.tagOne = tagOne;
        this.tagTwo = tagTwo;
        this.tagThree = tagThree;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTagOne() {
        return tagOne;
    }

    public void setTagOne(String tagOne) {
        this.tagOne = tagOne;
    }

    public String getTagTwo() {
        return tagTwo;
    }

    public void setTagTwo(String tagTwo) {
        this.tagTwo = tagTwo;
    }

    public String getTagThree() {
        return tagThree;
    }

    public void setTagThree(String tagThree) {
        this.tagThree = tagThree;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
