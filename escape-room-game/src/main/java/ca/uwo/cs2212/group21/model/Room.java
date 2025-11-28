package ca.uwo.cs2212.group21.model;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a room in the escape room game.
 * This class will store information about the room, such as its description, items present, and possible exits.
 */
public class Room {
    private String name;
    private String description;
    private List<Item> items;
    private HashMap<String, Room> exits;
    private List<String> exitList;
    private boolean isLocked;
    private String imagePath;
    private NPC npc;
    private double exitX;;
    private double exitY;  
    private double exitWidth;
    private double exitHeight;

    /**
     * Initializes a new room with the given parameters.
     * @param name        The name of the room.
     * @param description A brief description of the room.
     * @param isLocked    Indicates if the room is locked.
     * @param imagePath   The path to the room's image.
     */
    public Room(String name, String description, boolean isLocked, String imagePath) {
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>();
        this.exits = new HashMap<>();
        this.exitList = new ArrayList<>();
        this.isLocked = isLocked;
        this.imagePath = imagePath;
        this.npc = null;
        this.exitX = 0;
        this.exitY = 0;
        this.exitWidth = 0;
        this.exitHeight = 0;
    }

    //Getters and Setters

    public void setExit(String direction, Room neighbRoom, double x, double y, double width, double height) {
        exits.put(direction, neighbRoom);
        exitList.add(direction);
        this.exitX = x;
        this.exitY = y;
        this.exitWidth = width;
        this.exitHeight = height;
    }

    public void setNPC(NPC npc) {
        this.npc = npc;
    }

    public NPC getNPC() {
        return npc;
    }

    //talkcommand issues when compiling...this keeps it compiling for now so i can test some commands
    //can be removed once TalkCommand is updated to use getNPC()
    public java.util.List<NPC> getNPCS(){
        java.util.List<NPC> list = new java.util.ArrayList<>();
        if (npc != null) {
            list.add(npc);
        }
        return list;
    }

    public boolean hasNPC() {
        return this.npc != null;
    }

    public void addItem (Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    //helper method to find an item in this room by name
    // returns the item if found. otherwise returns null
    public Item findItem(String itemName){
        for (Item item : items) { //loop through every item currently in the room
            if (item.getName().equalsIgnoreCase(itemName)) { // compares items name to name being searched for, not case sensitive
                return item;
            }
        }

        return null;
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Item> getItems() {
        return items;
    }

    public HashMap<String, Room> getExits() {
        return exits;
    }

    public List<String> getExitList() {
        return exitList;
    }
    
    public Room getExit(String direction) {
        return exits.get(direction);
    }
    
    public double getExitX(String direction) {
        return this.exitX;
    }

    public double getExitY(String direction) {
        return this.exitY;
    }
    
    public double getExitWidth(String direction) {
        return this.exitWidth;
    }
    
    public double getExitHeight(String direction) {
        return this.exitHeight; 
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String getImagePath() {
        return imagePath;
    }
}