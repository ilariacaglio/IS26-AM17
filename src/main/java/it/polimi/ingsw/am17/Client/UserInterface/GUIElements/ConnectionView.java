package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Displays the interface when the user is asked to create or join a game
 */
public class ConnectionView {
    private VBox root;
    private final GUI mainGui;

    public ConnectionView(GUI mainGui) {
        this.mainGui = mainGui;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(25); // Increased spacing between elements
        root.setAlignment(Pos.CENTER); // Centered menu
        root.setPadding(new Insets(50));

        //create Buttons
        Button createGameButton = new Button("CREATE GAME");
        Button  joinGameButton = new Button("JOIN GAME");
        Button  backButton = new Button("BACK");
        Button exitButton = new Button("EXIT");

        // Apply style and width to all
        for (Button b : new Button[]{createGameButton, joinGameButton, backButton, exitButton}) {
            b.setPrefWidth(250);
            b.setCursor(Cursor.HAND);
        }

        Label title = new Label("MESOS GAME");
        title.setStyle("-fx-font-size: 30px; -fx-font-family: 'Arial Black';");

        // Use a VBox for the buttons so they stack vertically (standard for game menus)
        VBox buttonContainer = new VBox(15, createGameButton, joinGameButton, backButton, exitButton);
        buttonContainer.setAlignment(Pos.CENTER);

        createGameButton.setOnAction(_ -> mainGui.showPlayerCountSelection());
        joinGameButton.setOnAction(_ -> mainGui.showJoinInterface());
        backButton.setOnAction( _ -> mainGui.showStartInterface());

        exitButton.setOnAction(_ -> {
            Platform.exit();//close window
            System.exit(0);
        });
        //add Buttons to root
        root.getChildren().addAll(title,buttonContainer);
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}