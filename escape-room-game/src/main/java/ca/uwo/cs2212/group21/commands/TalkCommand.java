package ca.uwo.cs2212.group21.commands;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Room;
import ca.uwo.cs2212.group21.model.NPC;
import ca.uwo.cs2212.group21.model.Item;

public class TalkCommand {

    // Runs the TALK command for a given NPC name
    public String execute(GameState state, String npcName) {

        // If the player did not type a name
        if (npcName == null || npcName.isBlank()) {
            return "Talk to who?";
        }

        // 1: get the current room from the game state
        Room currentRoom = state.getCurrentRoom();
        if (currentRoom == null) {
            return "You are not in a room.";
        }

        // 2: get the NPC in this room 
        if (!currentRoom.hasNPC()) {
            return "There is no one here to talk to.";
        }

        NPC target = currentRoom.getNPC();
        if (target == null) {
            return "There is no one here to talk to.";
        }

        // check name matches
        if (!target.getName().equalsIgnoreCase(npcName)) {
            return "There is no one named " + npcName + " here.";
        }

        // 3: get their current dialogue line
        String line = getCurrentDialogueLine(target);   // placeholder for now
        if (line == null || line.isEmpty()) {
            return "They have nothing more to say.";
        }

        // 4: advance their dialogue state
        advanceDialogue(target);   // placeholder for now

        // 5: after talking, check if this NPC has an item to give
        Item reward = target.getItemToGive();      // uses existing getter
        if (reward != null) {
            state.addItemToInventory(reward);      // give it to player
            target.setItemToGive(null);            // clear so it is one time only

            return line + "\n" + target.getName() + " gives you " + reward.getName() + ".";
        }

        // The caller will put this line into the game text log
        return line;
    }

    // These two are placeholders until aiden finishes DialogueNode sooo reminder to me to put this once done

    private String getCurrentDialogueLine(NPC npc) {
        // TODO: replace with real DialogueNode logic later
        return npc.getName() + " says: \"[dialogue not implemented yet]\"";
    }

    private void advanceDialogue(NPC npc) {
        // TODO: replace with real DialogueNode logic later
    }
}
