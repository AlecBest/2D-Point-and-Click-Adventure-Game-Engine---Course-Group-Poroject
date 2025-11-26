package ca.uwo.cs2212.group21.model;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The GameEngine class is responsible for managing the core mechanics and flow of the escape room game.
 * It will handle player actions, game state updates, and interactions between different game components.
 */
public class GameEngine {

    private GameState player;
    private Room currentRoom;
    private int timeRemaining;
    private boolean isGameOver;
    private String fileSavePath;
    private HashMap<String, Room> rooms;
    private HashMap<String, NPC> npcs;
    private HashMap<String, Item> items;
    private List<Item> inventory;

    /**
     * Initializes a new GameEngine instance.
     */
    public GameEngine(String jsonPath) {
        this.rooms = new HashMap<>();
        this.npcs = new HashMap<>();
        this.items = new HashMap<>();
        this.inventory = new ArrayList<>();
        loadWorldData(jsonPath);
    }

    public void startNewGame() {
             int initialTime = 60; 
            Room startingRoom = rooms.get("Main Room"); //we can change this to whatever we end up calling the starting room
            this.player = new GameState(startingRoom, initialTime);
    }

    /**
      * Loads a saved game state from a JSON file.
     * @param saveJSONpath
    */
    public void loadGame (String saveJSONpath) {
        InputStream inputStream = getClass().getResourceAsStream(saveJSONpath); //path to the file we can probably put in resources folder
        if (inputStream == null) {
            throw new NullPointerException("Couldn't find resource file " + saveJSONpath);
        }

        JSONTokener tokener = new JSONTokener(inputStream); //this is the tokenizer to read the jsonfile
        JSONObject saveData = new JSONObject(tokener); //this is to create a json object from the tokenizer
        JSONArray inventoryArray = saveData.getJSONArray("inventory"); //this is to get the array of items in the inventory from the json file
        List<Item> tempInventory = new ArrayList<>();

        for (int i = 0; i < inventoryArray.length(); i++) {
            String itemName = inventoryArray.getString(i);
            Item item = items.get(itemName);
            if (item != null) {
                tempInventory.add(item);
            }
        }

        String currentRoomName = saveData.getString("currentRoom");
        Room loadedRoom = rooms.get(currentRoomName);

        int timeRemaining = saveData.getInt("timeRemaining");

        this.player = new GameState(loadedRoom, timeRemaining);

        for (Item itemInList : tempInventory) {
            this.player.addItemToInventory(itemInList);
             
            for (Room room : rooms.values()) {
                if (room.getItems().contains(itemInList)) {
                    room.removeItem(itemInList);
                    break;
                }
            }
            
        }
    }

    /**
    * Loads world data from a JSON file.
    * @param jsonPath
    */
    public void loadWorldData (String jsonPath) {
        InputStream inputStream = getClass().getResourceAsStream(jsonPath); //path to the file we can probably put in resources folder 

        if (inputStream == null) {
            throw new NullPointerException("Couldn't find resource file " + jsonPath);
        }

        JSONTokener tokener = new JSONTokener(inputStream); //this is the tokenizer to read the jsonfile 
        JSONObject worldData = new JSONObject(tokener); //this is to create a json object from the tokenizer
        JSONArray roomsArray = worldData.getJSONArray("rooms"); //this is to get the array of rooms from the json file

        for (int i = 0; i < roomsArray.length(); i++) { //this is to go through each room in the json file and create room objects
            JSONObject roomObject = roomsArray.getJSONObject(i);
            String roomName = roomObject.getString("name");
            String roomDescription = roomObject.getString("description");
            boolean isLocked = roomObject.getBoolean("isLocked");
            Room room = new Room(roomName, roomDescription, isLocked);
            rooms.put(roomName, room);

            JSONArray itemArray = roomObject.getJSONArray("items"); //this is to go through each item in the room and add them to the specific room
            for (int j = 0; j < itemArray.length(); j++) {
                JSONObject itemObj = itemArray.getJSONObject(j);
                String itemName = itemObj.getString("item");
                if (itemName != null) {
                    Item item = new Item(itemName, itemObj.getString("itemDescription"), itemObj.getBoolean("isItemUsable"));
                    room.addItem(item);
                    items.put(itemName, item);
                }
            }

            JSONArray npcArray = roomObject.getJSONArray("npcs"); //this is to go through each npc in the room and add them to the specific room
            for (int j = 0; j < npcArray.length(); j++) {
                JSONObject npcObj = npcArray.getJSONObject(j);
                String npcName = npcObj.getString("npc");
                if (npcName != null) {
                    NPC npc = new NPC (npcName, npcObj.getString("npcDialogue"), npcObj.getBoolean("isNPCTradeable"), npcObj.getString("npcImagePath"));
                    room.setNPC(npc);
                    npcs.put(npcName, npc);
                }
            }
            
        }

        for (int i = 0; i < roomsArray.length(); i++) {
            JSONObject roomObject = roomsArray.getJSONObject(i); //this is the current room being processed
            Room currentRoom = rooms.get(roomObject.getString("name")); //this is to get the specific room object from the hashmap
            JSONArray exitArray = roomObject.getJSONArray("exits"); //this is to go through each exit of the current room 

            for (int j = 0; j < exitArray.length(); j++) { //would go through each exit then map it to the current room 
                JSONObject exitObj = exitArray.getJSONObject(j);
                String exitName = exitObj.getString("exit");
                String exitDirection = exitObj.getString("exitDirection");
                if (exitName != null) {
                    currentRoom.setExit(exitDirection, rooms.get(exitName));
                }
            }
        }
    }

    public HashMap<String, Room> getRooms() {
    return rooms;
    }

    public GameState getPlayer() {
    return player;
    }

}
