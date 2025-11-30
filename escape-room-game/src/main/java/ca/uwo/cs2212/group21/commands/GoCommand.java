package ca.uwo.cs2212.group21.commands;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Room;

/**
 * GoCommand is responsible for moving the player from one room to another
 * It checks:
 *  - if the exit in that direction exists
 *  - if the current room is locked
 * and only moves the player if it is allowed
 */
public class GoCommand {

    /**
     * Tries to move the player in the given direction.
     * 
     * @param player    the current GameState
     * @param direction the direction to move in (for example, "NORTH") where the doors are locatedd
     * @return          a message describing what happened
     */
    public String execute(GameState player, String direction) {

        // simple safety checks
        if (player == null || direction == null) {
            return "You can't go that way.";
        }

        Room currentRoom = player.getCurrentRoom();

        if (currentRoom == null) {
            // this should not normally happen, but this stops a crash
            return "You are nowhere. Something went wrong.";
        }

        // look up the neighbouring room in the given directiion
        Room nextRoom = currentRoom.getExit(direction);

        // if there is no exit in that direction, stay where you are
        if (nextRoom == null) {
            return "You can't go that way.";
        }

        // if the current room is locked, do not allow them to leave yet
        if (currentRoom.isLocked()) {
            return "The door is locked. You cannot leave this room yet.";
        }

        // everything is okay, so move the player to the next room
        player.setCurrentRoom(nextRoom);

        // simple message for now, you can change text later for story flavour
        return "You move to " + nextRoom.getName() + ".";
    }
}

