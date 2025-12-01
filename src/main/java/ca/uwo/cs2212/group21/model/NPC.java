package ca.uwo.cs2212.group21.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents a non-player character (NPC) in the escape room game.
 * This class will store information about the NPC, such as its name, dialogue, and interactions
 */
public class NPC {
    private String name;
    private String dialogue;
    private boolean hasInteracted;
    private Item itemToGive;
    private Item requiredItem;
    private boolean isTradeable;
    private String imagePath;
    private String hint;
    private double x;
    private double y;
    

    /**
     * Initializes a new NPC with the given parameters.
     * @param name     The name of the NPC.
     * @param dialogue The dialogue associated with the NPC.
     * @param isTradeable Indicates if the NPC is willing to trade items.
     */
    public NPC(String name, String dialogue, boolean isTradeable, String imagePath, double x, double y) {
        this.name = name;
        this.dialogue = dialogue;
        this.isTradeable = isTradeable;
        this.hasInteracted = false;
        this.imagePath = imagePath;
        this.x = x;
        this.y = y;
    }

       /**
     * Determines if the NPC can trade with the player based on the item the player offers
     * returns true if the trade can occur, false otherwise.
     * @param playerItem
     * @return
     */
    public boolean canTrade(Item playerItem) {
        if (requiredItem == null || playerItem == null) { // If no item is required for trade or playerItem is null
            return false;
        }
        return isTradeable && playerItem.getName().equals(requiredItem.getName());
    }

    //Getters and Setters

    public void setItemToGive(Item itemToGive) {
        this.itemToGive = itemToGive;
    }
    
    public void setRequiredItem(Item requiredItem) {
        this.requiredItem = requiredItem;
    }

    public void setHint(String hint) { 
        this.hint = hint;
    }

    public void setHasInteracted(boolean hasInteracted) {
        this.hasInteracted = hasInteracted;
    }

    public String getName() {
        return name;
    }

    public String getDialogue() {
        return dialogue;
    }

    public Item getItemToGive() {
        return itemToGive;
    }

    public Item getRequiredItem() {
        return requiredItem;
    }
    
    public String getHint() {
        return hint;
    }

    public boolean isTradeable() {
        return isTradeable;
    }

    public boolean hasInteracted() {
        return hasInteracted;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getImagePath() {
        return imagePath;
    }

}