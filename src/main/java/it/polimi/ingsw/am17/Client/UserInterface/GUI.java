package it.polimi.ingsw.am17.Client.UserInterface;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.ServerAdapter;
import it.polimi.ingsw.am17.Client.UserInterface.GUIElements.*;
import it.polimi.ingsw.am17.CommonInterfaces.ColorException;
import it.polimi.ingsw.am17.CommonInterfaces.ErrorType;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;
import java.util.List;

public class GUI implements UI {

    private final double START_WINDOW_WIDTH = 400;
    private final double START_WINDOW_HEIGHT = 300;

    private final ServerAdapter serverAdapter;
    private ClientModel readOnlyModel;
    private Player localPlayer;
    private boolean building2EffectUsed;

    private VBox root;
    public Scene scene;

    private GameView gameView;

    private boolean isGameInterfaceInitialized = false;

    List<Color> availableColors;

    public GUI(ServerAdapter serverAdapter) {
        this.serverAdapter = serverAdapter;
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
     */
    @Override
    public void updateInterfaceFromStartGame() {
        //LEAVE UNTIL SYNCHRONIZATION PROBLEMS ARE SOLVED
        Platform.runLater(() -> {
            if (!isGameInterfaceInitialized) {
                showGameInterface();
                isGameInterfaceInitialized = true;
            }
            gameView.updateGameElements();
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
    public void updateInterfaceFromPlayerSelectTribeCards() {
        Platform.runLater(() -> {
            gameView.updateGameCardDecks();
        });
    }
    /**
     * Update all graphics component modified from playerQueue call
     */
    public void updateInterfaceFromPlayerQueueChange(){
        Platform.runLater(() -> {
            gameView.updatePlayerQueue();
        });
    }

    /**
     * Update all graphics component modified from pickOffering call
     */
    public void updateInterfaceFromPlayerSelectOfferingCard() {
        Platform.runLater(() -> {
            gameView.updateOfferingDeck();
        });
    }

    @Override
    public void updateInterfaceFromGameStateChange(){
        Platform.runLater(()-> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("New Era");
            alert.setHeaderText(null);
            String eraToDisplay = readOnlyModel.getGameState().toString().replace("era", "");
            alert.setContentText("Era "+ eraToDisplay + " has begun!");
            alert.showAndWait();
        });
    }

    /**
     * set root to the interface for the global ranking
     */
    public void showGlobalInterface(){
        GlobalRankingView globalRankingView = new GlobalRankingView(this, readOnlyModel, localPlayer);
        scene.setRoot(globalRankingView.getRoot());
    }

    /**
     * set root to the interface for local ranking
     */
    public void showLocalInterface(){
        LocalRankingView localRankingView = new LocalRankingView(this, readOnlyModel);
        scene.setRoot(localRankingView.getRoot());
    }

    /**
     * set root to the interface for the game
     */
    private void showGameInterface(){
        gameView = new GameView(this, readOnlyModel, localPlayer);
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
        joinView = new JoinView(this, readOnlyModel.getGamesIdList());
        scene.setRoot(joinView.getRoot());
        try {
            //ask server for gameList
            getGameList();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * update the game list in the join view
     */

    @Override
    public void updateInterfaceFromGameIdListChange(){
        // Check if the user is currently looking at the Join Screen
        if (joinView != null && scene.getRoot() == joinView.getRoot()) {

            // Pass the new list
            joinView.updateGameList(readOnlyModel.getGamesIdList());

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
            serverAdapter.getGamesList().join();
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
        serverAdapter.joinGame(gameId,localPlayer).join();
    }

    /**
     * ask the server to create a game
     * @param numPlayer number of player you want in the game
     * @throws Exception
     */
    public void createGame(int numPlayer) throws Exception{
        serverAdapter.createGame(localPlayer, numPlayer).join();
    }

    /**
     * ask the server to pick an offering card
     * @param card card you want to pick
     * @throws Exception
     */
    public void pickOfferingCard(OfferingCard card) throws Exception{
        serverAdapter.pickOfferingCard(card.getOrderLetter()).join();
    }

    /**
     * ask the server to pick tribes card
     * @param characterCards character cards you want to pick
     * @param buildingCards building cards you want to pick
     * @throws Exception
     */
    public void pickTribeCards(List<CharacterCard> characterCards, List<BuildingCard> buildingCards) throws Exception{
        serverAdapter.pickTribeCards(characterCards, buildingCards).join();
    }

    /**
     * update the model used by the gui
     * @param model
     */
    @Override
    public  void setModel(ClientModel model) {
        this.readOnlyModel = model;
    }

    /**
     * Set the color the player can choose when creating a character
     * @param availableColors
     */
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

    /**
     * When game is ended show the ranking of the game
     */
    @Override
    public void updateInterfaceFromEndGame(){
        Platform.runLater(this::showLocalInterface);
    }

    @Override
    public void updateInterfaceFromForcedEndGame(String disconnectedPlayer){
        Platform.runLater(()-> {
            //alert the player that another player has disconnected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Player has disconnected");
            alert.setHeaderText(null);
            alert.setContentText(disconnectedPlayer + " has disconnected from the game");
            alert.showAndWait();

            showStartInterface();
        });
    }

    /**
     * Alert the player when a notification error is received.
     * @param exception     the exception describing the error occurred.
     */
    @Override
    public void updateInterfaceFromErrorMessage(InvalidOperationException exception){
        ErrorType type = exception.getErrorType();
        if (type == ErrorType.DUPLICATE_COLOR) {
            availableColors = ((ColorException) exception).getAvailableColors();
            setAvailableColors(availableColors);
        }else {

            String messageToDisplay = (type == ErrorType.UNKNOWN)
                    ? exception.getMessage()
                    : type.getMessage();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(messageToDisplay);
                alert.showAndWait();
            });


        }
    }

    /**
     * Since you connected to the game show game interface
     */
    @Override
    public void updateInterfaceFromGameIdChange() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Connected");
            alert.setHeaderText(null);
            alert.setContentText("You have been connected to a game");
            alert.showAndWait();
        });

    }


    /**
     * Updates localPlayer value when the object is updated into the queue by the server
     * Used to update food, pp and cards of the player
     */
    public void setLocalPlayer() {
        readOnlyModel.getOrderedPlayers().stream()
                .filter(p -> p.getNickname().equals(localPlayer.getNickname()))
                .findFirst().ifPresent(foundPlayer -> localPlayer = foundPlayer);
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }
}
