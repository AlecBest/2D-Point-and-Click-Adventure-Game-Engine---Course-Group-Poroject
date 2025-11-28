package ca.uwo.cs2212.group21.commands;
import ca.uwo.cs2212.group21.model.GameState;
import ca.uwo.cs2212.group21.model.Item;


// handles the Use command
// can use one inventory item with another (combining items) or combine key fragments to make master key
// can use key or key fragment to open a door
public class UseCommand {

    // runs Use command
    // if secondItemName is null or blank, this treats it as "use item alone" (ex. key)
    // if secondItemName is given, then this treats it as "use item with another item" (ex. combine)
    public String execute (GameState state, String itemName, String secondItemName) {

        if (secondItemName == null || secondItemName.trim().isEmpty()){

            //use single item
            return useSingle(state, itemName);
        } else {
            return useWithItem(state, itemName,secondItemName);
        }
    }

    //use one item by itself
    private String useSingle(GameState state, String itemName){

        Item item = state.findItemInventory(itemName);
        if (item==null){

            //player tries to use something they're not holding
            return "you are not carrying " + itemName + ".";
        }

        //placeholder to plug more logic in later after testing, ex. key on final door
        return "nothing happens if you use " + item.getName() + " by itself.";
    }

    //for using an inventory item with another (combining items)
    private String useWithItem(GameState state, String itemNameA, String itemNameB){

        //find both items in players inventory
        Item first = state.findItemInventory(itemNameA);
        Item second = state.findItemInventory(itemNameB);

        if (first == null || second == null){

            //checks that both items are in inventory in order to combine them
            return "both items must be in inventory to combine.";
        }

        // so names aren't case sensitive
        String a = first.getName().toLowerCase();
        String b = second.getName().toLowerCase();

        // room 1: candle + holy oil = purified candle (correct combo)
        if (isPair(a, b, "candle", "holy oil")) {

            //removes items from inventory
            state.removeItemFromInventory(first);
            state.removeItemFromInventory(second);

            //combine items to create new item -> add it to inventory
            Item purifiedCandle = new Item(
                "Purified Candle",
                "a candle coated in holy oil.",
                true,
                "/images/purified_candle.png",
                0,0,0,0 
            );

            state.addItemToInventory(purifiedCandle);

            return "you coat the candle with holy oil. it hardens into a purified candle...";
        }

        // other combinations can be filled in here later
        // room 2 - book
        //room 3 - mystic meal
        // room 4 - key fragments -> key

        // wrong combo
        // any pair of items that isn't the proper "recipe" -> NPC doesn't like, time penalty
        // *** make sure time decrease is in seconds *****
        state.decreaseTime(30);
        return "these items don't react well together. you've lost 30 seconds!";
    }

    // helper to check if two item names match the correct recipe pair (order doesnt matter)
    private boolean isPair(String a, String b, String x, String y) {
        return (a.equals(x) && b.equals(y)) || (a.equals(y) && b.equals(x));
    }
}
