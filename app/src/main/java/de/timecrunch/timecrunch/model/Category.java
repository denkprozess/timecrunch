package de.timecrunch.timecrunch.model;

public class Category {

    private String name;
    private String id;
    private int color;
    private boolean hasTimeBlock;
    private int sorting;


    public Category(){
    }

    public Category(String id, String name, int color, boolean hasTimeBlock) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.hasTimeBlock = hasTimeBlock;
    }

    public String getId() {
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

    public int getSorting() {
        return sorting;
    }

    public void setSorting(int sorting) {
        this.sorting = sorting;
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Category)) {
            return false;
        }
        if (((Category) other).getId().equals(this.getId())) {
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
