package ca.uwo.cs2212.group21;

import java.io.IOException;
import javafx.scene.layout.AnchorPane;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoadGameController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML private AnchorPane mainScreen; //this is the title screen
    @FXML private AnchorPane gameScreen; //this is the actual game overlay
    
    
    public void switchToMainScene(Event event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/gameView.fxml")); 
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
