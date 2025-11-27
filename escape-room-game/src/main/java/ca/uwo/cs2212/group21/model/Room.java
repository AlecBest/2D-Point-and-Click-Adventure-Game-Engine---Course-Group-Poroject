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
    private boolean isLocked;
    private String imagePath;
    private NPC npc;

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
        this.isLocked = isLocked;
        this.imagePath = imagePath;
        this.npc = null;
    }

    //Getters and Setters

    public void setExit(String direction, Room neighbRoom) {
        exits.put(direction, neighbRoom);
    }

    public void setNPC(NPC npc) {
        this.npc = npc;
    }

    public NPC getNPC() {
        return npc;
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
    
    public Room getExit(String direction) {
        return exits.get(direction);
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