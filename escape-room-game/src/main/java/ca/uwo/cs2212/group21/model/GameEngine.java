package ca.uwo.cs2212.group21.model;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.cs2212.group21.commands.PickUpCommand;
import ca.uwo.cs2212.group21.commands.DropCommand;


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
    private HashMap<String, Item> items;
    private List<Item> inventory;

    /**
     * Initializes a new GameEngine instance.
     */
    public GameEngine(String jsonPath) {
        this.rooms = new HashMap<>();
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

        this.player = new GameState(loadedRoom, timeRemaining); //this would put the player in wherever they were and with whatever time they had left 

        for (Item itemInList : tempInventory) { //this would add all the items in the save essentially to the players inventory
            this.player.addItemToInventory(itemInList);
             
            for (Room room : rooms.values()) {
                if (room.getItems().contains(itemInList)) { //this is because if the player has the item in their inventory it should be removed from the room so we dont duplicate stuff 
                    room.removeItem(itemInList);
                    break;
                } //im not sure if we would be saving anything else about the player like their position when they saved but if we do could just add that  here 
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
            Room room = new Room(roomName, roomDescription, isLocked, roomObject.getString("roomImagePath"));
            rooms.put(roomName, room);

            JSONArray itemArray = roomObject.getJSONArray("items"); //this is to go through each item in the room and add them to the specific room
            for (int j = 0; j < itemArray.length(); j++) {
                JSONObject itemObj = itemArray.getJSONObject(j);
                String itemName = itemObj.getString("item");
                boolean isUsable = itemObj.getBoolean("isItemUsable");
                String itemImagePath = itemObj.getString("itemImagePath");
                String itemDescription = itemObj.getString("itemDescription");
                double xPos = itemObj.getDouble("xPosition");
                double yPos = itemObj.getDouble("yPosition");
                double width = itemObj.getDouble("width");
                double height = itemObj.getDouble("height");


                if (itemName != null) {
                    Item item = new Item(itemName, itemDescription, isUsable, itemImagePath, xPos, yPos, width, height);
                    room.addItem(item);
                    items.put(itemName, item);
                }
            }

            JSONObject npcObj = roomObject.getJSONObject("npc"); //since we only have one npc it just gets all the stuff from the json file and makes the npc object so we can set it to the particular coords in each room 
            String npcName = npcObj.getString("npc");
            String npcDialogue = npcObj.getString("npcDialogue");
            boolean isNPCTradeable = npcObj.getBoolean("isNPCTradeable");
            double xPos = npcObj.getDouble("xPosition");
            double yPos = npcObj.getDouble("yPosition");
            if (npcName != null) {
                String npcImage = "/images/whateverItIs.png/";
                NPC npc = new NPC (npcName, npcDialogue, isNPCTradeable, npcImage, xPos, yPos);
                room.setNPC(npc);
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

    //command wrapper methods
    /**
     * runs PickUpCommand on current game state
     * lets other parts of program call pick up item through GameEngine
     */
    public String pickUpItem(String itemName){
        PickUpCommand cmd = new PickUpCommand();
        return cmd.execute(player, itemName);
    }

    // same thing for DropCommand
    public String dropItem(String itemName) {
        DropCommand cmd = new DropCommand();
        return cmd.execute(player, itemName);
    }

}
