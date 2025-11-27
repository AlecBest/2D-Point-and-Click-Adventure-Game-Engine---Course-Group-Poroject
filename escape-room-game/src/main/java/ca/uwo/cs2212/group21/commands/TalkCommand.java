package ca.uwo.cs2212.group21.commands;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Room;
import ca.uwo.cs2212.group21.model.NPC;

import java.util.List;

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

        // 2: get the NPCs in this room
        List<NPC> npcs = currentRoom.getNPCS(); 
        if (npcs == null || npcs.isEmpty()) {
            return "There is no one here to talk to.";
        }

        // 3: find the NPC with that name
        NPC target = null;
        for (NPC npc : npcs) {
            if (npc.getName().equalsIgnoreCase(npcName)) { 
                target = npc;
                break;
            }
        }

        if (target == null) {
            return "There is no one named " + npcName + " here.";
        }

        // 4: get their current dialogue line
        String line = getCurrentDialogueLine(target);   // placeholder for now
        if (line == null || line.isEmpty()) {
            return "They have nothing more to say.";
        }

        // 5: advance their dialogue state
        advanceDialogue(target);   // placeholder for now

        // The caller will put this line into the game text log
        return line;
    }

    // These two are placeholders until DialogueNode implemented sooo reminder to me to put this once done

    private String getCurrentDialogueLine(NPC npc) {
        // TODO: replace with real DialogueNode logic later
        return npc.getName() + " says: \"[dialogue not implemented yet]\"";
    }

    private void advanceDialogue(NPC npc) {
        // TODO: replace with real DialogueNode logic later
        // i just put these here for now to see if the code compiles 
    }
}
