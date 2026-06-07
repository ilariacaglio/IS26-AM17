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
    private boolean building2EffectUsed;

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

    /**
     * Check gameState and either start the Game interface or update it
     * @param errorMessagge
     */
    @Override
    public void drawInterface(String errorMessagge) {
        //LEAVE UNTIL SYNCHRONIZATION PROBLEMS ARE SOLVED
        Platform.runLater(() -> {
            if (game == null) {
                System.out.println("DEBUG: Il modello 'game' è NULL!");
                return;
            }
            GameState gameState = game.getGameState();
            if (gameState == null) {
                System.out.println("DEBUG: Il gameState è NULL!");
                return;
            }

            System.out.println("DEBUG: drawInterface chiamato. Started: " + gameState.isGameStarted() + " | Ended: " + gameState.isGameEnded());
        //Platform.runLater(() -> {

            if(gameState.isGameStarted()) {
                if (!gameState.isGameEnded()) {
                    if (!isGameInterfaceInitialized) {
                        showGameInterface();
                        isGameInterfaceInitialized = true;
                    }
                    gameView.updateGameElements();
                }
            }
            if(gameState.isGameEnded()){
                System.out.println("DEBUG: Tento di mostrare showLocalInterface()...");
                showLocalInterface();
            }

            //}
        });
    }

    /**
     * Update all graphics component modified from endTurn call
     */
    public void updateInterfaceFromEndTurn(){
        Platform.runLater(() -> {
            gameView.updateGameElements();
        });
    }

    /**
     * Update all graphics component modified from pickTribes call
     */
    public void updateInterfaceFromPickTribes() {
        Platform.runLater(() -> {
            gameView.updateGameCardDecks();
        });
    }

    /**
     * Update all graphics component modified from pickOffering call
     */
    public void updateInterfaceFromPickOffering() {
        Platform.runLater(() -> {
            gameView.updateOfferingDeck();
        });
    }

    /**
     * set root to the interface for the global ranking
     */
    public void showGlobalInterface(){
        GlobalRankingView globalRankingView = new GlobalRankingView(this, game, localPlayer);
        scene.setRoot(globalRankingView.getRoot());
    }

    /**
     * set root to the interface for local ranking
     */
    public void showLocalInterface(){
        LocalRankingView localRankingView = new LocalRankingView(this, game);
        scene.setRoot(localRankingView.getRoot());
    }

    /**
     * set root to the interface for the game
     */
    private void showGameInterface(){
        gameView = new GameView(this, game, localPlayer);
        scene.setRoot(gameView.getRoot());
    }

    /**
     * set root to the start interface
     */
    public void showStartInterface(){
        StartView startView = new StartView(this, availableColors);
        scene.setRoot(startView.getRoot());
    }

    /**
     * set root to the interface to select the number of player when creating a game
     */
    public void showPlayerCountSelection(){
        PlayerCountSelectionView playerCountSelectionView = new PlayerCountSelectionView(this);
        scene.setRoot(playerCountSelectionView.getRoot());
    }

    /**
     * set root to the interface to select whether you want to join or create a game
     */
    public void showConnectionInterface() {
        ConnectionView connectionView = new ConnectionView(this);
        scene.setRoot(connectionView.getRoot());
    }

    /**
     * set root to the interface to join a game
     */
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



    @Override
    public void printGameId(UUID gameId) {

    }
    @Override
    public void printEra(){

    }

    /**
     * update all graphics in the game related to player queue
     */
    @Override public void updateInterfacePlayerQueue(){
        if(this.gameView != null) {
            Platform.runLater(() -> {
                gameView.updatePlayerQueue();
            });
        }
    }

    /**
     * update the game list in the join view
     */
    @Override
    public void printGamesList(){
        // Check if the user is currently looking at the Join Screen
        if (joinView != null && scene.getRoot() == joinView.getRoot()) {

            // Pass the new list
            joinView.updateGameList(game.getGamesIdList());

        }
    }

    /**
     * create and save a local player
     * @param player player you want to set as local player
     */
    public void createLocalPlayer(Player player){
        localPlayer = player;
    }

    /**
     * ask the server for the game list
     */
    public void getGameList(){
        try {
            server.getGamesList(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * ask the server to join a game
     * @param gameId id of the game you want to join
     * @throws Exception
     */
    public void joinGame(UUID gameId) throws Exception{
            server.joinGame(client, gameId,localPlayer);
    }

    /**
     * ask the server to create a game
     * @param numPlayer number of player you want in the game
     * @throws Exception
     */
    public void createGame(int numPlayer) throws Exception{
            server.createGame(client, localPlayer, numPlayer);
    }

    /**
     * ask the server to pick an offering card
     * @param card card you want to pick
     * @throws Exception
     */
    public void pickOfferingCard(OfferingCard card) throws Exception{
        server.pickOfferingCard(client, card.getOrderLetter());
    }

    /**
     * ask the server to pick tribes card
     * @param characterCards character cards you want to pick
     * @param buildingCards building cards you want to pick
     * @throws Exception
     */
    public void pickTribeCards(List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception{
        server.pickTribeCards(client, characterCards, buildingCards);
    }

    /**
     * update the model used by the gui
     * @param model
     */
    @Override
    public  void setModel(ClientModel model) {
        this.game = model;
    }

    /**
     * Set the color the player can choose when creating a character
     * @param availableColors
     */
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


    @Override
    public boolean isBuilding2EffectUsed() {
        return building2EffectUsed;
    }


    @Override
    public void setBuilding2EffectUsed(boolean building2EffectUsed) {
        this.building2EffectUsed = building2EffectUsed;
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

    /**
     * return the local player
     * @return localPlayer
     */
    public Player getLocalPlayer() {
        return localPlayer;
    }
}
