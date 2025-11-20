package ca.uwo.cs2212.group21.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents the current state of the escape room game.
 * This class will manage the player's progress, inventory, and other game-related data.
 */

public class GameState {
 
    private Room currentRoom;
    private List<Item> inventory;
    private boolean isGameOver;
    private int timeRemaining;
    
    /**
     * Initializes a new game state.
     * @param startRoom
     * @param initialTime
     */
    public GameState(Room startRoom, int initialTime) {

        this.currentRoom = startRoom;
        this.inventory = new ArrayList<>();
        this.isGameOver = false;
        this.timeRemaining = initialTime;
    }

    /**
     * Checks if an item is in the player's inventory by its name.
     * if found, returns the item,  otherwise, returns null. 
     * @param itemName
     * @return
     */
    public Item getItemFromInventory (String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null; //item not found
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    // getters and setters
    
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItemToInventory(Item item) {
        inventory.add(item);
    }

    public void removeItemFromInventory(Item item) {
        inventory.remove(item);
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
