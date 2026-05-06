package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.UUID;

public class GUI extends Application implements UI {
    private static VirtualServer staticServer;
    private static VirtualView staticClient;
    private static ClientModel staticGame;

    // This method allows your main logic to "prepare" the data before launching
    public GUI(VirtualServer server, VirtualView view, ClientModel model) {
        staticServer = server;
        staticClient = view;
        staticGame = model;
    }

    // MANDATORY: No-argument constructor (or just let Java provide the default one)
    public GUI() {}

    @Override
    public void start(Stage primaryStage) {
        // 1. Create a component (Node)
        Button btn = new Button("Click Me");
        btn.setOnAction(e -> System.out.println("Hello World!"));

        // 2. Arrange components in a Layout (Root Node)
        StackPane root = new StackPane();
        root.getChildren().add(btn);

        // 3. Create the Scene
        Scene scene = new Scene(root, 300, 250);

        // 4. Configure the Stage
        primaryStage.setTitle("My JavaFX App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start() {
        Application.launch(GUI.class);
    }

    @Override
    public void drawInterface(ClientModel game) {

    }
    public void printGameId(UUID gameId) {

    }
    public void printEra(){

    }
    public void printGamesList(){

    }

}
