package com.blogspot.waptell.tafakari.models;

public class Subject {

    public String title;
    public String event;
    public String description;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Subject(){}

    public Subject(String title, String event) {
        this.title = title;
        this.event = event;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
