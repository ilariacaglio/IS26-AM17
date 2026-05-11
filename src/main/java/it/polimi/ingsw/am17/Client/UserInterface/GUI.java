package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Builder;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.Hunter;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.UUID;


public class GUI extends Application implements UI {

    private static final double START_WINDOW_WIDTH = 400;
    private static final double START_WINDOW_HEIGHT = 300;
    private static final double GAME_WINDOW_WIDTH = 1200;
    private static final double GAME_WINDOW_HEIGHT = 800;


    private static VirtualServer staticServer;
    private static VirtualView staticClient;
    private static ClientModel staticGame;

    private VBox root;
    public static Scene scene;
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

//TODO: find a way to add all the CLI functions (like choose color)
    @Override
    public void start() {
        Application.launch(GUI.class);
    }
    @Override
    public void start(Stage stage) {
        drawStartInterface();
        scene = new Scene(root, START_WINDOW_WIDTH, START_WINDOW_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("MESOS");
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        stage.show();

    }

    @Override
    public void drawInterface(ClientModel game) {
        staticGame = game;
        Platform.runLater(() -> {
            // Ora sei nel thread giusto!
            // Se 'scene' è una variabile globale della classe GUI:
            if (GUI.scene != null && GUI.scene.getWindow() != null) {
                GUI.scene.setRoot(drawGameInterface());
            } else {
                System.err.println("La scena o la finestra non sono ancora pronte!");
            }
        });
    }



    public Parent drawGameInterface() {
        Stage stage = (Stage) scene.getWindow();
        stage.setHeight(GAME_WINDOW_HEIGHT);
        stage.setWidth(GAME_WINDOW_WIDTH);
        root = new VBox(10);
        root.setPadding(new Insets(10));

        //TODO: add graphics
        //TODO: add buttons methods with setOnAction()
        //TODO: fix dimension

        //create cards like buttons so player can select them
        //upperCards

        //tribes cards first
        HBox upperCardsBox =new HBox(10);
        for(TribesCard card : staticGame.getUpperTribeRow()){
            CardGUI upperCards = new CardGUI(card.getImagePath());
            upperCardsBox.getChildren().add(upperCards);
        }
        //building cards second
        for(BuildingCard card : staticGame.getUpperBuildingRow()){
            CardGUI upperCards = new CardGUI(card.getImagePath());
            upperCardsBox.getChildren().add(upperCards);
        }

        //put turnCard and offeringCard in the same HBox
        HBox offeringCardBox = new HBox(10);
        //turnCard first
        CardGUI turnCard = new CardGUI(staticGame.getTURN_CARD_IMAGE_PATH());
        offeringCardBox.getChildren().add(turnCard);

        //offeringCards second
        for(OfferingCard card : staticGame.getOfferingCards()){
            CardGUI offeringCard = new CardGUI(card.getImagePath());
            offeringCardBox.getChildren().add(offeringCard);
        }


        //lowerCards
        //tribe cards first
        HBox lowerCardsBox =  new HBox(10);
        for(TribesCard card : staticGame.getLowerTribeRow()){
            CardGUI lowerCards = new CardGUI(card.getImagePath());
            lowerCardsBox.getChildren().add(lowerCards);
        }
        //building cards second
        for(BuildingCard card : staticGame.getLowerBuildingRow()){
            CardGUI lowerCards = new CardGUI(card.getImagePath());
            lowerCardsBox.getChildren().add(lowerCards);
        }
        //other players card buttons
        HBox playersCardsBox =  new HBox(10);
        for(Player p : staticGame.getOrderedPlayers()){
            Button playerCards = new Button(p.getNickname() );
            playersCardsBox.getChildren().add(playerCards);
        }
        //player cards
        //TODO: show my cards always, no button
        Label personalCards = new Label("My Cards");
        HBox personalCardsBox =  new HBox(10);
        personalCardsBox.getChildren().add(personalCards);
        for(CharacterCard card : staticGame.getLocalPlayer().getCharacterCards()){
            CardGUI characterCard = new CardGUI(card.getImagePath());
            personalCardsBox.getChildren().add(characterCard);
        }

        //food and PP
        food = new Label("Food: " +staticGame.getLocalPlayer().getFood());//TODO: add logic to update + borders (layout problem)
        points = new Label("Points: "+staticGame.getLocalPlayer().getPp());
        HBox playerResourcesBox =  new HBox(10);
        playerResourcesBox.getChildren().addAll(points, food);

        //add components to root
        // Center the upper cards
        upperCardsBox.setAlignment(Pos.CENTER);

        // Center the offering cards
        offeringCardBox.setAlignment(Pos.CENTER);

        // Center the lower cards
        lowerCardsBox.setAlignment(Pos.CENTER);

        // Center the player buttons
        playersCardsBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(upperCardsBox, offeringCardBox, lowerCardsBox, playerResourcesBox,
                personalCardsBox, playersCardsBox);
        return root;
    }

    private Parent drawConnectionInterface(){
        root = new VBox(25); // Increased spacing between elements
        root.setAlignment(Pos.CENTER); // Centered menu
        root.setPadding(new Insets(50));
        //create Buttons
        Button createGameButton = new Button("CREATE GAME");
        Button  joinGameButton = new Button("JOIN GAME");
        Button exitButton = new Button("EXIT");

        // Apply style and width to all
        for (Button b : new Button[]{createGameButton, joinGameButton, exitButton}) {
            b.setPrefWidth(250);
            b.setCursor(Cursor.HAND);
        }

        Label title = new Label("MESOS GAME");
        title.setStyle("-fx-font-size: 30px; -fx-font-family: 'Arial Black';");

        // Use a VBox for the buttons so they stack vertically (standard for game menus)
        VBox buttonContainer = new VBox(15, createGameButton, joinGameButton, exitButton);
        buttonContainer.setAlignment(Pos.CENTER);
        //createGameButton opens drawInterface only for testing purposes
        createGameButton.setOnAction(e -> {
            scene.setRoot(drawPlayerCountSelection());
        });
        joinGameButton.setOnAction(e -> {
            // Cambia la radice della scena con l'interfaccia per l'ID
            joinGameButton.getScene().setRoot(drawJoinInterface());
        });

        //add Buttons to root
        root.getChildren().addAll(title,buttonContainer);
        return root;
    }

    private void drawStartInterface(){
        // Create the container
        root = new VBox(15); // 15px spacing between elements
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER); // Keep everything centered

        // Title Label
        Label title = new Label("!!!WELCOME TO MESOS!!!");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        // Input Grid for alignment
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // create nickname field
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Enter nickname...");

        // create color ComboBox
        ComboBox<Color> colorPicker = new ComboBox<>();
        colorPicker.getItems().setAll(Color.values());
        colorPicker.setValue(Color.values()[0]); // Default to first enum value

        // add textfield and combobox to grid with labels
        grid.add(new Label("Nickname:"), 0, 0);
        grid.add(nicknameField, 1, 0);
        grid.add(new Label("Your Color:"), 0, 1);
        grid.add(colorPicker, 1, 1);

        // create start button
        Button startButton = new Button("START ADVENTURE");//nome voluto da sara (non è vero)
        startButton.setPrefWidth(200);


        startButton.setOnAction(e -> {
            //get input values
            String name = nicknameField.getText();
            Color color = colorPicker.getValue();

            //check if nickname empty
            if (name.trim().isEmpty()) {
                nicknameField.setStyle("-fx-border-color: red;");
                return; // Don't proceed if empty
            }

            //create local player
            staticGame.createLocalPlayer(name, color);

            //go to next interface
            drawConnectionInterface();
            scene.setRoot(root);
        });

        root.getChildren().addAll(title, grid, startButton);
    }

    private VBox createWaitingOverlay() {
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


    private Parent drawPlayerCountSelection() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #243b55;");

        Label title = new Label("QUANTI GIOCATORI?");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        HBox options = new HBox(15);
        options.setAlignment(Pos.CENTER);

        //create waiting overlay
        VBox waitingOverlay = createWaitingOverlay();

        // Creiamo un bottone per ogni opzione (2, 3, 4 giocatori)
            for (int i = 2; i <= 5; i++) {
                int count = i;
                Button btn = new Button(String.valueOf(count));
                btn.setPrefSize(60, 60);
                btn.setStyle("-fx-background-color: #ecf0f1; -fx-font-size: 18px; -fx-font-weight: bold;");

                btn.setOnAction(e -> {
                    try {
                        waitingOverlay.setVisible(true);
                        options.setDisable(true);
                        staticServer.createGame(staticClient, staticGame.getLocalPlayer(), count);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                });

                options.getChildren().add(btn);
            }

        Button backButton = new Button("INDIETRO");
        backButton.setOnAction(e -> backButton.getScene().setRoot(drawConnectionInterface()));

        layout.getChildren().addAll(title, options, backButton, waitingOverlay);
        return layout;
    }

    private Parent drawJoinInterface() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #243b55;"); // Sfondo coerente

        Label title = new Label("INSERISCI ID PARTITA");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        //create waiting overlay
        VBox waitingOverlay = createWaitingOverlay();

        // Campo per l'ID
        TextField idField = new TextField();
        idField.setPromptText("Esempio: 1234");
        idField.setMaxWidth(200);
        idField.setStyle("-fx-font-size: 16px; -fx-alignment: center;");

        // Bottone per unirsi
        Button joinBtn = new Button("UNISCITI");
        joinBtn.setPrefWidth(200);
        joinBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        joinBtn.setOnAction(e -> {
            String gameID = idField.getText().trim();
            if (!gameID.isEmpty()) {
                try {
                    // Chiamata RMI per unirsi
                    waitingOverlay.setVisible(true);
                    staticServer.joinGame(staticClient, UUID.fromString(gameID),staticGame.getLocalPlayer());

                } catch (Exception ex) {
                    // Se l'ID è sbagliato o il server dà errore, mostra un alert
                    idField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    System.err.println("Errore nel join: " + ex.getMessage());
                }
            }
        });

        Button backBtn = new Button("INDIETRO");
        backBtn.setOnAction(e -> backBtn.getScene().setRoot(drawConnectionInterface()));

        layout.getChildren().addAll(title, idField, joinBtn, backBtn, waitingOverlay);
        return layout;
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
