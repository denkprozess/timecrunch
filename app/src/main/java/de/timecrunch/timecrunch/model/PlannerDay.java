package de.timecrunch.timecrunch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlannerDay {

    private int year;
    private int month;
    private int day;
    private Map<String, TimeBlock> blocks;

    public PlannerDay() {

    }

    public PlannerDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String createBlock(String categoryId, String color, int startHours, int startMinutes, int endHours, int endMinutes) {
        if(blocks == null) {
            blocks = new HashMap<>();
        }
        String id = UUID.randomUUID().toString();
        blocks.put(id, new TimeBlock(categoryId, color, startHours, startMinutes, endHours, endMinutes));
        return id;
    }

    public void changeBlock(String timeBlockId, TimeBlock timeBlock){
        blocks.put(timeBlockId,timeBlock);
    }

    public void removeTimeBlock(String timeBlockId){
        blocks.remove(timeBlockId);
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public Map<String, TimeBlock> getBlocks() {
        return blocks;
    }

    public TimeBlock getTimeBlock(String id){
        return blocks.get(id);
    }

    public void setBlocks(Map<String, TimeBlock> blocks) {
        this.blocks = blocks;
    }
}
