package ca.uwo.cs2212.group21.model;

import java.util.List;

public class Recipe {
    private List<String> ingredients;
    private String resultName;
    private String message;

    public Recipe(List<String> ingredients, String resultName, String message) {
        this.ingredients = ingredients;
        this.resultName = resultName;
        this.message = message;
    }

    /** 
     * @param item1
     * @param getResultName(
     * @return boolean
     */
    public boolean matches(String item1, String item2) { //making these all in strings for the recipes, instead of items
        String i1 = item1.toLowerCase();
        String i2 = item2.toLowerCase();

        return ingredients.contains(i1) && ingredients.contains(i2) && !i1.equals(i2);
    }

    /** 
     * @return String
     */
    public String getResultName() {
        return resultName;
    }
    
    /** 
     * @return List<String>
     */
    public List<String> getInputs() {
        return this.ingredients;
    }

    /** 
     * @return String
     */
    public String getMessage() {
        return message;
    }
}