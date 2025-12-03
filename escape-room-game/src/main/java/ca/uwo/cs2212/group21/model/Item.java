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
public Item(String name, String description, boolean isKey, boolean startHidden,
        String imagePath, double x, double y, double width, double height) {
    this.name = name;
    this.description = description;
    this.isKey = isKey;          // uses the parameter
    this.startHidden = startHidden;
    this.imagePath = imagePath;
    this.x = x; 
    this.y = y;
    this.width = width;
    this.height = height;
}

    /** 
     * @return String
     */
    //Getters and Setters

    public String getName() {
        return name;
    }

    /** 
     * @param itemName
     * @return Item
     */
    public Item getItem(String itemName) {
        return this;
    }

    /** 
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /** 
     * @return boolean
     */
    public boolean isKey() {
        return this.isKey;
    }

    /** 
     * @return boolean
     */
    public boolean isStartHidden() {
        return this.startHidden;
    }

    /** 
     * @return String
     */
    public String getImagePath() {
        return imagePath;
    }

    /** 
     * @return double
     */
    public double getX() {
        return x;
    }

    /** 
     * @return double
     */
    public double getY() {
        return y;
    }

    /** 
     * @return double
     */
    public double getWidth() {
        return width;
    }

    /** 
     * @return double
     */
    public double getHeight() {
        return height;
    }
}
