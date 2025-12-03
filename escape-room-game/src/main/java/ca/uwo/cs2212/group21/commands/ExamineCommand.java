package ca.uwo.cs2212.group21.commands;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Item;
import ca.uwo.cs2212.group21.model.Room;

//this command class handles the Examine command behaviour and player can either look around the room or examine a specific item

public class ExamineCommand {

    //examine itemName 
    public String execute(GameState state, String itemName) {

        //get the room the player is currently in
        Room room = state.getCurrentRoom();

        //Case 1: no specific item given -> return the room description
        if (itemName == null || itemName.isBlank()) {
            return room.getDescription();
        }

        //trim input to avoid issues with extra spaces
        String targetName = itemName.trim();

        //Case 2: check if item is in player's inventory
        Item inventoryItem = state.findItemInventory(targetName);
        if (inventoryItem != null) {
            return inventoryItem.getDescription();
        }

        //Case 3: check if item is in the current room
        Item roomItem = room.findItem(targetName); 
        if (roomItem != null) {
            return roomItem.getDescription();
        }

        //Case 4: item not found anywhere
        return "You do not see " + itemName + " here.";
    }
}
