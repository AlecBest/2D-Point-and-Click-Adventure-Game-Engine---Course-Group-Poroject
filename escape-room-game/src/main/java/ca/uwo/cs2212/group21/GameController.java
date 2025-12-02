package ca.uwo.cs2212.group21;

import java.io.IOException;
import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.List;

import ca.uwo.cs2212.group21.commands.PickUpCommand;
import ca.uwo.cs2212.group21.model.GameEngine;
import ca.uwo.cs2212.group21.model.NPC;
import ca.uwo.cs2212.group21.model.Item;
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

    @FXML
    private ImageView backgroundImageView; // this is the background image which would be what the room is
    @FXML
    private AnchorPane mainScreen; // this is the title screen
    @FXML
    private AnchorPane gameScreen; // this is the actual game overlay
    @FXML
    private AnchorPane inventory; // this is for the inventory overlay
    @FXML
    private GridPane inventoryGrid; // this is the grid inside the inventory where items would go
    @FXML
    private AnchorPane interactiveLayer; // this is the layer on top of the background where we would put items, npcs,
                                         // exits, etc

    @FXML
    private Label currentRoomLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private ImageView star1;
    @FXML
    private ImageView star2;
    @FXML
    private ImageView star3;

    @FXML
    private AnchorPane dialogueOverlay;
    @FXML
    private Label dialogueNameLabel;
    @FXML
    private TextArea dialogueBox;
    @FXML
    private Button nextButton;
    @FXML
    private VBox optionsBox;

    @FXML
    private AnchorPane combinePanel;
    @FXML
    private StackPane combineSlot1;
    @FXML
    private StackPane combineSlot2;
    @FXML
    private Button mergeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button combineButton;

    @FXML
    private ImageView currentNPCImageView;
    @FXML
    private ImageView playerImageView;

    @FXML
    private AnchorPane examinePanel;
    @FXML
    private StackPane examineSlot;
    @FXML
    private TextArea itemDescriptionBox;
    @FXML
    private Button examineButton;
    @FXML
    private Button closeExamineButton;
    @FXML
    private Button dropButton;

    @FXML
    private AnchorPane pauseScreen;
    @FXML
    private Button resumeButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button quitToMainButton;

    @FXML
    private AnchorPane saveGameSlots;
    @FXML
    private Button saveSlot1;
    @FXML
    private Button saveSlot2;
    @FXML
    private Button saveSlot3;

    private TranslateTransition currentAnimation;
    private GameEngine gameEngine;
    private Timeline gameTimer;
    // private final Image STAR_FULL = new
    // Image(getClass().getResourceAsStream("/images/star_full.png")); //whenever we
    // get a star image we put it here
    // private final Image STAR_EMPTY = new
    // Image(getClass().getResourceAsStream("/images/star_empty.png")); //whenever
    // we get an empty star image we put it here
    private final int TIME_LIMIT = 600; // 10 minutes but in seconds we can change later if too long

    private Item selected; // to keep track of selected item in inventory

    private boolean isCombineMode = false;
    private boolean isExamineMode = false;
    private boolean isGiveMode = false;

    private List<Item> combineItems = new ArrayList<>();
    private SoundManager soundManager = new SoundManager();
    private org.json.JSONObject dialogueData;

    @FXML
    private Label pickupPopup;

    /*
     * Initializes the game controller.
     */
    public void initialize() {
        gameScreen.setVisible(false);
        inventory.setVisible(false);
        inventory.setPickOnBounds(false); // Allow clicks to pass through transparent parts
        mainScreen.setVisible(true);
        dialogueOverlay.setVisible(false);
        examinePanel.setVisible(false);
        pauseScreen.setVisible(false);
        saveGameSlots.setVisible(false);

        mainScreen.sceneProperty().addListener((obs, oldScene, newScene) -> { // this is to add a key listener to the
                                                                              // scene whenever it gets set so it can
                                                                              // track key inputs
            if (newScene != null) {
                newScene.setOnKeyPressed(event -> {
                    if (gameEngine != null) {
                        switch (event.getCode()) {
                            case I: // toggle inventory
                                toggleInventory();
                                break;

                            case ESCAPE: // if escape is pressed it toggles inventory off if its on
                                if (inventory.isVisible()) {
                                    toggleInventory();
                                }

                                else if (saveGameSlots.isVisible()) {
                                    saveGameSlots.setVisible(false);
                                    gameTimer.play();
                                } else {
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

            double centeredX = targetX - (200 / 2); // assuming player image is 200x100 so centering it on click we can
                                                    // change if size diff
            double centeredY = targetY;
            movePlayerVisuals(centeredX, centeredY);
            gameEngine.playerMove(centeredX, centeredY); // update in game state
            soundManager.playSoundEffect("footsteps.mp3");
        });

        loadDialogues();
    }

    private void loadDialogues() {
        try (java.io.InputStream is = getClass().getResourceAsStream("/dialogues.json")) {
            if (is != null) {
                String jsonText = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                dialogueData = new org.json.JSONObject(jsonText).getJSONObject("dialogues");
            } else {
                System.err.println("Could not find dialogues.json");
            }
        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * Starts a new game when the "Start Game" button is clicked.
     * * @param event
     */
    public void startGame(ActionEvent event) throws IOException {
        // Fade out main screen
        javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(Duration.seconds(1), mainScreen);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            mainScreen.setVisible(false);

            // Initialize game engine
            gameEngine = new GameEngine("/worldMap.json");
            gameEngine.startNewGame();

            gameScreen.setOpacity(0.0);
            gameScreen.setVisible(true);

            // Setup game state visuals BEFORE fading in
            playerImageView = new ImageView(
                    new Image(getClass().getResourceAsStream(gameEngine.getPlayer().getImagePath())));
            playerImageView.setLayoutX(400);
            playerImageView.setLayoutY(300);
            playerImageView.setFitWidth(200);
            playerImageView.setFitHeight(150);
            playerImageView.setPreserveRatio(true);

            updateScreen();
            updateInventoryUI();
            startTimer();
            soundManager.playBackgroundMusic("spooky_bgm.mp3");

            // Fade in game screen
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(Duration.seconds(1),
                    gameScreen);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            // Set NPC opacity to 0 initially so it doesn't show during screen fade
            if (currentNPCImageView != null) {
                currentNPCImageView.setOpacity(0.0);
            }

            fadeIn.setOnFinished(e2 -> {
                // Fade in NPC if present
                if (currentNPCImageView != null) {
                    javafx.animation.FadeTransition npcFade = new javafx.animation.FadeTransition(Duration.seconds(2),
                            currentNPCImageView);
                    npcFade.setFromValue(0.0);
                    npcFade.setToValue(1.0);
                    npcFade.play();
                }
            });
            fadeIn.play();
        });
        fadeOut.play();
    }

    public void startTimer() {
        gameEngine.getPlayer().setTimeRemaining(TIME_LIMIT);

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
            } else {
                gameTimer.stop();
                gameEngine.getPlayer().setGameOver(true);
            }

            if (timeLabel != null) {
                timeLabel.setText("Time: " + timeInMinutes + ":" + seconds + " sec");
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
            gameTimer.play(); // resume timer
        } else {
            pauseScreen.setVisible(true);
            gameTimer.pause(); // pause timer
        }
    }

    public void onResumeClick(Event event) {
        togglePauseMenu();
    }

    public void onSaveGameClick(Event event) {
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

    /*
     * Updates the game screen to reflect the current game state.
     */
    private void updateScreen() {

        interactiveLayer.getChildren().clear();

        Room currentRoom = gameEngine.getPlayer().getCurrentRoom(); // this is to get the current room the player is in
                                                                    // from the engine

        backgroundImageView.setImage(new Image(getClass().getResourceAsStream(currentRoom.getImagePath()))); // this is
                                                                                                             // to set
                                                                                                             // the
                                                                                                             // background
                                                                                                             // image to
                                                                                                             // the
                                                                                                             // current
                                                                                                             // room
                                                                                                             // image
        backgroundImageView.setFitWidth(1080); // this is to make sure the background image fits the screen size
        backgroundImageView.setFitHeight(720);
        backgroundImageView.setPreserveRatio(false); // this is to make sure the image fills the whole screen

        for (Item item : currentRoom.getItems()) { // this is to go through each item in the room and make and place the
                                                   // image view for the item in the right coordinates, we would put the
                                                   // coordinates in the json file so just make sure

            ImageView itemView = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath()))); // this
                                                                                                                // creates
                                                                                                                // the
                                                                                                                // image
                                                                                                                // of
                                                                                                                // the
                                                                                                                // item

            itemView.getStyleClass().add("item-glow"); // WAS TESTING THIS WOULD CHANGE IT LATER SO WE CAN HOVER

            itemView.setX(item.getX());
            itemView.setY(item.getY());

            itemView.setFitWidth(item.getWidth()); // this is to set the width and height of the item so we could get it
                                                   // to be a specific size wherever we want it
            itemView.setFitHeight(item.getHeight());
            itemView.setPreserveRatio(true); // this is to make sure the image doesnt get like warped kinda

            itemView.setOnMouseClicked(e -> {
                e.consume(); // this is to stop the event from propagating to the layer below so the player
                             // doesnt move when clicking an item

                System.out.println("Clicked on item: " + item.getName()); // just to test
                System.out.println("Inventory before adding: " + gameEngine.getPlayer().getInventory().size());

                interactiveLayer.getChildren().remove(itemView); // this is to remove the item from the screen to see if
                                                                 // it works

                gameEngine.pickUpItem(item.getName()); // this is to call the pick up command to add the item to the
                                                       // inventory
                showPickupPopup("Picked up: " + item.getName());
                updateInventoryUI();
            });

            interactiveLayer.getChildren().add(itemView); // this is to add the item image to the game screen so it
                                                          // shows up
        }
        if (currentRoom.hasNPC()) {// this is to get the npc image and set it as a view then add it to the screen
                                   // wherever they should go
            NPC npc = currentRoom.getNPC();

            currentNPCImageView = new ImageView(new Image(getClass().getResourceAsStream(npc.getImagePath())));
            currentNPCImageView.setX(npc.getX());
            currentNPCImageView.setY(npc.getY());
            currentNPCImageView.setFitWidth(150);
            currentNPCImageView.setFitHeight(200); // we can change this would be a fixed thing anyways unless npc
                                                   // changes sizes for some reason idk
            currentNPCImageView.setPreserveRatio(true);

            currentNPCImageView.setOnMouseClicked(e -> {
                System.out.println("Clicked on NPC: " + npc.getName());
                e.consume();
                // handleNPCClick(npc);
                // would open dialogue box and start npc interaction here

                handleNPCClick(npc);
            });

            interactiveLayer.getChildren().add(currentNPCImageView);
        } else {
            this.currentNPCImageView = null; // if we decide to have a room with no npc just to make sure its null
        }

        for (String exitDirection : currentRoom.getExitList()) {

            Rectangle exitHitBox = new Rectangle(currentRoom.getExitX(exitDirection),
                    currentRoom.getExitY(exitDirection), currentRoom.getExitWidth(exitDirection),
                    currentRoom.getExitHeight(exitDirection));
            exitHitBox.setFill(Color.WHITE); // this is to make the rectangle invisible so it doesnt cover up the
                                             // background image that way its just a hitbox

            exitHitBox.setStroke(Color.RED); // this is just for testing purposes we would remove after we see that the
                                             // hitbox works fine

            exitHitBox.setOnMouseClicked(e -> {
                // call the go command through the game engine
                String result = gameEngine.go(exitDirection);

                // print the message for now
                System.out.println(result);
                soundManager.playSoundEffect("footsteps.mp3");

                // refresh the screen if the room changed
                updateScreen();
                updateInventoryUI();
            });

            interactiveLayer.getChildren().add(exitHitBox);
        }
        interactiveLayer.getChildren().add(playerImageView);

    }

private void handleNPCClick(NPC npc) {

    // Find which room we are currently in
    Room currentRoom = gameEngine.getPlayer().getCurrentRoom();

    // Always show overlay and NPC name when clicked
    dialogueOverlay.setVisible(true);
    dialogueNameLabel.setText(npc.getName());

    // 1: Intro room: talk only, no give
    if ("Main Room".equals(currentRoom.getName())) {
        // Use the per room dialogue text from JSON
        String introText = npc.getDialogue();
        dialogueBox.setText(introText);
        dialogueBox.setWrapText(true);

        // In the intro room, Next just closes the dialogue
        isGiveMode = false;
        nextButton.setVisible(true);
        optionsBox.setVisible(false);

        nextButton.setOnAction(e -> {
            dialogueOverlay.setVisible(false);
            dialogueBox.clear();
        });

        return;  // stop here; do not go into the give logic below
    }

    // Try JSON dialogue tree first
    if (dialogueData != null && dialogueData.has(npc.getName())) {
        org.json.JSONObject npcDialogue = dialogueData.getJSONObject(npc.getName());
        showDialogueNode(npcDialogue, "root");
    } else {
        // Fallback: simple TalkCommand text
        String dialogueText = gameEngine.talkToNpc();
        dialogueBox.setText(dialogueText);
        dialogueBox.setWrapText(true);
        nextButton.setVisible(true);
        optionsBox.setVisible(false);
    }

    // Set up give phase to start when Next is clicked
    isGiveMode = false;
    nextButton.setVisible(true);
    optionsBox.setVisible(false);

    nextButton.setOnAction(e -> {
        // Switch into give mode
        isGiveMode = true;

        // Open inventory so they can pick an item
        if (!inventory.isVisible()) {
            updateInventoryUI();
            inventory.setVisible(true);
            inventory.requestFocus();
        }

        // Prompt player to choose an item
        dialogueBox.setText("Select an item from your inventory to give to " + npc.getName() + ".");
        dialogueBox.setWrapText(true);
    });
}



    private void showDialogueNode(org.json.JSONObject npcDialogue, String nodeId) {
        if (!npcDialogue.has(nodeId))
            return;

        org.json.JSONObject node = npcDialogue.getJSONObject(nodeId);
        String text = node.getString("text");
        org.json.JSONObject options = node.getJSONObject("options");

        dialogueOverlay.setVisible(true);
        dialogueNameLabel.setText(npcDialogue.has("name") ? npcDialogue.getString("name") : "The Guide");
        // Better: pass NPC name to this method or store current NPC
        // For now, let's just use the text.

        dialogueBox.setText(text);

        optionsBox.getChildren().clear();
        optionsBox.setVisible(true);
        nextButton.setVisible(false);

        if (options.isEmpty()) {
            optionsBox.setVisible(false);
            nextButton.setVisible(true);
            nextButton.setText("Close");
            nextButton.setOnAction(e -> {
                dialogueOverlay.setVisible(false);
                dialogueBox.clear();
                isGiveMode = false;
            });
        } else {
            for (String key : options.keySet()) {
                String nextNodeId = options.getString(key);
                Button optionButton = new Button(key);
                optionButton.getStyleClass().add("button"); // Use CSS style
                optionButton.setMaxWidth(Double.MAX_VALUE);
                optionButton.setOnAction(e -> showDialogueNode(npcDialogue, nextNodeId));
                optionsBox.getChildren().add(optionButton);
            }
        }
    }

    private void showPickupPopup(String message) {
        pickupPopup.setText(message);
        pickupPopup.setVisible(true);
        pickupPopup.setOpacity(1.0);

        // Simple fade out animation
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(Duration.seconds(2), pickupPopup);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setDelay(Duration.seconds(1));
        ft.setOnFinished(e -> pickupPopup.setVisible(false));
        ft.play();
    }

    private void updateInventoryUI() {
        inventoryGrid.getChildren().clear(); // this is to clear the grid so we dont have duplicates when updating

        List<Item> items = gameEngine.getPlayer().getInventory();

        int maxSlots = 16;

        for (int i = 0; i < maxSlots; i++) { // this is to add empty slots to the inventory grid so it looks like an
                                             // inventory even if there are no items
            int col = i % 4;
            int row = i / 4;

            javafx.scene.layout.StackPane slot = new javafx.scene.layout.StackPane();
            slot.setPrefSize(50, 50);

            slot.getStyleClass().add("inventory-slot");

            if (i < items.size()) {
                Item item = items.get(i);

                ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                icon.setFitWidth(40);
                icon.setFitHeight(40);
                icon.setPreserveRatio(true);
                slot.setOnMouseClicked(e -> {
                    System.out.println("Selected: " + item.getName());
                    inventoryGrid.getChildren().forEach(n -> n.getStyleClass().remove("inventory-slot-selected"));
                    slot.getStyleClass().add("inventory-slot-selected");
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
        if (inventory.isVisible()) {
            inventory.setVisible(false);
            gameScreen.requestFocus(); // this is to put the focus back to the game screen so can click again
        } else {
            updateInventoryUI(); // refresh the grid before it opens
            inventory.setVisible(true);
            inventory.requestFocus(); // this is to put the focus on the inventory so can click items in it
        }
    }

    public void dropItemFromInventory(Event event) {
        gameEngine.dropItem(selected.getName());
        updateInventoryUI();
        updateScreen();
        System.out.println("Inventory after dropping: " + gameEngine.getPlayer().getInventory());
    }

    public void onCombineModeClick(Event event) {
        if (isCombineMode) { // if already in combine mode then exit it
            exitCombineMode();
            examineButton.setDisable(false); // reenable examine button when exiting combine mode
            dropButton.setDisable(false); // reenable drop button when exiting combine mode
        } else {
            isCombineMode = true; // otherwise would turn it on then show the combine panel and update the slots
            combinePanel.setVisible(true);
            combineItems.clear();
            updateCombineSlots();
            System.out.println("Combine Mode Activated");
            examineButton.setDisable(true); // disable examine button while in combine mode
            dropButton.setDisable(true); // disable drop button while in combine mode
        }

    }

    private void exitCombineMode() { // this is to exit and reset combine mode stuff
        isCombineMode = false;
        combinePanel.setVisible(false);
        combineItems.clear();
        updateCombineSlots();
        updateInventoryUI();
    }

    public void handleCombineSelecting(Item item) { // if the item is already in the combine list then remove it
                                                    // otherwise would add if there is space
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
            if (result.contains("Success") || result.contains("created")) { // Assuming success message contains these
                                                                            // words
                soundManager.playSoundEffect("success.mp3");
            }
        }
    }

    public void onClearButtonClick(Event event) {
        if (combineItems.isEmpty()) {
            return; // nothing to clear
        } else {
            if (combineItems.size() == 1) {
                if (gameEngine.getPlayer().getInventory().contains(combineItems.get(0))) { // If item already in
                                                                                           // inventory, just remove
                                                                                           // from combine list
                    combineItems.remove(0);
                } else {
                    gameEngine.getPlayer().addItemToInventory(combineItems.get(0)); // otherwise add it back to
                                                                                    // inventory so they dont just go
                                                                                    // poof to the void
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

        if (currentAnimation != null) { // this is so if you click queue a bunch of times it just stops the current anim
                                        // to start another one
            currentAnimation.stop();
        }

        double currentVisualX = playerImageView.getLayoutX() + playerImageView.getTranslateX(); // this is to get the
                                                                                                // current position
                                                                                                // since the translate
                                                                                                // wouldnt store like
                                                                                                // intermediate
                                                                                                // positions
        double currentVisualY = playerImageView.getLayoutY() + playerImageView.getTranslateY();

        playerImageView.setLayoutX(currentVisualX); // reset layout to current position
        playerImageView.setLayoutY(currentVisualY);
        playerImageView.setTranslateX(0); // reset translate to 0
        playerImageView.setTranslateY(0);
        playerImageView.setX(0);
        playerImageView.setY(0);

        double difX = x - currentVisualX; // this is to get the difference between current position and target so it can
                                          // animate to that position
        double difY = y - currentVisualY;

        currentAnimation = new TranslateTransition(Duration.seconds(1), playerImageView);
        currentAnimation.setToX(difX);
        currentAnimation.setToY(difY);

        currentAnimation.setOnFinished(e -> { // this is to set the final position properly and then reset translate so
                                              // no weird stuff happens
            playerImageView.setLayoutX(x);
            playerImageView.setLayoutY(y);
            playerImageView.setTranslateX(0);
            playerImageView.setTranslateY(0);
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
        exitExamineMode();
    }

    public void switchToMainScene(Event event) throws IOException {
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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/loadGame.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void quitGame(Event event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}