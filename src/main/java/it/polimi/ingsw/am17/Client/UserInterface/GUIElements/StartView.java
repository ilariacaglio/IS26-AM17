package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class StartView {
    private VBox root;
    private GUI mainGui;

    public StartView(GUI mainGui) {
        this.mainGui = mainGui;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("!!!WELCOME TO MESOS!!!");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        //ask for nickname
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Enter nickname...");

        //ask for color
        ComboBox<Color> colorPicker = new ComboBox<>();
        colorPicker.getItems().setAll(Color.values());
        colorPicker.setValue(Color.values()[0]);

        grid.add(new Label("Nickname:"), 0, 0);
        grid.add(nicknameField, 1, 0);
        grid.add(new Label("Your Color:"), 0, 1);
        grid.add(colorPicker, 1, 1);

        //button to save nickname and color and go to next screen
        Button startButton = new Button("START ADVENTURE");
        startButton.setPrefWidth(200);

        startButton.setOnAction(e -> {
            String name = nicknameField.getText();
            Color color = colorPicker.getValue();

            if (name.trim().isEmpty()) {
                nicknameField.setStyle("-fx-border-color: red;");
                return;
            }

            // Save the player to the main GUI
            Player newPlayer = new Player(name, color);
            mainGui.createLocalPlayer(newPlayer);

            // Tell the main GUI to switch screens
            mainGui.showConnectionInterface();
        });

        root.getChildren().addAll(title, grid, startButton);
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}