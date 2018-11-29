package de.timecrunch.timecrunch.model;

public class Category {

    private String name;
    private final int id;
    private int color;
    private boolean hasTimeBlock;


    public Category(int id, String name, int color, boolean hasTimeBlock) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.hasTimeBlock = hasTimeBlock;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public int getColor() {
        return color;
    }

    public boolean hasTimeBlock() {
        return hasTimeBlock;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Category)) {
            return false;
        }
        if (((Category) other).getId() == (this.getId())) {
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
