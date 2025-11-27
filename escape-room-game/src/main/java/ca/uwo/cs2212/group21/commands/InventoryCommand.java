package ca.uwo.cs2212.group21.commands;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Item;

import java.util.List;

public class InventoryCommand {

    // Runs the INVENTORY command and returns the message to show the player
    public String execute(GameState state) {

        // Get the list of items the player is currently holding
        List<Item> inventory = state.getInventory();

        // If there are no items, say so
        if (inventory == null || inventory.isEmpty()) {
            return "Your inventory is empty.";
        }

        // Otherwise build a list of item names
        StringBuilder result = new StringBuilder("You are carrying:\n");

        for (Item item : inventory) {
            result.append("- ").append(item.getName()).append("\n");
        }

        return result.toString();
    }
}
