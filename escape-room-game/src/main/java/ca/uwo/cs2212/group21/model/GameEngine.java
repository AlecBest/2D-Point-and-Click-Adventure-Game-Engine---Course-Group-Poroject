package ca.uwo.cs2212.group21.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import ca.uwo.cs2212.group21.commands.UseCommand;
import ca.uwo.cs2212.group21.commands.GoCommand;
import ca.uwo.cs2212.group21.commands.TalkCommand;
import ca.uwo.cs2212.group21.commands.GiveCommand;


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
    private HashMap<Room, Item> roomPuzzleItems;
    private List<Item> inventory;
    private int movesCount;
    private int score;
    private org.json.JSONObject dialogueData; 

    /**
     * Initializes a new GameEngine instance.
     */
    public GameEngine(String jsonPath) {
        this.rooms = new HashMap<>();
        this.items = new HashMap<>();
        this.inventory = new ArrayList<>();
        this.roomPuzzleItems = new HashMap<>();
        
        loadWorldData(jsonPath);
        loadDialogueData();
    }

    public void startNewGame() {
            int initialTime = 600; // 10 minutes in seconds
            Room startingRoom = rooms.get("Main Room"); //we can change this to whatever we end up calling the starting room
            this.player = new GameState(startingRoom, initialTime);
    }

    /**
      * Loads a saved game state from a JSON file.
     * @param saveJSONpath
    */
    public void loadGame (String saveJSONpath) {
        try (InputStream inputStream = new FileInputStream(saveJSONpath)) { //path to the file we can probably put in resources folder

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

        movesCount = saveData.getInt("movesCount");
        this.player.setMovesCount(movesCount);

        score = saveData.getInt("score"); 
        this.player.setScore(score);

    } catch (Exception e) {
        e.printStackTrace();
        }
    }

    /**
    * Loads world data from a JSON file.
    * @param jsonPath
    */
    public void loadWorldData (String jsonPath) {
        
        try (InputStream inputStream = getClass().getResourceAsStream(jsonPath)) {
            if (inputStream == null) throw new FileNotFoundException("Couldn't find resource file " + jsonPath);

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
                boolean isKey = itemObj.getBoolean("isKey");
                boolean startHidden = itemObj.getBoolean("startHidden");
                String itemImagePath = itemObj.getString("itemImagePath");
                String itemDescription = itemObj.getString("itemDescription");
                double xPos = itemObj.getDouble("xPosition");
                double yPos = itemObj.getDouble("yPosition");
                double width = itemObj.getDouble("width");
                double height = itemObj.getDouble("height");


                if (itemName != null) {
                    Item item = new Item(itemName, itemDescription, isKey, startHidden, itemImagePath, xPos, yPos, width, height);
                
                    if (!startHidden){
                        room.addItem(item);
                    }
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
                String npcImage = "/images/defaultNPC.png";
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
                double xPos = exitObj.getDouble("xPosition");
                double yPos = exitObj.getDouble("yPosition");
                double width = exitObj.getDouble("width");
                double height = exitObj.getDouble("height");
                if (exitName != null) {
                    currentRoom.setExit(exitDirection, rooms.get(exitName), xPos, yPos, width, height);
                }
            }

            String puzzleItemName = roomObject.optString("puzzleItem", null);
            if (puzzleItemName != null) {
                Item puzzleItem = items.get(puzzleItemName);
                if (puzzleItem != null) {
                    roomPuzzleItems.put(currentRoom, puzzleItem);
                    currentRoom.setPuzzleItem(puzzleItem);
                }
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveGame(String fileName) {
        JSONObject saveData = new JSONObject();
        JSONArray inventoryArray = new JSONArray();

        saveData.put("currentRoom", player.getCurrentRoom().getName());
        saveData.put("timeRemaining", player.getTimeRemaining());
        saveData.put("movesCount", this.getPlayer().getMovesCount());
        saveData.put("score", this.getPlayer().getScore());

        for (Item item : player.getInventory()) {
            inventoryArray.put(item.getName());
        }
        saveData.put("inventory", inventoryArray);

        try (FileWriter file = new FileWriter("saves/" + fileName)) {
            file.write(saveData.toString(4)); // this is just for readability
            file.flush(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadDialogueData() {
        try (InputStream inputStream = getClass().getResourceAsStream("/dialogues.json")) {
            if (inputStream == null) throw new FileNotFoundException("dialogues.json not found");

            JSONTokener tokener = new JSONTokener(inputStream);
            this.dialogueData = new JSONObject(tokener);
        } catch (Exception e) {
            e.printStackTrace();
            this.dialogueData = new JSONObject();
        }
    }

    public void loadRecipes() {
    try (InputStream inputStream = getClass().getResourceAsStream("/recipes.json")) {
        if (inputStream == null) throw new FileNotFoundException("recipes.json not found");
        
        JSONTokener tokener = new JSONTokener(inputStream);
        JSONArray jsonRecipes = new JSONArray(tokener);
        
        List<Recipe> recipeList = new ArrayList<>();
        
        for (int i = 0; i < jsonRecipes.length(); i++) {
            JSONObject obj = jsonRecipes.getJSONObject(i);
            
            JSONArray inputsArray = obj.getJSONArray("inputs");
            List<String> inputs = new ArrayList<>();
            for(int j=0; j<inputsArray.length(); j++) {
                inputs.add(inputsArray.getString(j).toLowerCase());
            }
            
            String result = obj.getString("result");
            String msg = obj.getString("successMessage");
            
            recipeList.add(new Recipe(inputs, result, msg));
        }
        
        player.setRecipes(recipeList);
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public HashMap<String, Room> getRooms() {
    return rooms;
    }

    public GameState getPlayer() {
    return player;
    }

    public HashMap<String, Item> getItemList() {
        return items;
    }

    public  JSONObject getDialogueData() {
        return this.dialogueData;
    }

    // -- command wrapper methods --

    // PickUpCommand wrapper method
    public String pickUpItem(String itemName) {
        PickUpCommand cmd = new PickUpCommand();
        String result = cmd.execute(player, itemName);

        // one successful player action = one turn
        player.incrementMovesCount();

        return result;
    }

    // DropCommand wrapper method
    public String dropItem(String itemName) {
        DropCommand cmd = new DropCommand();
        String result = cmd.execute(player, itemName);

        player.incrementMovesCount();
        return result;
    }

    // UseCommand wrapper method
    public String useItem(Item itemName, Item secondItemName) {
        UseCommand cmd = new UseCommand();
        String result = cmd.execute(player, itemName, secondItemName, this.items);

        player.incrementMovesCount();
        return result;
    }

    // GoCommand wrapper method
    public boolean go(String direction) {
        GoCommand cmd = new GoCommand();
        boolean success = cmd.execute(player, direction);

        // treat every Go as a turn, even if it fails
        player.incrementMovesCount();
        return success;
    }

    // TalkCommand wrapper method
    public String talkToNpc() {
        TalkCommand cmd = new TalkCommand();
        String result = cmd.execute(player);

        player.incrementMovesCount();
        return result;
    }

    // GiveCommand wrapper method
    public String giveItemToCurrentNpc(String itemName) {
        GiveCommand cmd = new GiveCommand();

        Room room = player.getCurrentRoom();
        if (room == null || !room.hasNPC()) {
            player.incrementMovesCount();
            return "There is no one here to give items to.";
        }

        NPC npc = room.getNPC();
        if (npc == null) {
            player.incrementMovesCount();
            return "There is no one here to give items to.";
        }

        String result = cmd.execute(player, npc.getName(), itemName);
        player.incrementMovesCount();
        return result;
    }

    public void playerMove(double newX, double newY) {
        player.setPosition(newX, newY);
    }
}
