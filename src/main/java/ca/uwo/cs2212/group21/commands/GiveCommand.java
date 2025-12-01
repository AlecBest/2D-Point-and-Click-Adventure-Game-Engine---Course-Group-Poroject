package ca.uwo.cs2212.group21.commands;

import java.util.ArrayList;
import java.util.List;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Item;
import ca.uwo.cs2212.group21.model.Room;
import ca.uwo.cs2212.group21.model.NPC;

/**
 * 
 * Player gives an item from their inventory to an NPC in the current room.
 */
public class GiveCommand {

    /**
     * Executes the GIVE command.
     * If itemName is "all", tries to give all inventory items to the NPC.
     * @param state    current game state
     * @param npcName  name of the NPC to give the item to
     * @param itemName name of the item in the player's inventory, or "all"
     * @return message describing the result of the action
     */
    public String execute(GameState state, String npcName, String itemName) {

        if (npcName == null || npcName.isBlank()) {
            return "Give to who?";
        }

        if (itemName == null || itemName.isBlank()) {
            return "Give what?";
        }

        Room room = state.getCurrentRoom();
        if (room == null) {
            return "You are not in a room.";
        }

        if (!room.hasNPC()) {
            return "There is no one here to give items to.";
        }

        NPC target = room.getNPC();
        if (target == null) {
            return "There is no one here to give items to.";
        }

        if (!target.getName().equalsIgnoreCase(npcName)) {
            return "There is no one named " + npcName + " here.";
        }

        // Special case: give all items in inventory
        if (itemName.equalsIgnoreCase("all")) {
            return giveAllItems(state, target);
        }

        // Normal case: give a single item
        return giveSingleItem(state, target, itemName);
    }

    // Handle giving one specific item
    private String giveSingleItem(GameState state, NPC target, String itemName) {
        Item item = state.getItemFromInventory(itemName);
        if (item == null) {
            return "You do not have " + itemName + " in your inventory.";
        }

        // use NPC's trade rule
        if (!target.canTrade(item)) {
            return target.getName() + " does not seem interested in " + item.getName() + ".";
        }

        // remove from inventory
        state.removeItemFromInventory(item);

        // NPC gives something back if they have an itemToGive
        Item reward = target.getItemToGive();
        if (reward != null) {
            state.addItemToInventory(reward);
            target.setItemToGive(null);

            return "You give " + item.getName() + " to " + target.getName()
                    + ".\n" + target.getName() + " gives you " + reward.getName() + " in return.";
        }

        return "You give " + item.getName() + " to " + target.getName() + ".";
    }

    // Handle giving all items in inventory
    private String giveAllItems(GameState state, NPC target) {
        // make a copy so we do not modify the list while iterating
        List<Item> inventorySnapshot = new ArrayList<>(state.getInventory());

        if (inventorySnapshot.isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder message = new StringBuilder();
        boolean anyAccepted = false;

        for (Item item : inventorySnapshot) {
            if (target.canTrade(item)) {
                state.removeItemFromInventory(item);
                anyAccepted = true;
                message.append("You give ")
                       .append(item.getName())
                       .append(" to ")
                       .append(target.getName())
                       .append(".\n");
            }
        }

        if (!anyAccepted) {
            return target.getName() + " does not want anything you are carrying.";
        }

        // after giving everything, NPC may give a reward
        Item reward = target.getItemToGive();
        if (reward != null) {
            state.addItemToInventory(reward);
            target.setItemToGive(null);
            message.append(target.getName())
                   .append(" gives you ")
                   .append(reward.getName())
                   .append(" in return.");
        } else {
            message.append(target.getName())
                   .append(" takes your items.");
        }

        return message.toString();
    }
}
