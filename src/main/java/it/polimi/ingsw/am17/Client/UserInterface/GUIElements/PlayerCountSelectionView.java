package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Draws the interface that allows the user to select the number of players before creating a new game
 */
public class PlayerCountSelectionView {
    private VBox root;
    private final GUI mainGui;
    private static final int MIN_PLAYER = 2;
    private static final int MAX_PLAYER = 5;
    private boolean isWaiting = false;
    private Button backButton;

    public PlayerCountSelectionView(GUI mainGui) {
        this.mainGui = mainGui;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(ScreenScale.size(20));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #243b55;");

        //ask how many player the user wants
        Label title = new Label("NUMBER OF PLAYER?");
        title.setStyle("-fx-text-fill: white; -fx-font-size: %dpx; -fx-font-weight: bold;".formatted(ScreenScale.sizeInt(20)));

        HBox options = new HBox(ScreenScale.size(15));
        options.setAlignment(Pos.CENTER);

        //create waiting overlay
        VBox waitingOverlay = createWaitingOverlay();

        // create button to choose from
        for (int i = MIN_PLAYER; i <= MAX_PLAYER; i++) {
            Button btn = createChoiceButton(i, waitingOverlay, options);
            options.getChildren().add(btn);
        }

        backButton = new Button("Back");
        backButton.setOnAction(_ -> {
            if (isWaiting) return; // Prevent clicking Back if already waiting
            mainGui.showConnectionInterface();
        });

        root.getChildren().addAll(title, options, backButton, waitingOverlay);
    }

    /**
     * @return the button with the specified number as text
     */
    private Button createChoiceButton(int i, VBox waitingOverlay, HBox options) {
        Button btn = new Button(String.valueOf(i));
        btn.setPrefSize(ScreenScale.size(60), ScreenScale.size(60));
        btn.setStyle("-fx-background-color: #ecf0f1; -fx-font-size: %dpx; -fx-font-weight: bold;".formatted(ScreenScale.sizeInt(18)));

        btn.setOnAction(_ -> {
            // Prevent spam clicks if they click the active button again
            if (isWaiting) return;

            try {
                isWaiting = true;
                waitingOverlay.setVisible(true);

                backButton.setDisable(true);

                // Loop through all buttons in the HBox
                for (javafx.scene.Node node : options.getChildren()) {
                    // If the node is a button, and it is NOT the one just clicked, disable it
                    if (node instanceof Button && node != btn) {
                        node.setDisable(true);
                    }
                }

                mainGui.createGame(i);
            } catch (Exception ex) {
                isWaiting = false;
                throw new RuntimeException(ex);
            }
        });
        return btn;
    }

    /**
     * @return  the waiting overlay, displayed when the game is in lobby state
     */
    public VBox createWaitingOverlay() {
        // create the container
        VBox overlay = new VBox(ScreenScale.size(10)); // 10px spacing
        overlay.setAlignment(Pos.CENTER);

        // Modify Background: Semi-transparent black/grey
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");

        // Add 10 pixels of padding at the top
        overlay.setPadding(new Insets(ScreenScale.size(10), 0, 0, 0));

        // Add loading circle
        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(ScreenScale.size(60), ScreenScale.size(60));
        progress.setStyle("-fx-progress-color: white;");

        //add text
        Label text = new Label("Waiting for other players...");
        text.setStyle("-fx-text-fill: white; -fx-font-size: %dpx; -fx-font-weight: bold;".formatted(ScreenScale.sizeInt(18)));

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
