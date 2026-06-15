package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.UUID;

/**
 * Draws the interface displayed when the user wants to join a game from the id list
 */
public class JoinView {
    private VBox root;
    private final GUI mainGui;
    private final List<UUID> listOfGames;
    ListView<UUID> gameList;

    public JoinView(GUI mainGui, List<UUID> listOfGames) {
        this.mainGui = mainGui;
        this.listOfGames = listOfGames;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #243b55;");

        Label title = new Label("CHOOSE A GAME");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Field to type GameID
        TextField idField = new TextField();
        idField.setPromptText("INSERT GAME ID");
        idField.setPrefWidth(400);
        idField.setStyle("-fx-font-size: 16px; -fx-alignment: center;");

        // Join button
        Button joinBtn = new Button("JOIN");
        joinBtn.setPrefWidth(400);
        joinBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        joinBtn.setOnAction(_ -> {
            String gameID = idField.getText().trim();
            if (!gameID.isEmpty()) {
                try {
                    mainGui.joinGame(UUID.fromString(gameID));
                } catch (Exception ex) {
                    idField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    System.err.println("Error in join: " + ex.getMessage());
                }
            }
        });

        Button backBtn = new Button("BACK");
        backBtn.setOnAction(_ -> mainGui.showConnectionInterface());

        // Create a ListView instead of a TextArea
        gameList = new ListView<>();
        gameList.setMinHeight(120);
        gameList.setPrefHeight(120);

        // Listen for user clicks natively
        gameList.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                // Automatically populate the text field when an item is clicked
                idField.setText(newValue.toString());
                idField.setStyle("-fx-font-size: 16px; -fx-alignment: center;"); // Reset border in case of previous error
            }
        });

        HBox idGame = new HBox(10);
        idGame.getChildren().addAll(idField, joinBtn);

        updateGameList(listOfGames);
        root.getChildren().addAll(title, gameList, idField, joinBtn, backBtn);

    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }

    /**
     * Updates the game id list with the list passed as parameter
     */
    public void updateGameList(List<UUID> list) {
        Platform.runLater(() -> {
            gameList.getItems().clear(); // Clear old items
            gameList.getItems().addAll(list); // Add the new UUIDs directly
        });
    }
}
