package ca.uwo.cs2212.group21.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single node in a dialogue tree.
 */
public class DialogueNode {

    private String text;
    private Map<String, DialogueNode> options;

    /**
     * Constructs a new DialogueNode with the given text.
     *
     * @param text The dialogue text for this node.
     */
    public DialogueNode(String text) {
        this.text = text;
        this.options = new HashMap<>();
    }

    /**
     * Gets the dialogue text of this node.
     *
     * @return The dialogue text.
     */
    public String getText() {
        return text;
    }

    /**
     * Adds a response option that leads to another dialogue node.
     *
     * @param optionText The text for the player's response option.
     * @param nextNode   The DialogueNode that follows if this option is chosen.
     */
    public void addOption(String optionText, DialogueNode nextNode) {
        options.put(optionText, nextNode);
    }

    /**
     * Gets the next dialogue node based on the player's chosen option.
     *
     * @param optionText The text of the chosen option.
     * @return The next DialogueNode, or null if the option doesn't exist.
     */
    public DialogueNode getNextNode(String optionText) {
        return options.get(optionText);
    }

    /**
     * Gets the map of available options from this node.
     *
     * @return A map where keys are option texts and values are the corresponding next DialogueNodes.
     */
    public Map<String, DialogueNode> getOptions() {
        return options;
    }

    /**
     * Checks if this is a terminal node (i.e., has no options to continue the dialogue).
     *
     * @return true if there are no options, false otherwise.
     */
    public boolean isTerminal() {
        return options.isEmpty();
    }
}
