package de.timecrunch.timecrunch.model;

import com.google.android.gms.maps.model.LatLng;

public class TaskModel {

    private final String id;
    private String text;
    private LatLng location;
    private TaskAlarm alarm;

    public TaskModel(String id, String text, LatLng location) {
        this.id = id;
        this.text = text;
        this.location = location;
    }

    public TaskModel(String id, String text, LatLng location, TaskAlarm alarm) {
        this.id = id;
        this.text = text;
        this.location = location;
        this.alarm = alarm;
    }

    // Legacy to keep compatibility
    public TaskModel(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
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
        if (((TaskModel) other).getId().equals(this.getId())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
