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
    public Boolean execute(GameState player, String direction) {

        // simple safety checks
        if (player == null || direction == null) {
            return false;
        }

        Room currentRoom = player.getCurrentRoom();

        if (currentRoom == null) {
            // this should not normally happen, but this stops a crash
            return false;
        }

        // look up the neighbouring room in the given directiion
        Room nextRoom = currentRoom.getExit(direction);

        // if there is no exit in that direction, stay where you are
        if (nextRoom == null) {
            return false;
        }

        // if the current room is locked, do not allow them to leave yet
        if (nextRoom.isLocked()) {
            return false;
        }

        // everything is okay, so move the player to the next room
        player.setCurrentRoom(nextRoom);
        player.incrementMovesCount();

        // simple message for now, you can change text later for story flavour
        return true;
    }
}

