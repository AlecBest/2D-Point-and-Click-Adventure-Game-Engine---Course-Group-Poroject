package ca.uwo.cs2212.group21.commands;
import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Item;
import ca.uwo.cs2212.group21.model.Room;

//this command class handles the Pick Up command behaviour
//take an item name -> checks if item is in the current room -> if in current room -> move item into player's inventory
public class PickUpCommand {

    //pick up itemName
    public String execute(GameState state, String itemName) {

        //get room the player is currenlty in 
        Room room = state.getCurrentRoom();
        
        //use findItem helper from Room.java to find the item by name
        Item item=room.findItem(itemName);

        //if the room doesnt have this item -> player cant pick it up
        //safe check, item shouldnt be null but if the command gets called with wrong name, this returns message instead of crashing
        //for ui bugs, json change, edited features... avoids crashing
        if (item==null){
            return itemName + " is not in the room.";
        }

        //move item from room into player's invenctory 
        room.removeItem(item);
        state.addItemToInventory(item);

        //state.incrementMovesCount(); // increment moves count when picking up item we can just leave it like moves and items for now 


        return item.getName() + " picked up.";
    }
    
}
