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
    private List<Recipe> recipes;
    private String imagePath;
    private boolean isGameOver;
    private int timeRemaining;
    private double playerX;
    private double playerY;
    private int score;
    private int movesCount;
    
    /**
     * Initializes a new game state.
     * @param startRoom
     * @param initialTime
     */
    public GameState(Room startRoom, int initialTime) {

        this.currentRoom = startRoom;
        this.inventory = new ArrayList<>();
        this.recipes = new ArrayList<>();
        this.isGameOver = false;
        this.timeRemaining = initialTime;

        this.imagePath = "/images/guard.png"; //just test

        this.playerX = 600.0; //default starting x position we can change this later
        this.playerY = 360.0; //default starting y position

        this.movesCount = 0; //the requirements said something about turn based so not sure if the timer is enough so adding this for clicks/moves 
        this.score = 3; // assuming we start with 3 stars then they go down like the minecraft hunger bar 
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

    // helper so commands can call this directly for inventory lookup
    public Item findItemInventory(String itemName) {
        return getItemFromInventory(itemName);
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    // getters and setters
    
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void setPosition(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }

    public double getPlayerX() {
        return playerX;
    }

    public double getPlayerY() {
        return playerY;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void setMovesCount(int movesCount) {
        this.movesCount = movesCount;
    }

    public void incrementMovesCount() {
        this.movesCount++;
    }

    public int getMovesCount() {
        return movesCount;
    }

    public void setPlayerImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return this.imagePath;
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

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    // this method dcreases the time the player has by a certain amount
    //if the time reaches zero or below, then the game will end
    public void decreaseTime(int amount){
        this.timeRemaining -= amount;
        if (this.timeRemaining <= 0){
            this.timeRemaining = 0;
            this.isGameOver = true;
        }
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
