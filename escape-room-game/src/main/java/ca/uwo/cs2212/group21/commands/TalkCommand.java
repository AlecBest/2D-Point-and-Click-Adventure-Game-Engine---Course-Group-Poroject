package ca.uwo.cs2212.group21.commands;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Room;
import ca.uwo.cs2212.group21.model.NPC;

/**
 * Handles talking to the NPC in the player's current room.
 */
public class TalkCommand {

    /**
     * Executes the talk command.
     *
     * @param player the current GameState (player and world info)
     * @return the dialogue text to show in the UI
     */
    public String execute(GameState player) {

        if (player == null) {
            return "There is no active game.";
        }

        Room currentRoom = player.getCurrentRoom();
        if (currentRoom == null) {
            return "You are not in a room.";
        }

        NPC npc = currentRoom.getNPC();
        if (npc == null) {
            return "There is no one here to talk to.";
        }

        String dialogue = npc.getDialogue();

        if (dialogue == null || dialogue.isEmpty()) {
            return npc.getName() + " has nothing to say.";
        }

        return dialogue;
    }
}
