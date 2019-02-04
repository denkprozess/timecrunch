package de.timecrunch.timecrunch.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

public class TaskModel {

    private String categoryId;
    private String id;
    private String text;
    // This conversion is necessary because LatLng cannot be serialized
    private Double lat;
    private Double lng;
    private TaskAlarm alarm;
    private int sorting;
    private boolean isRepeating;

    // needed for serialization
    public TaskModel() {
    }

    public TaskModel(String categoryId, String id, String text, LatLng location, boolean isRepeating) {
        this.categoryId = categoryId;
        this.id = id;
        this.text = text;
        setLocation(location);
        this.isRepeating = isRepeating;
    }

    public TaskModel(String categoryId, String id, String text, LatLng location, TaskAlarm alarm, boolean isRepeating) {
        this.categoryId = categoryId;
        this.id = id;
        this.text = text;
        setLocation(location);
        this.alarm = alarm;
        this.isRepeating = isRepeating;
    }

    // Legacy to keep compatibility
    public TaskModel(String categoryId, String text) {
        this.categoryId = categoryId;
        this.id = "";
        this.text = text;
        this.alarm = null;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    @Exclude
    public LatLng getLocation() {
        if (lat != null && lng != null) {
            return new LatLng(lat, lng);
        } else {
            return null;
        }
    }
    @Exclude
    public void setLocation(LatLng location){
        if (location != null) {
            this.lat = location.latitude;
            this.lng = location.longitude;
        }
    }

    public TaskAlarm getAlarm() {
        return alarm;
    }

    public void setAlarm(TaskAlarm alarm) {
        this.alarm = alarm;
    }

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public boolean getIsRepeating(){
        return isRepeating;
    }

    public void setRepeating(boolean repeating) {
        isRepeating = repeating;
    }

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
