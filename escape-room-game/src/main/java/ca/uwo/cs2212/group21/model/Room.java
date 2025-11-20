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
    private List<NPC> npcs;
    private HashMap<String, Room> exits;

    /**
     * Initializes a new room with the given parameters.
     * @param name        The name of the room.
     * @param description A brief description of the room.
     */
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.items = new ArrayList<>();
        this.npcs = new ArrayList<>();
        this.exits = new HashMap<>();
    }

    //Getters and Setters

    public void setExit(String direction, Room neighbRoom) {
        exits.put(direction, neighbRoom);
    }

    public void addItem (Item item) {
        items.add(item);
    }

    public void setNPC(NPC npc) {
        npcs.add(npc);
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

    public List<NPC> getNpcs() {
        return npcs;
    }

    public HashMap<String, Room> getExits() {
        return exits;
    }
    
    public Room getExit(String direction) {
        return exits.get(direction);
    }

}
