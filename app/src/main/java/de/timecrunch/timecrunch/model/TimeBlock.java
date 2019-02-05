package de.timecrunch.timecrunch.model;

import java.util.ArrayList;

public class TimeBlock {

    private String categoryId;
    private String color;
    private int startHours;
    private int startMinutes;
    private int endHours;
    private int endMinutes;
    private ArrayList<TaskModel> tasks;

    public TimeBlock() {

    }

    public TimeBlock(String categoryId, String color, int startHours, int startMinutes, int endHours, int endMinutes) {
        this.categoryId = categoryId;
        this.color = color;
        this.startHours = startHours;
        this.startMinutes = startMinutes;
        this.endHours = endHours;
        this.endMinutes = endMinutes;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getStartHours() {
        return startHours;
    }

    public void setStartHours(int startHours) {
        this.startHours = startHours;
    }

    public int getStartMinutes() {
        return startMinutes;
    }

    public void setStartMinutes(int startMinutes) {
        this.startMinutes = startMinutes;
    }

    public int getEndHours() {
        return endHours;
    }

    public void setEndHours(int endHours) {
        this.endHours = endHours;
    }

    public int getEndMinutes() {
        return endMinutes;
    }

    public void setEndMinutes(int endMinutes) {
        this.endMinutes = endMinutes;
    }

    public ArrayList<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public void addTask(TaskModel task) {
        if(tasks == null) {
            this.tasks = new ArrayList<TaskModel>();
        }

        tasks.add(new TaskModel(
                getCategoryId(),
                task.getId(),
                task.getText(),
                task.getLocation(),
                task.getAlarm(),
                task.getIsRepeating()));
    }
}
