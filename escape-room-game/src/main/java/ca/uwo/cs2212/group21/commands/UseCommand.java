package ca.uwo.cs2212.group21.commands;
import java.util.HashMap;

import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Item;
import ca.uwo.cs2212.group21.model.Recipe;


// handles the Use command
// can use one inventory item with another (combining items) or combine key fragments to make master key
// can use key or key fragment to open a door
public class UseCommand {

    // runs Use command
    // if secondItemName is null or blank, this treats it as "use item alone" (ex. key)
    // if secondItemName is given, then this treats it as "use item with another item" (ex. combine)
    public String execute (GameState state, Item itemName, Item secondItemName, HashMap<String, Item> masterList){

        if (secondItemName == null){

            //use single item
            return useSingle(state, itemName);
        } else {
            return useWithItem(state, itemName,secondItemName, masterList);
        }
    }

    //use one item by itself
    private String useSingle(GameState state, Item itemName){

        if (itemName==null){

            //player tries to use something they're not holding
            return "you are not carrying " + itemName + ".";
        }

        //placeholder to plug more logic in later after testing, ex. key on final door
        return "nothing happens if you use " + itemName.getName() + " by itself.";
    }

    //for using an inventory item with another (combining items)
    private String useWithItem(GameState state, Item first, Item second, HashMap<String, Item> masterList){

        String name1 = first.getName();
        String name2 = second.getName();

        if (state.getRecipes() != null) {
            for (Recipe recipe : state.getRecipes()) {
                if (recipe.matches(name1, name2)) {
                    state.removeItemFromInventory(first); //if the match itself is in the recipes then would remove both items from inventory
                    state.removeItemFromInventory(second);

                    Item resultItem = masterList.get(recipe.getResultName()); //would find the item in the list since all items would be in the master list, just some we have is not shown in the room

                    if (resultItem != null) {
                        state.addItemToInventory(resultItem); //if it finds it then would just add it 
                        return recipe.getMessage(); //return the message from the recipe json can make it so it goes to the npc for dialogue purposes idk we dont need to do this can just use from dialogue
                    } else {
                        System.err.println("Result item " + recipe.getResultName() + " not found in master list.");
                        return "An error occurred while combining the items.";
                    }
                }
            }
        }
        //if recipe didnt match any known combinations then would decrease time and reutnr the items back to inventory
        state.decreaseTime(30);
        state.addItemToInventory(first); //to make sure no deadlock happens 
        state.addItemToInventory(second);
        return "oof...these items don't react well together. you've lost 30 seconds.";
    }

    // helper to check if two item names match the correct recipe pair (order doesnt matter)
    private boolean isPair(String a, String b, String x, String y) {
        return (a.equals(x) && b.equals(y)) || (a.equals(y) && b.equals(x));
    }
}
