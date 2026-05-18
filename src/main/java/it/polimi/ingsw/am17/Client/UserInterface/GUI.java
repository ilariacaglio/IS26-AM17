package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import it.polimi.ingsw.am17.Server.Utility.MoveValidator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.List;


public class GUI extends Application implements UI {

    private String TURN_CARD_IMAGE_PATH = "/Images/background_game.png";

    private static final double START_WINDOW_WIDTH = 400;
    private static final double START_WINDOW_HEIGHT = 300;
    private static final double GAME_WINDOW_WIDTH = 1920;
    private static final double GAME_WINDOW_HEIGHT = 1080;

    private static List<CharacterCard> tribesSelected = new ArrayList<>();
    private static List<BuildingCard> buildingSelected = new ArrayList<>();
    private static OfferingCard offeringSelected = null;

    private static List<CardGUI> offeringCardGUI = new ArrayList<>();

    private static VirtualServer staticServer;
    private static VirtualView staticClient;
    private static ClientModel staticGame;
    private static Player localPlayer;
    private static Player selectedPlayer;

    private VBox root;
    public static Scene scene;
    private Label name;
    private Label food;
    private Label points;
    private HBox playerCardsBox;
    private HBox playerResourcesBox;

    // This method allows your main logic to "prepare" the data before launching
    public GUI(VirtualServer server, VirtualView view, ClientModel model) {
        staticServer = server;
        staticClient = view;
        staticGame = model;
    }

    // MANDATORY: No-argument constructor (or just let Java provide the default one)
    public GUI() {}

    @Override
    public  void setModel(ClientModel model) {
        this.staticGame = model;
    }
    /**
     * Updates localPlayer value when the object is updated into the queue by the server
     * Used to update food, pp and cards of the player
     */
    public void setLocalPlayer() {
        staticGame.getOrderedPlayers().stream()
                .filter(p -> p.getNickname().equals(localPlayer.getNickname()))
                .findFirst().ifPresent(foundPlayer -> localPlayer = foundPlayer);
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

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
    public void drawInterface(ClientModel game, String errorMessagge) {
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

    public static boolean isPlayerTurn()
    {
        return staticGame.isPlayerTurn();
    }



    public Parent drawGameInterface() {
        Stage stage = (Stage) scene.getWindow();
        stage.setMaximized(true);
        root = new VBox(10);
        root.setPadding(new Insets(10));

        // 1. Create the overlay VBox exactly like your original code (NO max size restriction)
        VBox turnOverlay = new VBox();
        turnOverlay.setAlignment(Pos.CENTER); // This will center the label inside the stretched box

            // 2. Create the label
        Label turnText = new Label("IT'S YOUR TURN!");

// 3. Put ALL the styling (background, border, font) directly on the Label instead of the VBox
        turnText.setStyle("""
    -fx-background-color: rgba(0, 0, 0, 0.75);
    -fx-background-radius: 15px;
    -fx-padding: 15px 40px;
    -fx-border-color: #c76b22;
    -fx-border-radius: 15px;
    -fx-border-width: 2px;
    -fx-text-fill: #f4dca6;
    -fx-font-weight: bold;
    -fx-font-size: 36px;
    -fx-font-family: 'Verdana';
""");

// 4. Add the shadow
        DropShadow textShadow = new DropShadow();
        textShadow.setRadius(5.0);
        textShadow.setOffsetY(3.0);
        textShadow.setColor(javafx.scene.paint.Color.color(0, 0, 0, 0.8));
        turnText.setEffect(textShadow);

// 5. Add the beautifully styled label to the stretching VBox
        turnOverlay.getChildren().add(turnText);
        turnOverlay.setVisible(false);
        if(staticGame.isPlayerTurn()){
            turnOverlay.setVisible(true);
        }

        root.setStyle("-fx-background-image: url('/images/background_game.png');");
        root.setStyle("""
            -fx-background-image: url('/images/background_game.png');
            -fx-background-size: cover;
            -fx-background-position: center center;
            -fx-background-repeat: no-repeat;
        """);

        //TODO: add graphics
        //TODO: add buttons methods with setOnAction()
        //TODO: fix dimension

        Label localPlayerName = new Label("Local Player: " + localPlayer.getNickname());
        localPlayerName.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        HBox localPlayerNameBox =  new HBox(10);
        localPlayerNameBox.getChildren().addAll(localPlayerName);

        //create cards like buttons so player can select them
        //upperCards

        //tribes cards first
        HBox upperCardsBox =new HBox(10);
        for(TribesCard card : staticGame.getUpperTribeRow()){
            CardGUI upperCards = new CardGUI(card);
            upperCardsBox.getChildren().add(upperCards);
        }
        //building cards second
        for(BuildingCard card : staticGame.getUpperBuildingRow()){
            CardGUI upperCards = new CardGUI(card);
            upperCardsBox.getChildren().add(upperCards);
        }

        //put turnCard and offeringCard in the same HBox
        HBox offeringCardBox = new HBox(10);
        //turnCard first
        CardGUI turnCard = new CardGUI(staticGame.getTURN_CARD_IMAGE_PATH());
        offeringCardBox.getChildren().add(turnCard);

        //offeringCards second
        for(OfferingCard card : staticGame.getOfferingCards()){
            CardGUI offeringCard = new CardGUI(card);
            offeringCardGUI.add(offeringCard);
            offeringCardBox.getChildren().add(offeringCard);
        }


        //lowerCards
        //tribe cards first
        HBox lowerCardsBox =  new HBox(10);
        for(TribesCard card : staticGame.getLowerTribeRow()){
            CardGUI lowerCards = new CardGUI(card);
            lowerCardsBox.getChildren().add(lowerCards);
        }
        //building cards second
        for(BuildingCard card : staticGame.getLowerBuildingRow()){
            CardGUI lowerCards = new CardGUI(card);
            lowerCardsBox.getChildren().add(lowerCards);
        }

        //send button
        Button sendButton = new Button("SEND");
        sendButton.setOnAction(e ->
        {
            try {
                if (offeringSelected != null) {
                    staticServer.pickOfferingCard(staticGame.getGameId(), localPlayer, offeringSelected);
                } else {
                    // get players offering card
                    OfferingCard myOfferingCard = staticGame.getOfferingCards().stream()
                            .filter(c->c.getPlayer()!= null && c.getPlayer().equals(localPlayer))
                            .findFirst().orElse(null);

                    Exception exception = MoveValidator.validateCardChoice(myOfferingCard.getNumCardsUpper(), myOfferingCard.getNumCardsLower(),
                            tribesSelected, buildingSelected, staticGame.getUpperTribeRow(), staticGame.getLowerTribeRow(),
                            staticGame.getUpperBuildingRow(), staticGame.getLowerBuildingRow());
                    if(exception == null)
                        staticServer.pickTribeCards(staticGame.getGameId(), localPlayer, tribesSelected, buildingSelected);
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error in selection");
                        alert.setHeaderText(null);
                        alert.setContentText(exception.getMessage());
                        alert.showAndWait();
                    }

                }
                offeringSelected = null;
                buildingSelected = new ArrayList<>();
                tribesSelected =  new ArrayList<>();
                offeringCardGUI = new ArrayList<>();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        sendButton.setStyle("""
            -fx-background-color: #5c2c16;\s
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 5px;
            -fx-cursor: hand;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            """);
        HBox sendButtonBox = new HBox();
        sendButtonBox.getChildren().add(sendButton);
        sendButtonBox.setAlignment(Pos.CENTER);


        //other players card buttons
        HBox playersCardsBox =  new HBox(10);
        HBox showCardsBox = new HBox(10);
        ScrollPane otherScrollPane = new ScrollPane(showCardsBox);
        //final Player[] openedPlayer = {null};
        for(Player p : staticGame.getOrderedPlayers()){
            Button playerButton = new Button(p.getNickname() );
            //set nickname color
            Color nicknameColor = p.getColor();
            switch (nicknameColor) {
                case RED:
                    playerButton.setStyle("""
                              -fx-text-fill: red;
                              -fx-background-color: #aaaaaa;
                              -fx-font-weight: bold;
                              -fx-font-size: 15px;
                    """);
                    break;
                case BLUE:
                    playerButton.setStyle("""
                              -fx-text-fill: blue;
                              -fx-background-color: #aaaaaa;
                              -fx-font-weight: bold;
                              -fx-font-size: 15px;
                    """);
                    break;
                case WHITE:
                    playerButton.setStyle("""
                              -fx-text-fill: white;
                              -fx-background-color: #aaaaaa;
                              -fx-font-weight: bold;
                              -fx-font-size: 15px;
                    """);
                    break;
                case BLACK:
                    playerButton.setStyle("""
                              -fx-text-fill: black;
                              -fx-background-color: #aaaaaa;
                              -fx-font-weight: bold;
                              -fx-font-size: 15px;
                    """);
                    break;
                case YELLOW:
                    playerButton.setStyle("""
                              -fx-text-fill: yellow;
                              -fx-background-color: #aaaaaa;
                              -fx-font-weight: bold;
                              -fx-font-size: 15px;
                    """);
                    break;
            }
            //add area for other players' cards

            otherScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            otherScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            otherScrollPane.setFitToHeight(true);
            otherScrollPane.setPannable(true);
            otherScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            playersCardsBox.getChildren().add(playerButton);
            StackPane.setAlignment(playersCardsBox, Pos.BOTTOM_CENTER);
            playerButton.setOnAction(e -> {
                selectedPlayer = p;
                createPlayerCardLabel();
                orderPersonalCards();
            });

        }

        VBox playersButtonBox = new VBox(10,  playersCardsBox, otherScrollPane);

        //player cards

        playerCardsBox =  new HBox(10);

        //add area for personal cards
        ScrollPane scrollPane = new ScrollPane(playerCardsBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        //order personal cards
        orderPersonalCards();



        VBox playerCardsBox = new VBox(10, scrollPane);

        //player name, food and pp
        playerResourcesBox =  new HBox(10);
        createPlayerCardLabel();


        //add components to root
        // Center the upper cards
        upperCardsBox.setAlignment(Pos.CENTER);

        // Center the offering cards
        offeringCardBox.setAlignment(Pos.CENTER);

        // Center the lower cards
        lowerCardsBox.setAlignment(Pos.CENTER);

        // Center the player buttons
        playersCardsBox.setAlignment(Pos.CENTER);

        // Create a blank region to act as a spring/spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        root.getChildren().addAll(turnOverlay, localPlayerNameBox, upperCardsBox, offeringCardBox, lowerCardsBox, sendButtonBox,
                playerResourcesBox, playerCardsBox, spacer, playersButtonBox);
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
            try {
                //ask server for gameList
                staticServer.getGamesList(staticClient);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // Cambia la radice della scena con l'interfaccia per l'ID
            joinGameButton.getScene().setRoot(drawJoinInterface());
        });

        exitButton.setOnAction(e -> {
            Platform.exit();//close window
            System.exit(0);
            //TODO: chiudere connessione con socket e RMI
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
        Button startButton = new Button("START ADVENTURE");
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
            localPlayer = new Player(name, color);
            selectedPlayer = localPlayer;

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
                        staticServer.createGame(staticClient, localPlayer, count);
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
        offeringSelected = null;
        tribesSelected = new ArrayList<>();
        buildingSelected = new ArrayList<>();


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
                    staticServer.joinGame(staticClient, UUID.fromString(gameID),localPlayer);

                } catch (Exception ex) {
                    // Se l'ID è sbagliato o il server dà errore, mostra un alert
                    idField.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    System.err.println("Errore nel join: " + ex.getMessage());
                }
            }
        });

        Button backBtn = new Button("INDIETRO");
        backBtn.setOnAction(e -> backBtn.getScene().setRoot(drawConnectionInterface()));

        TextArea gameList = new TextArea();

        gameList.setEditable(false);
        gameList.setWrapText(true);

        gameList.setOnMouseClicked(event -> {
            int caretPos = gameList.getCaretPosition();
            String text = gameList.getText();

            int start = text.lastIndexOf('\n', caretPos - 1) + 1;
            int end = text.indexOf('\n', caretPos);
            if (end == -1) end = text.length();

            String line = text.substring(start, end);
            idField.setText(line);
        });

        HBox idGame = new HBox(10);
        idGame.getChildren().addAll(idField, joinBtn);
        layout.getChildren().addAll(title, gameList, idGame, backBtn, waitingOverlay);
        updateGameList(gameList);

        return layout;
    }

    public static void tribesSelected(CharacterCard card)
    {
        if(tribesSelected.contains(card))
            tribesSelected.remove(card);
        else
            tribesSelected.add(card);
    }

    public static void buildingSelected(BuildingCard card)
    {
        if(buildingSelected.contains(card))
            buildingSelected.remove(card);
        else
            buildingSelected.add(card);
    }



    public static void offeringSelected(OfferingCard card)
    {
        if(offeringSelected == card)
            offeringSelected = null;
        else
            offeringSelected = card;

        for(CardGUI cardGUI : offeringCardGUI)
        {
            cardGUI.selected = false;
            cardGUI.setSelected();
        }
    }

    private void updateGameList(TextArea gameList) {
        Platform.runLater(() -> {
            gameList.setText(staticGame.getGamesIdList().stream()
                    .map(UUID::toString)
                    .collect(java.util.stream.Collectors.joining("\n")));
        });

    }

    private void orderPersonalCards(){
        //update selected player
        selectedPlayer = staticGame.getPlayerFromList(selectedPlayer);

        List<CharacterCard> orderedCards = new ArrayList<>(
                selectedPlayer.getCharacterCards()
        );

        orderedCards.sort(Comparator.comparing(CharacterCard::getCardType));

        playerCardsBox.getChildren().clear();

        //add personal character cards
        for(CharacterCard card : orderedCards){
            CardGUI characterCard = new CardGUI(card.getImagePath());
            playerCardsBox.getChildren().add(characterCard);
        }
        //add personal building cards
        for(BuildingCard card : localPlayer.getBuildingCards()){
            CardGUI buildingCard = new CardGUI(card.getImagePath());
            playerCardsBox.getChildren().add(buildingCard);
        }
    }


    private void createPlayerCardLabel(){
        name = new Label("Name: " + selectedPlayer.getNickname());
        name.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        food = new Label("Food: " + selectedPlayer.getFood());
        food.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        points = new Label("Points: "+ selectedPlayer.getPp());
        points.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        if(playerResourcesBox != null) {
            playerResourcesBox.getChildren().clear();
            playerResourcesBox.getChildren().addAll(name, points, food);
        }
    }


    public static boolean isPickTribesCard()
    {
        return !staticGame.isPickOCPhase();
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
