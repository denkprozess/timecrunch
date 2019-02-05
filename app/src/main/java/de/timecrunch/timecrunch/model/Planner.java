package de.timecrunch.timecrunch.model;

import java.util.ArrayList;

public class Planner {

    private int year;
    private int month;
    private int day;
    private ArrayList<TimeBlock> blocks;

    public Planner() {

    }

    public Planner(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void createBlock(String categoryId, String color, int startHours, int startMinutes, int endHours, int endMinutes) {
        if(blocks == null) {
            blocks = new ArrayList<TimeBlock>();
        }

        blocks.add(new TimeBlock(categoryId, color, startHours, startMinutes, endHours, endMinutes));

    }

    public ArrayList<TimeBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(ArrayList<TimeBlock> blocks) {
        this.blocks = blocks;
    }
}
