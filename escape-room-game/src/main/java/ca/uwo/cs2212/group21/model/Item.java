package ca.uwo.cs2212.group21.model;

/**
 * Represents an item in the escape room game.
 * This class stores the name, description, and usability of the item.
 */

public class Item {

    private String name; 
    private String description;
    private boolean isUsable;

      /**
     * Represents an item in the escape room game.
     * @param name        The name of the item.
     * @param description A brief description of the item.
     * @param isUsable    Indicates if the item can be used.    
     */
    public Item(String name, String description, boolean isUsable) {
        this.name = name;
        this.description = description;
        this.isUsable = isUsable;
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUsable() {
        return isUsable;
    }
}
