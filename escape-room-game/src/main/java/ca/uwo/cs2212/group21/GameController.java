package ca.uwo.cs2212.group21;

import java.io.IOException;
import java.util.List;

import ca.uwo.cs2212.group21.commands.PickUpCommand;
import ca.uwo.cs2212.group21.model.GameEngine;
import ca.uwo.cs2212.group21.model.NPC;
import ca.uwo.cs2212.group21.model.Item;
import ca.uwo.cs2212.group21.model.Room;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameController {

    
    private Stage stage;
    private Scene scene;
    private Parent root; 

    @FXML private ImageView backgroundImageView; //this is the background image which would be what the room is 
    @FXML private AnchorPane mainScreen; //this is the title screen
    @FXML private AnchorPane gameScreen; //this is the actual game overlay
    @FXML private AnchorPane inventory; //this is for the inventory overlay
    @FXML private GridPane inventoryGrid; //this is the grid inside the inventory where items would go
    @FXML private AnchorPane interactiveLayer; //this is the layer on top of the background where we would put items, npcs, exits, etc

    @FXML private Label currentRoomLabel;
    @FXML private Label timeLabel;
    @FXML private Label scoreLabel;
    @FXML private ImageView star1;
    @FXML private ImageView star2;
    @FXML private ImageView star3;

    @FXML private AnchorPane dialogueOverlay;
    @FXML private Label dialogueNameLabel;  
    @FXML private TextArea dialogueBox;     
    @FXML private Button nextButton;        
    @FXML private VBox optionsBox;
    

    private GameEngine gameEngine;
    private ImageView currentNPCImageView;

    private Timeline gameTimer;    
    //private final Image STAR_FULL = new Image(getClass().getResourceAsStream("/images/star_full.png")); //whenever we get a star image we put it here
    //private final Image STAR_EMPTY = new Image(getClass().getResourceAsStream("/images/star_empty.png")); //whenever we get an empty star image we put it here

    private Item selected; //to keep track of selected item in inventory

    public void initialize() {
        gameScreen.setVisible(false);
        inventory.setVisible(false);
        mainScreen.setVisible(true);
        dialogueOverlay.setVisible(false);

        mainScreen.sceneProperty().addListener((obs,oldScene,newScene) -> { //this is to add a key listener to the scene whenever it gets set so it can track key inputs
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if(gameEngine != null) {
                        switch (event.getCode()) {
                    case I: //if I gets pressed it toggles inventory (opens or closes)
                        toggleInventory();
                        break;
                    case ESCAPE: //if escape is pressed it toggles inventory off if its on 
                        if (inventory.isVisible()) {
                            toggleInventory();
                        }

                        if (dialogueOverlay != null && dialogueOverlay.isVisible()) { //this is to make sure the dialogue overlay is invisible if it has no dialogue
                            dialogueOverlay.setVisible(false);
                            break;
                        
                    }   
                    }
                }
            });
            }
        });
    }

            
    public void startGame(ActionEvent event) throws IOException {
        gameEngine = new GameEngine("/worldMap.json"); //this is to start up a new game since doing the anchor pane method so would just set visible or not 
        gameEngine.startNewGame();

        mainScreen.setVisible(false);
        gameScreen.setVisible(true);

        updateScreen();
        updateInventoryUI();
    }

    private void updateScreen() {
        
        interactiveLayer.getChildren().clear();

        Room currentRoom = gameEngine.getPlayer().getCurrentRoom(); //this is to get the current room the player is in from the engine 

        backgroundImageView.setImage(new Image(getClass().getResourceAsStream(currentRoom.getImagePath()))); //this is to set the background image to the current room image
        backgroundImageView.setFitWidth(1080); //this is to make sure the background image fits the screen size
        backgroundImageView.setFitHeight(720);
        backgroundImageView.setPreserveRatio(false); //this is to make sure the image fills the whole screen

        for (Item item : currentRoom.getItems()) { //this is to go through each item in the room and make and place the image view for the item in the right coordinates, we would put the coordinates in the json file so just make sure

            ImageView itemView = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath()))); //this creates the image of the item

            itemView.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0, 0, 0);"); //WAS TESTING THIS WOULD CHANGE IT LATER SO WE CAN HOVER

            itemView.setX(item.getX()); 
            itemView.setY(item.getY());

            itemView.setFitWidth(item.getWidth()); //this is to set the width and height of the item so we could get it to be a specific size wherever we want it 
            itemView.setFitHeight(item.getHeight());
            itemView.setPreserveRatio(true); //this is to make sure the image doesnt get like warped kinda

            itemView.setOnMouseClicked(e -> {
                System.out.println("Clicked on item: " + item.getName()); //just to test
                System.out.println("Inventory before adding: " + gameEngine.getPlayer().getInventory().size());
                
                interactiveLayer.getChildren().remove(itemView); //this is to remove the item from the screen to see if it works

                gameEngine.pickUpItem(item.getName()); //this is to call the pick up command to add the item to the inventory 
            });

            interactiveLayer.getChildren().add(itemView); //this is to add the item image to the game screen so it shows up
        }
        if (currentRoom.hasNPC()) {//this is to get the npc image and set it as a view then add it to the screen wherever they should go 
            NPC npc = currentRoom.getNPC();

            ImageView npcImage = new ImageView(new Image(getClass().getResourceAsStream(npc.getImagePath()))); 
            npcImage.setX(npc.getX());
            npcImage.setY(npc.getY());
            npcImage.setFitWidth(150);
            npcImage.setFitHeight(200); //we can change this would be a fixed thing anyways unless npc changes sizes for some reason idk
            npcImage.setPreserveRatio(true);

            //space for when we get dialogue and interaction logic so can trigger on mouse click to start convo or give item 

            interactiveLayer.getChildren().add(npcImage);
            this.currentNPCImageView = npcImage;
        }
        else {
            this.currentNPCImageView = null; //if we decide to have a room with no npc just to make sure its null 
        }

        for (String exitDirection : currentRoom.getExitList()) {

            Rectangle exitHitBox = new Rectangle(currentRoom.getExitX(exitDirection), currentRoom.getExitY(exitDirection), currentRoom.getExitWidth(exitDirection), currentRoom.getExitHeight(exitDirection));
            exitHitBox.setFill(Color.WHITE); //this is to make the rectangle invisible so it doesnt cover up the background image that way its just a hitbox

            exitHitBox.setStroke(Color.RED); //this is just for testing purposes we would remove after we see that the hitbox works fine

            exitHitBox.setOnMouseClicked (e -> {
                System.out.println("Exit clicked: " + currentRoom.getExit(exitDirection).getName()); //this is just for testing to see if the exit was clicked
                //gameEngine.moveToRoom(exitDirection); //this is to move to the room in that direction when clicked
                //updateScreen();
            });

            interactiveLayer.getChildren().add(exitHitBox);
        }
    }

    private void updateInventoryUI() {
        inventoryGrid.getChildren().clear(); //this is to clear the grid so we dont have duplicates when updating 

        List<Item> items = gameEngine.getPlayer().getInventory();

        int maxSlots = 16;

        for (int i = 0; i < maxSlots; i++) { //this is to add empty slots to the inventory grid so it looks like an inventory even if there are no items 
            int col = i % 4;
            int row = i / 4;

            javafx.scene.layout.StackPane slot = new javafx.scene.layout.StackPane();
            slot.setPrefSize(50, 50);

            slot.setStyle("-fx-border-color: #555555; -fx-border-width: 1px; -fx-background-color: #dddddd;");

            if (i < items.size()) {
                Item item = items.get(i);

                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                icon.setFitWidth(40);
                icon.setFitHeight(40);
                icon.setPreserveRatio(true);
                slot.setOnMouseClicked(e -> {
                System.out.println("Selected: " + item.getName());
                inventoryGrid.getChildren().forEach(n -> n.setStyle("-fx-border-color: #555555; -fx-border-width: 1px; -fx-background-color: #dddddd;"));
                slot.setStyle("-fx-border-color: #FFD700; -fx-border-width: 2px; -fx-background-color: #ffffaa;"); //this is to highlight the selected item in gold could change if we want 
                System.out.println("Slot clicked: col " + col + ", row " + row); 
                selected = item;

            });
            slot.getChildren().add(icon);
        }
            inventoryGrid.add(slot, col, row); 
           
        }
    }

    public void toggleInventory() {
        if (inventory.isVisible()) {
            inventory.setVisible(false);
            gameScreen.requestFocus(); //this is to put the focus back to the game screen so can click again 
        } else {
            updateInventoryUI(); //refresh the grid before it opens
            inventory.setVisible(true);
            inventory.requestFocus(); //this is to put the focus on the inventory so can click items in it
        }
    }
    
    public void dropItemFromInventory(Event event) {
        gameEngine.dropItem(selected.getName());
        updateInventoryUI();
        updateScreen();
        System.out.println("Inventory after dropping: " + gameEngine.getPlayer().getInventory());
    }

    public void switchToMainScene(Event event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/gameView.fxml")); 
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    
    public void switchToStartScene(Event event) throws IOException {
        mainScreen.setVisible(false);
        gameScreen.setVisible(true);
    }

    public void switchToLoadGameScene(Event event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/loadGame.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void quitGame(Event event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


}
