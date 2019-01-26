package de.timecrunch.timecrunch.model;

import com.google.android.gms.maps.model.LatLng;

public class TaskModel {

    private final int id;
    private String text;
    private LatLng location;
    private TaskAlarm alarm;

    public TaskModel(int id, String text, LatLng location) {
        this.id = id;
        this.text = text;
        this.location = location;
    }

    public TaskModel(int id, String text, LatLng location, TaskAlarm alarm) {
        this.id = id;
        this.text = text;
        this.location = location;
        this.alarm = alarm;
    }

    // Legacy to keep compatibility
    public TaskModel(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LatLng getLocation() {
        return location;
    }

    public TaskAlarm getAlarm() { return alarm; }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TaskModel)) {
            return false;
        }
        if (((TaskModel) other).getId() == (this.getId())) {
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
