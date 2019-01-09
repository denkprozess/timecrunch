package de.timecrunch.timecrunch.model;

public class Task {

    private final int id;
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


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Task)) {
            return false;
        }
        if (((Task) other).getId() == (this.getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id;
    }
}
