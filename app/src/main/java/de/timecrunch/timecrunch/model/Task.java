package de.timecrunch.timecrunch.model;

public class Task {


    private int id;
    private String text;

    public Task(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
