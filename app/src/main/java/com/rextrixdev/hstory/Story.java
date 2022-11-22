package com.rextrixdev.hstory;

public class Story {

    public String title, story, userid,date,language;
    public int likes, views;

    public Story(){

    }

    public Story(String title, String story, String userid,String date, int likes, int views,String language) {
        this.title = title;
        this.story = story;
        this.userid = userid;
        this.date = date;
        this.likes = likes;
        this.views = views;
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public String getStory() {
        return story;
    }

    public String getUserid() {
        return userid;
    }


}
