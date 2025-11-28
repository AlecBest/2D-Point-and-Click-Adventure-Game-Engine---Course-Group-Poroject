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

        // room 2: torn book + missing pages = restored book
        if (isPair(a, b, "torn book", "missing pages")){

            // remove items from inventory
            state.removeItemFromInventory(first);
            state.removeItemFromInventory(second);

            //combine items
            Item restoredBook = new Item(
                "Restored Book",
                "an old book with all its' pages back in place.",
                true,
                "/images/restored_book.png",
                0,0,0,0
            );

            state.addItemToInventory(restoredBook);
            return "you've matched the loose pages with the torn book. the book looks whole again...";
        }

        // room 3: bread + cheese = mystic meal
        if (isPair(a,b, "bread", "cheese")){

            //remove items from inventory
            state.removeItemFromInventory(first);
            state.removeItemFromInventory(second);

            //combine items
            Item mysticMeal = new Item(
                "Mystic Meal",
                "a meal charged with energy",
                true,
                "/images/mystic_meal.png",
                0,0,0,0
            );

            state.addItemToInventory(mysticMeal);
            return "bread and cheese. simple yet comforting...";
        }

        // room 4: add two key fragments to create partial key 
        // -> player has three key fragments, but combines two first, then the combines that with the final fragment (two-step approach to making final key)
        if (isPair(a,b,"key fragment a", "key fragment b") || isPair(a, b, "key fragment a", "key fragment c") || isPair(a,b, "key fragment b", "key fragment c")){

            // remove key fragments from inventory
            state.removeItemFromInventory(first);
            state.removeItemFromInventory(second);

            Item partialKey = new Item(
                "Partial Key",
                "two fragments joined together... one more needed.",
                true,
                "/images/partial_key.png",
                0,0,0,0
            );

            // add partial key to inventory
            state.addItemToInventory(partialKey);
            return "the two fragments snap together, forming part of a key...";
        }

        // room 4: partial key + remaining key fragment = liberation key
        if (isPair(a, b, "partial key", "key fragment a") || isPair(a,b, "partial key", "key fragment b") || isPair(a, b, "partial key", "key fragment c")) {

            // remove partial key and remaining fragment from inventory
            state.removeItemFromInventory(first);
            state.removeItemFromInventory(second);

            Item liberationKey = new Item(
                "Liberation Key",
                "the final key that unlocks your escape...",
                true,
                "/images/liberation_key.png",
                0,0,0,0
            );

            // add final key to inventory
            state.addItemToInventory(liberationKey);
            return "the final key fragment clicks into place. the liberation key is now complete. your freedom awaits...";
        }

        // not a valid combination (ex. if player combines correct item a with trick item c)
        state.decreaseTime(30);
        return "oof...these items don't react well together. you've lost 30 seconds.";
    }

    // helper to check if two item names match the correct recipe pair (order doesnt matter)
    private boolean isPair(String a, String b, String x, String y) {
        return (a.equals(x) && b.equals(y)) || (a.equals(y) && b.equals(x));
    }
}
