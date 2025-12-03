package ca.uwo.cs2212.group21;

import java.io.IOException;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.cs2212.group21.commands.PickUpCommand;
import ca.uwo.cs2212.group21.model.GameEngine;
import ca.uwo.cs2212.group21.model.NPC;
import ca.uwo.cs2212.group21.model.Item;
import ca.uwo.cs2212.group21.model.Recipe;
import ca.uwo.cs2212.group21.model.Room;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private ImageView backgroundImageView; // this is the background image which would be what the room is
    @FXML private AnchorPane mainScreen; // this is the title screen
    @FXML private AnchorPane gameScreen; // this is the actual game overlay
    @FXML private AnchorPane inventory; // this is for the inventory overlay
    @FXML private GridPane inventoryGrid; // this is the grid inside the inventory where items would go
    @FXML private AnchorPane interactiveLayer; // this is the layer on top of the background where we would put items, npcs, exits, etc

    private NPC activeNpc;

    @FXML private Label currentRoomLabel;
    @FXML private Label roomDescriptionLabel;
    @FXML private Label turnsTakenLabel;
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

    @FXML private AnchorPane combinePanel;
    @FXML private StackPane combineSlot1;
    @FXML private StackPane combineSlot2;
    @FXML private Button mergeButton;
    @FXML private Button clearButton;
    @FXML private Button combineButton;

    @FXML private ImageView currentNPCImageView;
    @FXML private ImageView playerImageView;

    @FXML private AnchorPane examinePanel;
    @FXML private StackPane examineSlot;
    @FXML private TextArea itemDescriptionBox;
    @FXML private Button examineButton;
    @FXML private Button closeExamineButton;
    @FXML private Button dropButton;

    @FXML private AnchorPane pauseScreen;
    @FXML private Button resumeButton;
    @FXML private Button saveButton;
    @FXML private Button quitToMainButton;

    @FXML private AnchorPane saveGameSlots;
    @FXML private Button saveSlot1;
    @FXML private Button saveSlot2;
    @FXML private Button saveSlot3;

    @FXML private Label pickupPopup;
    private Label endScreenTimeLabel;

    private TranslateTransition currentAnimation;
    private GameEngine gameEngine;
    private Timeline gameTimer;
    // private final Image STAR_FULL = new Image(getClass().getResourceAsStream("/images/star_full.png")); //whenever we get a star image we put it here
    // private final Image STAR_EMPTY = new Image(getClass().getResourceAsStream("/images/star_empty.png")); //whenever we get an empty star image we put it here
    private final int TIME_LIMIT = 600; // 10 minutes but in seconds we can change later if too long

    private Item selected; // to keep track of selected item in inventory

    private boolean isCombineMode = false;
    private boolean isExamineMode = false;
    private boolean isGiveMode = false;

    private List<Item> combineItems = new ArrayList<>();
    private SoundManager soundManager = new SoundManager();
    private double lastPlayerX = 400; // Track last X position to determine direction
    private Label endScreenTimeLabel;

    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    /*
     * Initializes the game controller.
     */
    public void initialize() {
        gameScreen.setVisible(false);
        inventory.setVisible(false);
        mainScreen.setVisible(true);
        dialogueOverlay.setVisible(false);
        examinePanel.setVisible(false);
        pauseScreen.setVisible(false);
        saveGameSlots.setVisible(false);
        
        // Start main menu background music
        soundManager.playBackgroundMusic("mainmenu.mp3");

        mainScreen.sceneProperty().addListener((obs, oldScene, newScene) -> { // this is to add a key listener to the scene whenever it gets set so it can track key inputs
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (gameEngine != null) {
                        switch (event.getCode()) {
                            case I: // toggle inventory
                                toggleInventory();
                                break;

                            case ESCAPE: // if escape is pressed it toggles inventory off if its on
                                soundManager.playEscButtonSound();
                                if (inventory.isVisible()) {
                                    toggleInventory();
                                }

                        else if (saveGameSlots.isVisible()) {
                            saveGameSlots.setVisible(false);
                            gameTimer.play();
                        }
                        else {
                            togglePauseMenu();
                        }
                        }
                    }
                });
            }
        });
        

        interactiveLayer.setOnMouseClicked(event -> {
            
            double targetX = event.getX();
            double targetY = event.getY(); // to get the coordinates to move to

            double centeredX = targetX - (200 / 2); // assuming player image is 200x100 so centering it on click we can change if size diff
            double centeredY = targetY - (100);

            movePlayerVisuals(centeredX, centeredY);
            gameEngine.playerMove(centeredX, centeredY); // update in game state
        });

    }

    /*
     * Starts a new game when the "Start Game" button is clicked.
     * * @param event
     */
    public void startGame(ActionEvent event) throws IOException {
        soundManager.playStartButtonSound();
       // Fade out main screen
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.seconds(1), mainScreen);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            // Start game music immediately to ensure it plays
            soundManager.playBackgroundMusic("spooky_bgm.mp3");

            mainScreen.setVisible(false);

            // Initialize game engine
            gameEngine = new GameEngine("/worldMap.json");
            gameEngine.loadRecipes();
            gameEngine.startNewGame();
            
            // Set time limit AFTER game engine is initialized
            gameEngine.getPlayer().setTimeRemaining(TIME_LIMIT);

            gameScreen.setOpacity(0.0);
            gameScreen.setVisible(true);

            // Setup game state visuals BEFORE fading in
            playerImageView = new ImageView(new Image(getClass().getResourceAsStream(gameEngine.getPlayer().getImagePath())));
            playerImageView.setLayoutX(400);
            playerImageView.setLayoutY(300);
            playerImageView.setFitWidth(200);
            playerImageView.setFitHeight(150);
            playerImageView.setPreserveRatio(true);

            updateScreen();
            updateInventoryUI();
            startTimer();
            

            // Fade in game screen
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.seconds(1),
                    gameScreen);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            fadeIn.play();
        });
        fadeOut.play();
    }


    public void startTimer() {

        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int timeLeft = gameEngine.getPlayer().getTimeRemaining();
            int timeInMinutes = gameEngine.getPlayer().getTimeRemaining() / 60;
            int seconds = timeLeft % 60;
            if (timeLeft > 0) {
                gameEngine.getPlayer().setTimeRemaining(timeLeft - 1);
                timeLabel.setText("Time left: " + timeInMinutes + ":" + seconds + " sec");
                int currentScore = 3;
                if (timeInMinutes < 7) currentScore = 2;
                if (timeInMinutes < 5) currentScore = 1;
                if (timeInMinutes <= 2) currentScore = 0;
                gameEngine.getPlayer().setScore(currentScore);

            } else {
                gameTimer.stop();
                gameEngine.getPlayer().setGameOver(true);
            }

            if (timeLabel != null) {
                timeLabel.setText("Time remaining: " + timeInMinutes + ":" + seconds + " sec");
            }
        }));

        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();

    }

    public void handleGameOver() {
        // would handle game over screen stuff here
        soundManager.stopBackgroundMusic();
        soundManager.playSoundEffect("gameover.mp3");
    }

    public void stopTimer() {
        if (gameTimer != null)
            gameTimer.stop();
    }

    private void togglePauseMenu() {
        if (pauseScreen.isVisible()) {
            pauseScreen.setVisible(false);
            gameTimer.play(); //resume timer
        }
        else {
            pauseScreen.setVisible(true);
            gameTimer.pause(); //pause timer
        }
    }

    public void onResumeClick(Event event) {
        soundManager.playEscButtonSound();
        togglePauseMenu();
    }  

    public void onSaveGameClick(Event event) {
        soundManager.playEscButtonSound();
        pauseScreen.setVisible(false);
        saveGameSlots.setVisible(true);
    }

    public void saveSlot1Click(Event event) {
        gameEngine.saveGame("saveSlot1.json");
        saveGameSlots.setVisible(false);
        gameTimer.play();
    }

    public void saveSlot2Click(Event event) {
        gameEngine.saveGame("saveSlot2.json");
        saveGameSlots.setVisible(false);
        gameTimer.play();
    }

    public void saveSlot3Click(Event event) {
        gameEngine.saveGame("saveSlot3.json");
        saveGameSlots.setVisible(false);
        gameTimer.play();
    }

    public void setGameEngine(GameEngine engine) {
        this.gameEngine = engine;
    }

    public void startGameFromLoad() {
        gameScreen.setVisible(true);

        playerImageView = new ImageView(new Image(getClass().getResourceAsStream(gameEngine.getPlayer().getImagePath())));
        playerImageView.setLayoutX(400);
        playerImageView.setLayoutY(300);
        playerImageView.setFitWidth(200);
        playerImageView.setFitHeight(150);
        playerImageView.setPreserveRatio(true);

        updateScreen();
        updateInventoryUI();
        startTimer();
        soundManager.playBackgroundMusic("spooky_bgm.mp3");
    }


    /*
     * Updates the game screen to reflect the current game state.
     */
    private void updateScreen() {

        interactiveLayer.getChildren().clear();

        Room currentRoom = gameEngine.getPlayer().getCurrentRoom(); // this is to get the current room the player is in from the engine

        
        currentRoomLabel.setText(currentRoom.getName());
        roomDescriptionLabel.setText(currentRoom.getDescription());

        turnsTakenLabel.setText("Moves Taken: " + gameEngine.getPlayer().getMovesCount());
        
        

        backgroundImageView.setImage(new Image(getClass().getResourceAsStream(currentRoom.getImagePath()))); // this is to set the background image to the current room image
        backgroundImageView.setFitWidth(1080); // this is to make sure the background image fits the screen size
        backgroundImageView.setFitHeight(720);
        backgroundImageView.setPreserveRatio(false); // this is to make sure the image fills the whole screen

        for (Item item : currentRoom.getItems()) { // this is to go through each item in the room and make and place the image view for the item in the right coordinates, we would put the coordinates in the json file so just make sure

            ImageView itemView = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath()))); // this creates the image of the item

            itemView.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0, 0, 0);"); // WAS TESTING THIS WOULD CHANGE IT LATER SO WE CAN HOVER

            itemView.setX(item.getX());
            itemView.setY(item.getY());

            itemView.setFitWidth(item.getWidth()); // this is to set the width and height of the item so we could get it to be a specific size wherever we want it
            itemView.setFitHeight(item.getHeight());
            itemView.setPreserveRatio(true); // this is to make sure the image doesnt get like warped kinda

            itemView.setOnMouseClicked(e -> {
                e.consume(); // this is to stop the event from propagating to the layer below so the player doesnt move when clicking an item

                System.out.println("Clicked on item: " + item.getName()); // just to test
                System.out.println("Inventory before adding: " + gameEngine.getPlayer().getInventory().size());

                interactiveLayer.getChildren().remove(itemView); // this is to remove the item from the screen to see if it works

                gameEngine.pickUpItem(item.getName()); // this is to call the pick up command to add the item to the inventory

                showPickupPopup("Picked up: " + item.getName());
                updateInventoryUI();
            });

            interactiveLayer.getChildren().add(itemView); // this is to add the item image to the game screen so it shows up
        }
        if (currentRoom.hasNPC()) {// this is to get the npc image and set it as a view then add it to the screen wherever they should go
            NPC npc = currentRoom.getNPC();

            currentNPCImageView = new ImageView(new Image(getClass().getResourceAsStream(npc.getImagePath())));
            currentNPCImageView.setX(npc.getX());
            currentNPCImageView.setY(npc.getY());
            currentNPCImageView.setFitWidth(150);
            currentNPCImageView.setFitHeight(200); // we can change this would be a fixed thing anyways unless npc changes sizes for some reason idk
            currentNPCImageView.setPreserveRatio(true);
            
            // Set initial opacity to 0 for fade-in effect
            currentNPCImageView.setOpacity(0.0);

            currentNPCImageView.setOnMouseClicked(e -> {
                System.out.println("Clicked on NPC: " + npc.getName());
                e.consume();

                soundManager.playNPCsound();
                handleNPCClick(npc);
            });

            interactiveLayer.getChildren().add(currentNPCImageView);
            
            // Create and play fade-in animation
            javafx.animation.FadeTransition npcFadeIn = new javafx.animation.FadeTransition(Duration.seconds(1.5), currentNPCImageView);
            npcFadeIn.setFromValue(0.0);
            npcFadeIn.setToValue(1.0);
            npcFadeIn.setDelay(Duration.millis(500)); // Slight delay to start after room fade-in begins
            npcFadeIn.play();
        } else {
            this.currentNPCImageView = null; // if we decide to have a room with no npc just to make sure its null
        }

        for (String exitDirection : currentRoom.getExitList()) {

            Rectangle exitHitBox = new Rectangle(currentRoom.getExitX(exitDirection),currentRoom.getExitY(exitDirection), currentRoom.getExitWidth(exitDirection),
            currentRoom.getExitHeight(exitDirection));
            
            exitHitBox.setFill(Color.TRANSPARENT); // this is to make the rectangle invisible so it doesnt cover up the background image that way its just a hitbox

            exitHitBox.setStroke(null); // this is just for testing purposes we would remove after we see that the hitbox works fine

            exitHitBox.setOnMouseClicked(e -> {
                // call the go command through the game engine
                Boolean moved = gameEngine.go(exitDirection);
                soundManager.playSoundEffect("door.mp3");
                if (moved) updateScreenWithTransition();
            });

            interactiveLayer.getChildren().add(exitHitBox);
        }
        interactiveLayer.getChildren().add(playerImageView);
        

    }

    private void handleNPCClick(NPC npc) {

        Room currentRoom = gameEngine.getPlayer().getCurrentRoom();
        String roomName = currentRoom.getName();

        dialogueOverlay.setVisible(true);
        dialogueNameLabel.setText(npc.getName());
        dialogueBox.setWrapText(true);

        org.json.JSONObject allDialogues = gameEngine.getDialogueData();

        if (allDialogues != null && allDialogues.has(npc.getName()) && allDialogues.getJSONObject(npc.getName()).has(roomName)) {
            org.json.JSONObject npcDialogueSpecificRoom = allDialogues.getJSONObject(npc.getName()).getJSONObject(roomName);
            showDialogueNode(npcDialogueSpecificRoom, "root");
        } else {
            dialogueBox.setText("The NPC has nothing to say right now.");
            nextButton.setText("Close");
            nextButton.setVisible(true);
            optionsBox.setVisible(false);
            nextButton.setOnAction(e -> {
                dialogueOverlay.setVisible(false);
                dialogueBox.clear();
            });
        }
    }

    private void showDialogueNode(org.json.JSONObject npcDialogue, String nodeId) {
        if (!npcDialogue.has(nodeId)) return;

        org.json.JSONObject node = npcDialogue.getJSONObject(nodeId);
        String text = node.getString("text");
        org.json.JSONObject options = node.optJSONObject("options");
        String nextNodeID = node.optString("next", null); 

        dialogueOverlay.setVisible(true);
        dialogueNameLabel.setText(npcDialogue.has("name") ? npcDialogue.getString("name") : "The Guide");
        dialogueBox.setText(text);

        optionsBox.getChildren().clear();
        nextButton.setOnAction(null); 

        //separated them into cases for each scenario
        if (options != null && !options.isEmpty()) { //this is the case where there are multiple options to choose from
            optionsBox.setVisible(true);
            nextButton.setVisible(false);

            for (String key : options.keySet()) {
                String targetNode = options.getString(key);
                Button optionButton = new Button(key);
                optionButton.getStyleClass().add("button");
                optionButton.setMaxWidth(Double.MAX_VALUE);
                optionButton.setOnAction(e -> showDialogueNode(npcDialogue, targetNode));
                optionsBox.getChildren().add(optionButton);
            }
        }   
        else if (nextNodeID != null && !nextNodeID.isEmpty()) { //this is the case where there is just a next node to go to
            optionsBox.setVisible(false);
            nextButton.setVisible(true);
            nextButton.setText("Next");
            nextButton.setOnAction(e -> showDialogueNode(npcDialogue, nextNodeID));
        }
        else { //this is the case where its the end of the dialogue so no more options or next
            optionsBox.setVisible(false);
            nextButton.setVisible(true);

            boolean isLockedDoorPresent = false;
            Room currentRoom = gameEngine.getPlayer().getCurrentRoom();

            if (currentRoom.getExits() != null) { //if no dialogue then probably would be for giving an item so would check if any locked doors in room
                for (Room room : currentRoom.getExits().values()) {
                    if (room.isLocked()) {
                        isLockedDoorPresent = true;
                        break;
                    }
                }
            }

            if (isLockedDoorPresent) { //if it finds a locked door then you could select im ready to give the item
                nextButton.setText("I'm ready");
                nextButton.setOnAction(e -> enterGiveMode(currentRoom.getNPC())); 

                
                optionsBox.getChildren().clear();
                optionsBox.setVisible(true);

                Button notReadyButton = new Button("Not ready yet"); //this is the whole not ready yet buttong in case the player clicks the npc but say they need to get another item first or was accidentally clicked
                notReadyButton.getStyleClass().add("button"); //same options button styling
                notReadyButton.setMaxWidth(Double.MAX_VALUE);
                notReadyButton.setOnAction(e -> {
                    isGiveMode = false;
                    inventory.setVisible(false);
                    dialogueOverlay.setVisible(false);
                });

                optionsBox.getChildren().add(notReadyButton);
            } else {
                nextButton.setText("Close"); //if no locked door then would just end dialogue normally
                nextButton.setOnAction(e -> {
                    if (activeNpc != null) {
                        activeNpc.setHasInteracted(true);
                }
                    dialogueOverlay.setVisible(false);
                    dialogueBox.clear();
                });
            }
        }
    }

    private void enterGiveMode(NPC npc) {
        isGiveMode = true;
        
        if (!inventory.isVisible()) {
            toggleInventory();
        }

        dialogueBox.setText("Please select an item from your inventory to give to " + npc.getName() + ".");

        nextButton.setVisible(true);
        nextButton.setText("Give Item"); //this is the set up for the give button when in give mode
        nextButton.setOnAction(e -> handleGiveAttempt(npc));
    }

    private void handleGiveAttempt(NPC npc) {
        if (selected == null) { //this prob wont happen since selected is kinda global and would only be null at like the very start of the game but just in case
            dialogueBox.setText("No item selected. Please select an item from your inventory to give to " + npc.getName() + ".");
            return;
        }

        if (selected != null && selected.isKey()) { //this would be that they gave the right item to unlock the door (key item)
            gameEngine.getPlayer().removeItemFromInventory(selected);
            updateInventoryUI();

            Room currentRoom = gameEngine.getPlayer().getCurrentRoom();
            for (Room rooms : currentRoom.getExits().values()) { //to go through and get the exit that is locked and unlock it since idk if we would have time to do rooms with multiple exits but coded in case
                if (rooms.isLocked()) {
                    rooms.setLocked(false);
                    dialogueBox.setText("Thank you for the " + selected.getName() + "! The " + rooms.getName() + " is now unlocked."); //just made generic text we can change this 
                    isGiveMode = false; //change back to false after 
                    inventory.setVisible(false);
                    break;
                }
            }
            nextButton.setText("Close");
            nextButton.setOnAction(e -> {
                dialogueOverlay.setVisible(false);
                optionsBox.setVisible(false);
                dialogueBox.clear();
            });
        }
        else { //if they give the wrong item then would trigger the penalty here 
            gameEngine.getPlayer().removeItemFromInventory(selected);

            gameEngine.getPlayer().decreaseTime(30);
            triggerJumpScare(); //this is to trigger the npc going to the angry version then back 
            dialogueBox.setText("This is not what I wanted! You lose 30 seconds. Please give me the correct item.");

            Recipe recipe = findRecipeForResult(selected.getName());
            if (recipe != null) { //this is if the item is crafted and not just one found in the room
                for (String ingredientName : recipe.getInputs()) {
                    Item originalPart = gameEngine.getItemList().get(ingredientName);
                    if (originalPart != null) {
                        gameEngine.getPlayer().addItemToInventory(originalPart);
                    }
                }
                updateInventoryUI();
                dialogueBox.setText("This is not what I wanted! You lose 30 seconds. I've returned the original items used to craft " + selected.getName() + ". Please give me the correct item.");
            } else { //otherwise just normal item so would just give back to inventory
                gameEngine.getPlayer().addItemToInventory(selected);
                dialogueBox.setText("This is not what I wanted! You lose 30 seconds. I've returned the " + selected.getName() + " to your inventory. Please give me the correct item.");
                updateInventoryUI();

            }
        }
    }

    private Recipe findRecipeForResult(String resultItemName) {
        if (gameEngine.getPlayer().getRecipes() == null) {
            return null;
        }   

        for (Recipe recipe : gameEngine.getPlayer().getRecipes()) {
            if (recipe.getResultName().equals(resultItemName)) {
                return recipe;
            }
        }
        return null;
    }


    private void showPickupPopup(String message) {
        pickupPopup.setText(message);
        pickupPopup.setVisible(true);
        pickupPopup.setOpacity(1.0);

        // Simple fade out animation
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.seconds(2), pickupPopup);
        ft.setFromValue(1);
        ft.setToValue(0.0);
        ft.setDelay(Duration.seconds(1));
        ft.setOnFinished(e -> pickupPopup.setVisible(false));
        ft.play();
        soundManager.playSoundEffect("pickup.mp3");
    }

    private void triggerJumpScare() {
        Image angryNPC = new Image(getClass().getResourceAsStream("/images/npc_mad.png"));
        Image normalNPC = new Image(getClass().getResourceAsStream("/images/defaultNPC.png"));
        if (angryNPC != null) {
            currentNPCImageView.setImage(angryNPC);
        }
        //leaving space here if you want to add sounds effects or screen shakes or something 

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(0.5)); //this is so the angry npc only shows for half a second then swaps back 
        pause.setOnFinished(e -> {
            currentNPCImageView.setImage(normalNPC);
        });
        pause.play();
    }

    private void updateInventoryUI() {
        inventoryGrid.getChildren().clear(); // this is to clear the grid so we dont have duplicates when updating

        List<Item> items = gameEngine.getPlayer().getInventory();

        int maxSlots = 16;

        for (int i = 0; i < maxSlots; i++) { // this is to add empty slots to the inventory grid so it looks like an inventory even if there are no items
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
                    inventoryGrid.getChildren().forEach(n -> n.setStyle(
                            "-fx-border-color: #555555; -fx-border-width: 1px; -fx-background-color: #dddddd;"));
                    slot.setStyle("-fx-border-color: #FFD700; -fx-border-width: 2px; -fx-background-color: #ffffaa;"); // this is to highlight the selected item in gold could change if we want
                    System.out.println("Slot clicked: col " + col + ", row " + row);
                    selected = item;

                    // Give logic: only when we are in give mode and dialogue is open
                    if (isGiveMode && gameEngine.getPlayer().getCurrentRoom().hasNPC()) {

                        // Use GameEngine wrapper that calls GiveCommand with current NPC and this item
                        String result = gameEngine.giveItemToCurrentNpc(selected.getName());
                        System.out.println(result);

                        // Show result in the dialogue box
                        dialogueBox.setText(result);

                        // Inventory may have changed: refresh it
                        updateInventoryUI();
                    }

                    if (isCombineMode) {
                    handleCombineSelecting(selected);
                    }

                    if (isExamineMode) {
                    refreshExaminePanel();
                    }

                
            });
            slot.getChildren().add(icon);
        }
            inventoryGrid.add(slot, col, row); 
           
        }
    }

    public void toggleInventory() {
        System.out.println("toggleInventory called. Before: " + inventory.isVisible());

        if (inventory.isVisible()) {
            // Closing inventory
            inventory.setVisible(false);
            gameScreen.requestFocus();

            // If we were in give mode and player closed inventory with I,
            // exit give mode and make Next close the dialogue instead of reopening inventory.
        if (isGiveMode) {
            isGiveMode = false;
            nextButton.setOnAction(e -> {
                dialogueOverlay.setVisible(false);
                dialogueBox.clear();
            });
        }
        } else {
            // Opening inventory (normal gameplay)
            updateInventoryUI();
            inventory.setVisible(true);
            inventory.requestFocus();
        }

        System.out.println("After: " + inventory.isVisible());
    }


    public void dropItemFromInventory(Event event) {
        soundManager.playDropButtonSound();
        gameEngine.dropItem(selected.getName());
        updateInventoryUI();
        updateScreen();
        System.out.println("Inventory after dropping: " + gameEngine.getPlayer().getInventory());
    }

    public void onCombineModeClick(Event event) {
        if (isCombineMode) { // if already in combine mode then exit it
            exitCombineMode();
            examineButton.setDisable(false); //reenable examine button when exiting combine mode
            dropButton.setDisable(false); //reenable drop button when exiting combine mode
        } else {
            isCombineMode = true; // otherwise would turn it on then show the combine panel and update the slots
            combinePanel.setVisible(true);
            combineItems.clear();
            updateCombineSlots();
            System.out.println("Combine Mode Activated");
            examineButton.setDisable(true); //disable examine button while in combine mode
            dropButton.setDisable(true); //disable drop button while in combine mode
        }

    }

    private void exitCombineMode() { // this is to exit and reset combine mode stuff
        isCombineMode = false;
        combinePanel.setVisible(false);
        combineItems.clear();
        updateCombineSlots();
        updateInventoryUI();
    }

    public void handleCombineSelecting(Item item) { // if the item is already in the combine list then remove it otherwise would add if there is space
        if (combineItems.contains(item)) {
            combineItems.remove(item);
            gameEngine.getPlayer().addItemToInventory(item); // add back to inventory when removed from combine list
            updateInventoryUI();
        } else if (combineItems.size() < 2) {
            combineItems.add(item);
            gameEngine.getPlayer().removeItemFromInventory(item); // remove from inventory when added to combine list
            updateInventoryUI();
        }
        updateCombineSlots();
    }

    public void updateCombineSlots() {
        combineSlot1.getChildren().clear();
        combineSlot2.getChildren().clear();

        if (combineItems.size() > 0) {
            Item item1 = combineItems.get(0);
            ImageView icon1 = new ImageView(new Image(getClass().getResourceAsStream(item1.getImagePath())));
            icon1.setFitWidth(50);
            icon1.setFitHeight(50);
            icon1.setPreserveRatio(true);
            combineSlot1.getChildren().add(icon1);
            combineSlot1.setOnMouseClicked(e -> handleCombineSelecting(item1));
        }

        if (combineItems.size() > 1) {
            Item item2 = combineItems.get(1);
            ImageView icon2 = new ImageView(new Image(getClass().getResourceAsStream(item2.getImagePath())));
            icon2.setFitWidth(50);
            icon2.setFitHeight(50);
            icon2.setPreserveRatio(true);
            combineSlot2.getChildren().add(icon2);
            combineSlot2.setOnMouseClicked(e -> handleCombineSelecting(item2));
        }

        mergeButton.setDisable(combineItems.size() < 2); // only can merge if there are 2 items
    }

    public void onMergeButtonClick(Event event) {
        if (combineItems.size() == 2) {
            String result = gameEngine.useItem(combineItems.get(0), combineItems.get(1));
            System.out.println(result); // this is to show the result of the combination attempt
            // exitCombineMode();
            combineItems.clear();
            updateCombineSlots();
            updateInventoryUI();
            
        }
    }

    public void onClearButtonClick(Event event) {
        if (combineItems.isEmpty()) {
            return; // nothing to clear
        } else {
            if (combineItems.size() == 1) {
                if (gameEngine.getPlayer().getInventory().contains(combineItems.get(0))) { // If item already in inventory, just remove from combine list
                    combineItems.remove(0);
                } else {
                    gameEngine.getPlayer().addItemToInventory(combineItems.get(0)); // otherwise add it back to inventory so they dont just go poof to the void
                    combineItems.remove(0);
                }
            } else if (combineItems.size() == 2) {
                if (gameEngine.getPlayer().getInventory().contains(combineItems.get(0))) {
                    combineItems.remove(0);
                } else {
                    gameEngine.getPlayer().addItemToInventory(combineItems.get(0));
                    combineItems.remove(0);
                }

                if (gameEngine.getPlayer().getInventory().contains(combineItems.get(0))) {
                    combineItems.remove(0);
                } else {
                    gameEngine.getPlayer().addItemToInventory(combineItems.get(0));
                    combineItems.remove(0);
                }
            }
        }
        combineItems.clear();
        updateCombineSlots();
        updateInventoryUI();
    }

    public void movePlayerVisuals(double x, double y) {

        double targetX = x;
        double targetY = y;

        double playerWidth = 150;
        double playerHeight = 200;

        double screenWidth = interactiveLayer.getWidth();
        double screenHeight = interactiveLayer.getHeight();

        double minX = 0;
        double minY = 350;
        double maxX = screenWidth - playerWidth;
        double maxY = 550;

        double finalX = Math.max(minX, Math.min(x, maxX)); // this is to clamp the the position so they dont walk off screen
        double finalY = Math.max(minY, Math.min(y, maxY));

        if (currentAnimation != null) { // this is so if you click queue a bunch of times it just stops the current anim to start another one
            currentAnimation.stop();
            soundManager.stopFootsteps(); // Stop previous footsteps
        }

        double currentVisualX = playerImageView.getLayoutX() + playerImageView.getTranslateX(); // this is to get the current position since the translate wouldnt store like intermediate positions
        double currentVisualY = playerImageView.getLayoutY() + playerImageView.getTranslateY();

        playerImageView.setLayoutX(currentVisualX); // reset layout to current position
        playerImageView.setLayoutY(currentVisualY);
        playerImageView.setTranslateX(0); // reset translate to 0
        playerImageView.setTranslateY(0);
        playerImageView.setX(0);
        playerImageView.setY(0);

        double difX = finalX - currentVisualX; // this is to get the difference between current position and target so it can animate to that position
        double difY = finalY - currentVisualY;


        currentAnimation = new TranslateTransition(Duration.seconds(1), playerImageView);
        currentAnimation.setToX(difX);
        currentAnimation.setToY(difY);

        // Flip player sprite based on horizontal movement direction
        double currentX = playerImageView.getLayoutX();
        if (finalX < currentX) {
            // Moving left - flip sprite horizontally
            playerImageView.setScaleX(-1);
        } else if (finalX > currentX) {
            // Moving right - normal orientation
            playerImageView.setScaleX(1);
        }
        // Update last position for next movement
        lastPlayerX = finalX;


        // Start footsteps when animation begins
        soundManager.startFootsteps();

        currentAnimation.setOnFinished(e -> { // this is to set the final position properly and then reset translate so no weird stuff happens
            playerImageView.setLayoutX(finalX);
            playerImageView.setLayoutY(finalY);
            playerImageView.setTranslateX(0);
            playerImageView.setTranslateY(0);
            // Stop footsteps when animation ends
            soundManager.stopFootsteps();
        });

        currentAnimation.play();
    }


    public void onExamineClick(Event event) {
        examineSlot.getChildren().clear();

        if (isExamineMode) { // if already in examine mode then exit it
            exitExamineMode();
        } else {
            enterExamineMode();
        }
    }

    public void enterExamineMode() {
        isExamineMode = true;
        examinePanel.setVisible(true);
        refreshExaminePanel();
        combineButton.setDisable(true);
    }

    public void exitExamineMode() {
        isExamineMode = false;
        examinePanel.setVisible(false);
        combineButton.setDisable(false);
    }

    public void refreshExaminePanel() {
        examineSlot.getChildren().clear();

        if (selected != null) {
            ImageView examineIcon = new ImageView(new Image(getClass().getResourceAsStream(selected.getImagePath())));
            examineIcon.setFitWidth(50);
            examineIcon.setFitHeight(50);
            examineIcon.setPreserveRatio(true);
            examineSlot.getChildren().add(examineIcon);

            itemDescriptionBox.setText(selected.getDescription());
            itemDescriptionBox.setWrapText(true);
        }
    }

    public void onCloseExamineClick(Event event) {
        soundManager.playEscButtonSound();
        exitExamineMode();
    }

    public void switchToMainScene(Event event) throws IOException {
        soundManager.stopBackgroundMusic();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/gameView.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToStartScene(Event event) throws IOException {
        mainScreen.setVisible(false);
        gameScreen.setVisible(true);
    }

    public void switchToLoadGameScene(Event event) throws IOException {
        soundManager.playLoadGameSound();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/loadGame.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void quitGame(Event event) {
        soundManager.playQuitButtonSound();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Updates the screen with a fade out/fade in transition effect using a black overlay.
     * This creates a smooth visual transition when changing rooms.
     * Also checks for win condition when entering the final room.
     */
    private void updateScreenWithTransition() {
        // Create a black rectangle overlay for the fade effect
        Rectangle blackOverlay = new Rectangle(1080, 720);
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setOpacity(0.0);
        
        // Add overlay to the interactive layer (on top of everything)
        interactiveLayer.getChildren().add(blackOverlay);
        
        // Fade to black
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.millis(400), blackOverlay);
        fadeOut.setFromValue(0.0);
        fadeOut.setToValue(1.0);
        
        fadeOut.setOnFinished(e -> {
            // Update the room content while screen is black
            updateScreen();
            
            // Check if player reached the final room (win condition)
            if (gameEngine.getPlayer().getCurrentRoom().getName().equals("Game Completed")) {
                soundManager.stopBackgroundMusic();
                soundManager.playSuccessSequence(); // Play success.mp3 then success2.mp3
                if (gameTimer != null) {
                    gameTimer.stop();
                }

                int timeLeft  = gameEngine.getPlayer().getTimeRemaining();
                int timeTaken = TIME_LIMIT - timeLeft;   // how long they actually took
                String pretty = formatTime(timeTaken);

                // Update the HUD text at the top (optional)
                timeLabel.setText("Time taken: " + pretty);
                
            
                turnsTakenLabel.setText("Moves Taken: " + gameEngine.getPlayer().getMovesCount());

                // ---------- label beside the ghost on the end screen ----------
                if (endScreenTimeLabel == null) {
                    endScreenTimeLabel = new Label("      " + pretty);
                    endScreenTimeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

                    // so numbers fit next to ghost
                    double ghostX = currentNPCImageView.getX();
                    double ghostY = currentNPCImageView.getY();

                    endScreenTimeLabel.setLayoutX(ghostX - 260);  
                    endScreenTimeLabel.setLayoutY(ghostY + 40); 

                    interactiveLayer.getChildren().add(endScreenTimeLabel);
                } else {
                    endScreenTimeLabel.setText("Time Completed: " + pretty);

                    double ghostX = currentNPCImageView.getX();
                    double ghostY = currentNPCImageView.getY();
                    endScreenTimeLabel.setLayoutX(ghostX - 5);
                    endScreenTimeLabel.setLayoutY(ghostY + 40);

                    if (!interactiveLayer.getChildren().contains(endScreenTimeLabel)) {
                        interactiveLayer.getChildren().add(endScreenTimeLabel);
                    }
                }
            }

            
            // Fade from black
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.millis(400), blackOverlay);
            fadeIn.setFromValue(1.0);
            fadeIn.setToValue(0.0);
            fadeIn.setOnFinished(e2 -> {
                // Remove the overlay after fade in completes
                interactiveLayer.getChildren().remove(blackOverlay);
            });
            fadeIn.play();
        });
        
        fadeOut.play();
    }

}