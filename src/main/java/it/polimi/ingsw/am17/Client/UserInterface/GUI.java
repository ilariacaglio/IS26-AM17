package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.GUIElements.*;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualServer;
import it.polimi.ingsw.am17.CommonInterfaces.VirtualView;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameState;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.List;

public class GUI implements UI {

    private final double START_WINDOW_WIDTH = 400;
    private final double START_WINDOW_HEIGHT = 300;

    private VirtualServer server;
    private VirtualView client;
    private ClientModel game;
    private Player localPlayer;

    private VBox root;
    public Scene scene;

    private GameView gameView;

    private boolean isGameInterfaceInitialized = false;

    List<Color> availableColors;

    public GUI(VirtualServer server, VirtualView view) {
        this.server = server;
        client = view;
    }
    JoinView joinView;

    /**
     * Start the JavaFx Application
     */
    public void start() {
        Runnable startFX = () -> {
            Stage stage = new Stage();

            StartView startView = new StartView(this, null);
            scene = new Scene(startView.getRoot(), START_WINDOW_WIDTH, START_WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.setTitle("MESOS");//window name
            stage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });
            stage.show();
        };

        try {
            //starts the JavaFX thread
            Platform.startup(startFX);
        } catch (IllegalStateException e) {
            // If the JavaFX toolkit is already running
            Platform.runLater(startFX);
        }
    }

    @Override
    public void drawInterface(String errorMessagge) {
        Platform.runLater(() -> {
            GameState gameState = game.getGameState();
            if(gameState.isGameStarted()) {
                if (!gameState.isGameEnded()) {
                    if (!isGameInterfaceInitialized) {
                        showGameInterface();
                        isGameInterfaceInitialized = true;
                    }
                    gameView.updateGameElements();
                } else {
                    showLocalInterface();
                }
            }
        });
    }
    public void updateInterfaceFromEndTurn(){
        Platform.runLater(() -> {
            gameView.updateGameElements();
        });
    }
    public void updateInterfaceFromPickTribes() {
        Platform.runLater(() -> {
            gameView.updateGameCardDecks();
        });
    }
    public void updateInterfaceFromPickOffering() {
        Platform.runLater(() -> {
            gameView.updateOfferingDeck();
        });
    }

    public void showGlobalInterface(){
        GlobalRankingView globalRankingView = new GlobalRankingView(this, game, localPlayer);
        scene.setRoot(globalRankingView.getRoot());
    }

    public void showLocalInterface(){
        LocalRankingView localRankingView = new LocalRankingView(this, game);
        scene.setRoot(localRankingView.getRoot());
    }

    private void showGameInterface(){
        gameView = new GameView(this, game, localPlayer);
        scene.setRoot(gameView.getRoot());
    }
    public void showStartInterface(){
        StartView startView = new StartView(this, availableColors);
        scene.setRoot(startView.getRoot());
    }

    public void showPlayerCountSelection(){
        PlayerCountSelectionView playerCountSelectionView = new PlayerCountSelectionView(this);
        scene.setRoot(playerCountSelectionView.getRoot());
    }

    public void showConnectionInterface() {
        ConnectionView connectionView = new ConnectionView(this);
        scene.setRoot(connectionView.getRoot());
    }

    public void showJoinInterface() {
        joinView = new JoinView(this, game.getGamesIdList());
        scene.setRoot(joinView.getRoot());
        try {
            //ask server for gameList
            getGameList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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

    @Override
    public void printGameId(UUID gameId) {

    }
    @Override
    public void printEra(){

    }
    @Override public void updateInterfacePlayerQueue(){
        if(this.gameView != null) {
            Platform.runLater(() -> {
                gameView.updatePlayerQueue();
            });
        }
    }
    @Override
    public void printGamesList(){
        // Check if the user is currently looking at the Join Screen
        if (joinView != null && scene.getRoot() == joinView.getRoot()) {

            // Pass the new list
            joinView.updateGameList(game.getGamesIdList());

        }
    }

    public void createLocalPlayer(Player player){
        localPlayer = player;
    }
    public void getGameList(){
        try {
            server.getGamesList(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void joinGame(UUID gameId) throws Exception{
            server.joinGame(client, gameId,localPlayer);
    }
    public void createGame(int numPlayer) throws Exception{
            server.createGame(client, localPlayer, numPlayer);
    }
    public void pickOfferingCard(OfferingCard card) throws Exception{
        server.pickOfferingCard(client, card.getOrderLetter());
    }
    public void pickTribeCards(List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception{
        server.pickTribeCards(client, characterCards, buildingCards);
    }


    @Override
    public  void setModel(ClientModel model) {
        this.game = model;
    }

    @Override
    public void setAvailableColors(List<Color> availableColors) {
        this.availableColors=availableColors;
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Duplicate Color");
            alert.setHeaderText(null);
            alert.setContentText("A player has already chosen your color");
            alert.showAndWait();
        });
    }

    // tODO
    @Override
    public boolean isBuilding2EffectUsed() {
        return false;
    }

    // todo
    @Override
    public void setBuilding2EffectUsed(boolean building2EffectUsed) {

    }


    /**
     * Updates localPlayer value when the object is updated into the queue by the server
     * Used to update food, pp and cards of the player
     */
    public void setLocalPlayer() {
        game.getOrderedPlayers().stream()
                .filter(p -> p.getNickname().equals(localPlayer.getNickname()))
                .findFirst().ifPresent(foundPlayer -> localPlayer = foundPlayer);
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }
}
