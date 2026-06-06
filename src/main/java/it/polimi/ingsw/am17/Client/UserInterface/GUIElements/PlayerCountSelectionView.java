package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.UUID;

public class PlayerCountSelectionView {
    private VBox root;
    private GUI mainGui;
    private final int MIN_PLAYER = 2;
    private final int MAX_PLAYER = 5;

    public PlayerCountSelectionView(GUI mainGui) {
        this.mainGui = mainGui;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #243b55;");

        //ask how many player the user wants
        Label title = new Label("NUMBER OF PLAYER?");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        HBox options = new HBox(15);
        options.setAlignment(Pos.CENTER);

        //create waiting overlay
        VBox waitingOverlay = createWaitingOverlay();

        // create button to choose from
        for (int i = MIN_PLAYER; i <= MAX_PLAYER; i++) {
            Button btn = createChoiceButton(i, waitingOverlay, options);
            options.getChildren().add(btn);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> mainGui.showConnectionInterface());

        root.getChildren().addAll(title, options, backButton, waitingOverlay);
    }

    private Button createChoiceButton(int i, VBox waitingOverlay, HBox options) {
        Button btn = new Button(String.valueOf(i));
        btn.setPrefSize(60, 60);
        btn.setStyle("-fx-background-color: #ecf0f1; -fx-font-size: 18px; -fx-font-weight: bold;");

        //on click send message to server and show waiting overlay
        btn.setOnAction(e -> {
            try {
                waitingOverlay.setVisible(true);
                options.setDisable(true);//disable all buttons so player cant spam requests
                mainGui.createGame(i);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });
        return btn;
    }

    public VBox createWaitingOverlay() {
        // create the container
        VBox overlay = new VBox(10); // 10px spacing
        overlay.setAlignment(Pos.CENTER);

        // Modify Background: Semi-transparent black/grey
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Add 10 pixels of padding at the top
        overlay.setPadding(new Insets(10, 0, 0, 0));

        // Add loading circle
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(60, 60);
        progress.setStyle("-fx-progress-color: white;");

        //add text
        Label text = new Label("Waiting for other players...");
        text.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        overlay.getChildren().addAll(progress, text);

        //hide
        overlay.setVisible(false);
        return overlay;
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}
