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

import java.util.List;

/**
 * Draws the starting view of the GUI, which allows the user to enter a nickname and select a color
 */
public class StartView {
    private VBox root;
    private final GUI mainGui;
    private final List<Color> availableColors;

    public StartView(GUI mainGui, List<Color> availableColors) {
        this.mainGui = mainGui;
        this.availableColors = availableColors;
        buildUI();
    }

    private void buildUI() {
        root = new VBox(ScreenScale.size(25));
        root.setPadding(new Insets(ScreenScale.size(40)));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("!!!WELCOME TO MESOS!!!");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: %dpx;".formatted(ScreenScale.sizeInt(36)));
        GridPane grid = new GridPane();
        grid.setHgap(ScreenScale.size(15));
        grid.setVgap(ScreenScale.size(20));
        grid.setAlignment(Pos.CENTER);

        //ask for nickname
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Enter nickname...");
        nicknameField.setStyle("-fx-font-size: %dpx;".formatted(ScreenScale.sizeInt(16)));
        nicknameField.setPrefWidth(ScreenScale.size(200));


        //ask for color
        ComboBox<Color> colorPicker = new ComboBox<>();
        if(availableColors == null || availableColors.isEmpty())
            colorPicker.getItems().setAll(Color.values());
        else
            colorPicker.getItems().setAll(availableColors);
        colorPicker.setValue(Color.values()[0]);
        colorPicker.setStyle("-fx-font-size: %dpx;".formatted(ScreenScale.sizeInt(16)));
        colorPicker.setPrefWidth(ScreenScale.size(200));

        grid.add(new Label("Nickname:"), 0, 0);
        grid.add(nicknameField, 1, 0);
        grid.add(new Label("Your Color:"), 0, 1);
        grid.add(colorPicker, 1, 1);

        //button to save nickname and color and go to next screen
        Button startButton = getStartButton(nicknameField, colorPicker);

        root.getChildren().addAll(title, grid, startButton);
    }

    private Button getStartButton(TextField nicknameField, ComboBox<Color> colorPicker) {
        Button startButton = new Button("START ADVENTURE");
        startButton.setPrefWidth(ScreenScale.size(250));
        startButton.setStyle("-fx-font-weight: bold; -fx-font-size: %dpx; -fx-padding: %dpx %dpx;".formatted(
                ScreenScale.sizeInt(18),
                ScreenScale.sizeInt(12),
                ScreenScale.sizeInt(20)
        ));

        startButton.setOnAction(_ -> {
            String name = nicknameField.getText();
            Color color = colorPicker.getValue();

            if (name.trim().isEmpty()) {
                nicknameField.setStyle("-fx-border-color: red; -fx-border-width: %dpx; -fx-font-size: %dpx;".formatted(
                        ScreenScale.sizeInt(2),
                        ScreenScale.sizeInt(16)
                ));
                return;
            }

            // Save the player to the main GUI
            Player newPlayer = new Player(name, color);
            mainGui.createLocalPlayer(newPlayer);

            // Tell the main GUI to switch screens
            mainGui.showConnectionInterface();
        });
        return startButton;
    }

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}