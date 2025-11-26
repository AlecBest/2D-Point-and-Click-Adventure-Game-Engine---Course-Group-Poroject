package ca.uwo.cs2212.group21;

import java.io.IOException;

import ca.uwo.cs2212.group21.model.NPC;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GameController {

    
    private Stage stage;
    private Scene scene;
    private Parent root; 

    @FXML
    private AnchorPane gamePane;
    private NPC myGuard;
    @FXML
    private AnchorPane mainScreen;
    @FXML
    private AnchorPane gameScreen;
    @FXML
    private AnchorPane inventory;

    public void spawnNPC(Event event){
        RadioButton btn = (RadioButton) event.getSource();

        if (btn.isSelected()) {
            if (myGuard == null) {
                myGuard = new NPC("Guard", "Halt! Who goes there?", false, "/images/guard.png");
            }
            if (!gamePane.getChildren().contains(myGuard.getView())) {
            gamePane.getChildren().add(myGuard.getView());
            }
        } else {
            if (myGuard != null) {
                gamePane.getChildren().remove(myGuard.getView());
            }      
        }
    }

    public void switchToMainScene(Event event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainScene.fxml"));
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
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoadGameScene.fxml"));
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
