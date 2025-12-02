package ca.uwo.cs2212.group21.model;

/**
 * Represents an item in the escape room game.
 * This class stores the name, description, and usability of the item.
 */

public class Item {

    private String name; 
    private String description;
    private boolean isKey;
    private boolean startHidden;
    private String imagePath;
    private double x;
    private double y;
    private double width;
    private double height;

      /**
     * Represents an item in the escape room game.
     * @param name        The name of the item.
     * @param description A brief description of the item.
     * @param isUsable    Indicates if the item can be used.    
     * @param imagePath   The path to the item's image.
     * @param x           The x-coordinate of the item's position.
     * @param y           The y-coordinate of the item's position.
     * @param width       The width of the item's image.
     * @param height      The height of the item's image.
     */
    public Item(String name, String description, boolean isPuzzleItem, boolean startHidden, String imagePath, double x, double y, double width, double height) {
        this.name = name;
        this.description = description;
        this.isKey = isKey;
        this.startHidden = startHidden;
        this.imagePath = imagePath;
        this.x = x; 
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public Item getItem(String itemName) {
        return this;
    }

    public String getDescription() {
        return description;
    }

    public boolean isKey() {
        return this.isKey;
    }

    public boolean isStartHidden() {
        return this.startHidden;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
