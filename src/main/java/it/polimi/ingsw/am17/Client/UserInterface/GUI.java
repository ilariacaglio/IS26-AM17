package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.UUID;

public class GUI extends Application implements UI {
    private static VirtualServer staticServer;
    private static VirtualView staticClient;
    private static ClientModel staticGame;

    private VBox root;
    private Scene scene;
    private Label food;
    private Label points;

    // This method allows your main logic to "prepare" the data before launching
    public GUI(VirtualServer server, VirtualView view, ClientModel model) {
        staticServer = server;
        staticClient = view;
        staticGame = model;
    }

    // MANDATORY: No-argument constructor (or just let Java provide the default one)
    public GUI() {}

    /*@Override
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
    }*/
//TODO: find a way to add all the CLI functions (like choose color)
    @Override
    public void start() {
        Application.launch(GUI.class);
    }
    @Override
    public void start(Stage stage) {
        drawStartInterface();
        scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("MESOS table gameboard");
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();

    }

    @Override
    public void drawInterface(ClientModel game) {
        this.staticGame = game;
        root = new VBox(10);
        root.setPadding(new Insets(10));
        //TODO: use list.size() in all for (used random numbers for testing)
        //TODO: use getter methods in ClientModel for list.size()
        //TODO: add graphics
        //TODO: add buttons methods with setOnAction()
        //TODO: fix dimension

        //create cards like buttons so player can select them
        //upperCards
        HBox upperCardsBox =new HBox(10);
        for(int i=0; i<4; i++){
            Button upperCards = new Button("Upper Card " + i);
            upperCardsBox.getChildren().add(upperCards);
        }
        //turnCard and offeringCard in the same HBox
        HBox offeringCardBox = new HBox(10);
        //turnCard first
        //turnCard
        Button turnCard = new Button("Turn Card");
        offeringCardBox.getChildren().add(turnCard);
        //offeringCards
        for(int i=0; i<5; i++){
            Button offeringCard = new Button("Offering Card " + i);
            offeringCardBox.getChildren().add(offeringCard);
        }
        //lowerCards
        HBox lowerCardsBox =  new HBox(10);
        for(int i=0; i<6; i++){
            Button lowerCards = new Button("Lower Card " + i);
            lowerCardsBox.getChildren().add(lowerCards);
        }
        //other players card buttons
        HBox playersCardsBox =  new HBox(10);
        for(int i=0; i<3; i++){
            Button playerCards = new Button("Cards' Player " + i);
            playersCardsBox.getChildren().add(playerCards);
        }
        //player cards
        Button personalCards = new Button("My Cards");
        HBox personalCardsBox =  new HBox(10);
        personalCardsBox.getChildren().add(personalCards);
        //food and PP
        food = new Label("Food: ");//TODO: add logic to update + borders (layout problem)
        points = new Label("Points: ");
        HBox playerResourcesBox =  new HBox(10);
        playerResourcesBox.getChildren().addAll(points, food);

        //add components to root
        root.getChildren().addAll(upperCardsBox, offeringCardBox, lowerCardsBox, playerResourcesBox,
                personalCardsBox, playersCardsBox);
    }

    private void drawStartInterface(){
        root = new VBox(10);
        root.setPadding(new Insets(10));
        //create Buttons
        Button createGameButton = new Button("CREATE GAME");
        Button  joinGameButton = new Button("JOIN GAME");
        Button exitButton = new Button("EXIT");
        HBox startButtons = new HBox(10, createGameButton, joinGameButton, exitButton);
        //createGameButton opens drawInterface only for testing purposes
        createGameButton.setOnAction(e -> {

            drawInterface(staticGame);

            scene.setRoot(root);
        });

        //add Buttons to root
        root.getChildren().add(startButtons);
    }

    private void createGame(){
        root = new VBox(10);
        root.setPadding(new Insets(10));
        //ask how many players and let the player put the number
    }
    @Override
    public void printGameId(UUID gameId) {

    }
    @Override
    public void printEra(){

    }
    @Override
    public void printGamesList(){

    }

}
