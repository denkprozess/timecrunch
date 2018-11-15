package de.timecrunch.timecrunch.model;

public class Category {

    private String name;
    private int color;

    public Category(String name, int color){
        this.name=name;
        this.color=color;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    @Override
    public boolean equals(Object other){
        if(other==this){
            return true;
        }
        if(!(other instanceof Category)){
            return false;
        }
        if(((Category) other).getName().equals(this.getName())){
            return true;
        }else{
            return false;
        }
    }


}
