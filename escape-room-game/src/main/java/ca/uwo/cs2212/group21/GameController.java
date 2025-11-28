package ca.uwo.cs2212.group21;

import java.io.IOException;

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

    public void initialize() {
        gameScreen.setVisible(false);
        inventory.setVisible(false);
        mainScreen.setVisible(true);
        dialogueOverlay.setVisible(false);

        gameScreen.visibleProperty().addListener((obs,wasVisible,isVisible) -> {
            if (isVisible) {
                gameScreen.requestFocus();
            }
        });

        gameScreen.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case I:
                    toggleInventory();
                    break;
                case ESCAPE:
                    if (inventory.isVisible()) {
                        toggleInventory();
                    }

                    if (dialogueOverlay != null) {
                        dialogueOverlay.setVisible(false);
                        break;
                    }
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

        for (Item item : currentRoom.getItems()) { //this is to go through each item in the room and make and place the image view for the item in the right coordinates, we would put the coordinates in the json file so just make sure

            ImageView itemView = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath()))); //this creates the image of the item


            itemView.setX(item.getX()); //would set the x and y for it to show up in the right spot, we can fiddle around and find where to put it then mark those coords in the json probs
            itemView.setY(item.getY());

            itemView.setFitWidth(item.getWidth()); //this is to set the width and height of the item so we could get it to be a specific size wherever we want it 
            itemView.setFitHeight(item.getHeight());
            itemView.setPreserveRatio(true); //this is to make sure the image doesnt get like warped kinda

            itemView.setOnMouseClicked(e -> {
                System.out.println("Clicked on item: " + item.getName()); //just to test

                interactiveLayer.getChildren().remove(itemView); //this is to remove the item from the screen to see if it works
                //space here for when we have the logic to pick up items and whatnot 
            });

            interactiveLayer.getChildren().add(itemView); //this is to add the item image to the game screen so it shows up
        }
        if (currentRoom.hasNPC()) {//this is to get the npc image and set it as a view then add it to the screen wherever they should go 
            NPC npc = currentRoom.getNPC();

            ImageView npcImage = new ImageView(new Image(getClass().getResourceAsStream(npc.getImagePath()))); 
            npcImage.setX(npc.getX());
            npcImage.setY(npc.getY());
            npcImage.setFitWidth(1);
            npcImage.setFitHeight(1); //we can change this would be a fixed thing anyways unless npc changes sizes for some reason idk
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
            exitHitBox.setFill(Color.TRANSPARENT); //this is to make the rectangle invisible so it doesnt cover up the background image that way its just a hitbox

            exitHitBox.setStroke(Color.RED); //this is just for testing purposes we would remove after we see that the hitbox works fine

            exitHitBox.setOnMouseClicked (e -> {
                System.out.println("Exit clicked: " + currentRoom.getExit(exitDirection).getName()); //this is just for testing to see if the exit was clicked
                //rest of code would go here for the actual calling of the move logic whenever we have that
            });

            interactiveLayer.getChildren().add(exitHitBox);
        }
    }

    private void updateInventoryUI() {
        inventoryGrid.getChildren().clear(); //this is to clear the grid so we dont have duplicates when updating 

        int col = 0;
        int row = 0;

        for (Item item : gameEngine.getPlayer().getInventory()) { //this is to go through each item in the player's inventory and add it to the grid 

            ImageView itemView = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath()))); 

            itemView.setFitWidth(50); //this is to set a fixed size for the inventory items so they fit in the grid size
            itemView.setFitHeight(50);
            itemView.setPreserveRatio(true);

            itemView.setOnMouseClicked(e -> {
                //space for when we have item details or use item logic to trigger on click of the item in inventory 
            });

            inventoryGrid.add(itemView, col, row); //this is to add the item image to the grid at the current column and row 

            col++;
            if (col > 3) { //this is to move to the next row after 3 items in a row 
                col = 0;
                row++;
            }
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
