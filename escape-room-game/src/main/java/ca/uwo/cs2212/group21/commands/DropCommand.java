package ca.uwo.cs2212.group21.commands;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Item;
import ca.uwo.cs2212.group21.model.Room;

// this command handles dropping an item from player's inventory back into the room
public class DropCommand {
    /** 
     * @param state
     * @param itemName
     * @return String
     */
    public String execute(GameState state, String itemName){

        //only drop items that are currently in inventory already. this covers following cases:
        //1. dropping item immediately after picking up
        //2. dropping item from inventory screen later
        Item item = state.findItemInventory(itemName);

        if (item==null){
            return itemName + " is not in your inventory.";
        }

        // remove item from inventory
        state.removeItemFromInventory(item);

        //put/drop item back into room
        Room room = state.getCurrentRoom();
        room.addItem(item);
        return item.getName() + " dropped.";
    }
    
}
