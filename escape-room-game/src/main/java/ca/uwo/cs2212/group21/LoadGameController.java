package ca.uwo.cs2212.group21;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.json.JSONTokener;

import ca.uwo.cs2212.group21.model.GameEngine;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoadGameController {

    @FXML private ListView<String> saveFilesList;
    @FXML private Button loadGameButton;
    @FXML private Button backButton;


    @FXML private Pane detailsPanel; 
    @FXML private Label previewTimeLabel;
    @FXML private Label previewScoreLabel;
    @FXML private Label previewRoomLabel;

    public void initialize() {

        File folder = new File("saves");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    saveFilesList.getItems().add(file.getName());
                }
            }
        }

        if (detailsPanel != null) detailsPanel.setVisible(false);
        if (loadGameButton != null) loadGameButton.setDisable(true);

        saveFilesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showSaveDetails(newVal);
                if (loadGameButton != null) loadGameButton.setDisable(false);
            } else {
                if (detailsPanel != null) detailsPanel.setVisible(false);
                if (loadGameButton != null) loadGameButton.setDisable(true);
            }
        });
    }


    private void showSaveDetails(String filename) {
        if (detailsPanel == null) return;

        try (InputStream is = new FileInputStream("saves/" + filename)) {
            JSONTokener tokener = new JSONTokener(is);
            JSONObject data = new JSONObject(tokener);

            int time = data.optInt("timeRemaining", 0);
            int score = data.optInt("score", 0);
            String room = data.optString("currentRoom", "Unknown");

            previewTimeLabel.setText("Time Remaining: " + (time / 60) + "m " + (time % 60) + "s");
            previewScoreLabel.setText("Current Score: " + score + " Stars");
            previewRoomLabel.setText("Room: " + room);
            
            detailsPanel.setVisible(true);

        } catch (Exception e) {
            System.err.println("Could not preview file: " + filename);
            e.printStackTrace();
        }
    }


    public void onLoadGameClicked(ActionEvent event) {
        String filename = saveFilesList.getSelectionModel().getSelectedItem();
        if (filename == null) return;

        try {
            GameEngine engine = new GameEngine("/worldMap.json");
            engine.loadGame("saves/" + filename);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameView.fxml"));
            Parent root = loader.load();

            theGameController gameController = loader.getController();
            gameController.setGameEngine(engine);
            gameController.startGameFromLoad(); 

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/gameView.fxml"));
        Parent root = loader.load();
        
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}