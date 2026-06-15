package it.polimi.ingsw.am17.Client.UserInterface.GUIElements;

import it.polimi.ingsw.am17.Client.Model.ClientModel;
import it.polimi.ingsw.am17.Client.UserInterface.CardGUI;
import it.polimi.ingsw.am17.Client.UserInterface.GUI;
import it.polimi.ingsw.am17.CommonInterfaces.InvalidOperationException;
import it.polimi.ingsw.am17.Server.Model.Color;
import it.polimi.ingsw.am17.Server.Model.GameCard.Buildings.BuildingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.OfferingCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.Characters.CharacterCard;
import it.polimi.ingsw.am17.Server.Model.GameCard.TribeCards.TribesCard;
import it.polimi.ingsw.am17.Server.Model.Player;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Draws the interface displayed when game is started.
 */
public class GameView {
    private VBox root;
    private final GUI mainGui;
    private VBox turnOverlay;
    private HBox upperCardsBox;
    private HBox lowerCardsBox;
    private HBox playerCardsBox;
    private HBox playerResourcesBox;
    private final List<CardGUI> offeringCardGUI = new ArrayList<>();
    private TurnCardGUI turnCard;
    private final ClientModel readOnlyModel;
    private final Player localPlayer;
    private Player selectedPlayer;
    private List<CharacterCard> tribesSelected = new ArrayList<>();
    private List<BuildingCard> buildingSelected = new ArrayList<>();
    private OfferingCard offeringSelected = null;

    public GameView(GUI mainGui, ClientModel readOnlyModel, Player localPlayer) {
        this.mainGui = mainGui;
        this.readOnlyModel = readOnlyModel;
        this.localPlayer = localPlayer;
        this.selectedPlayer = localPlayer;
        buildUI();
    }

    // todo: this method is 250 lines long, split!!
    private void buildUI() {
        //set fullscreen size
        // TODO: this doesn't always work
        Stage stage = (Stage) mainGui.scene.getWindow();
        stage.setMaximized(true);

        root = new VBox(10);

        turnOverlay = new VBox();
        turnOverlay.setAlignment(Pos.CENTER);

        Label turnText = new Label("IT'S YOUR TURN!");

        turnText.setStyle("""
            -fx-background-color: rgba(0, 0, 0, 0.75);
            -fx-background-radius: 15px;
            -fx-padding: 5px 40px;
            -fx-border-color: #c76b22;
            -fx-border-radius: 15px;
            -fx-border-width: 2px;
            -fx-text-fill: #f4dca6;
            -fx-font-weight: bold;
            -fx-font-size: 36px;
            -fx-font-family: 'Verdana';
            """);

        DropShadow textShadow = new DropShadow();
        textShadow.setRadius(5.0);
        textShadow.setOffsetY(3.0);
        textShadow.setColor(javafx.scene.paint.Color.color(0, 0, 0, 0.8));
        turnText.setEffect(textShadow);

        turnOverlay.getChildren().add(turnText);
        turnOverlay.setVisible(false);
        if(readOnlyModel.isPlayerTurn(localPlayer)){
            turnOverlay.setVisible(true);
        }

        // 1. Safely load the URL and check if it exists
        URL imageUrl = getClass().getResource("/Images/background_game.png");
        if (imageUrl == null) {
            throw new RuntimeException("Could not find image at /images/background_game.png inside resources!");
        }

        // 2. Create the Image object
        Image image = new Image(imageUrl.toExternalForm());

        // 3. Define the background settings
        BackgroundImage bgImage = new BackgroundImage(
                image,
                BackgroundRepeat.NO_REPEAT, // -fx-background-repeat: no-repeat
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,  // -fx-background-position: center
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true) // -fx-background-size: cover
        );

        // 4. Apply it to your root Pane
        root.setBackground(new Background(bgImage));

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
        upperCardsBox =new HBox(10);

        //put turnCard and offeringCard in the same HBox
        HBox offeringCardBox = new HBox(20);
        //turnCard first
        turnCard = new TurnCardGUI(readOnlyModel.getTURN_CARD_IMAGE_PATH(), readOnlyModel.getNumPlayers());
        offeringCardBox.getChildren().add(turnCard);

        //offeringCards second
        for(OfferingCard card : readOnlyModel.getOfferingCards()){
            CardGUI offeringCard;
            if(card.getPlayer() == null) {
                offeringCard = new CardGUI(card.getImagePath());
                offeringCard.setUserData(card);
            } else {
                offeringCard = new CardGUI(card.getImagePath(), card.getPlayer().getColor().getFxColor());
                offeringCard.setUserData(card);
            }
            setOnMouseClickForOffering(offeringCard, card);
            offeringCardGUI.add(offeringCard);
            offeringCardBox.getChildren().add(offeringCard);
        }

        //lowerCards
        //tribe cards first
        lowerCardsBox =  new HBox(10);

        //send button
        Button sendButton = new Button("SEND");
        sendButton.setOnAction(_ ->
        {
            try {
                if (offeringSelected != null) {
                    try{
                        readOnlyModel.validateOfferingCardTurnAction(localPlayer, offeringSelected.getOrderLetter());
                        mainGui.pickOfferingCard(offeringSelected);
                        offeringSelected = null;
                        buildingSelected = new ArrayList<>();
                        tribesSelected =  new ArrayList<>();
                    } catch (InvalidOperationException invalidOperationException) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error in selection");
                        alert.setHeaderText(null);
                        alert.setContentText(invalidOperationException.getErrorType().getMessage());
                        alert.showAndWait();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error in selection");
                        alert.setHeaderText(null);
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    }

                } else {
                    try {
                        readOnlyModel.validateTribeCardsTurnAction(localPlayer, tribesSelected, buildingSelected);
                        mainGui.pickTribeCards(tribesSelected, buildingSelected);
                        offeringSelected = null;
                        buildingSelected = new ArrayList<>();
                        tribesSelected =  new ArrayList<>();
                    } catch (InvalidOperationException invalidOperationException) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error in selection");
                        alert.setHeaderText(null);
                        alert.setContentText(invalidOperationException.getErrorType().getMessage());
                        alert.showAndWait();
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error in selection");
                        alert.setHeaderText(null);
                        alert.setContentText(ex.getMessage());
                        alert.showAndWait();
                    }

                }
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
        for(Player p : readOnlyModel.getOrderedPlayers()){
            Button playerButton = new Button(p.getNickname() );
            //set nickname color
            Color nicknameColor = p.getColor();
            String colorName = nicknameColor.name().toLowerCase(); // e.g., "red", "blue"

            playerButton.setStyle("""
    /* Smooth metallic gradient background */
    -fx-background-color: linear-gradient(to bottom, #f5f7fa 0%%, #c3cfe2 100%%);
    
    /* Rounded pill-like edges */
    -fx-background-radius: 25;
    
    /* Comfortable padding to shape the button */
    -fx-padding: 12px 30px;
    
    /* Soft drop shadow for 3D depth */
    -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 4);
    
    /* Dynamic color insertion */
    -fx-text-fill: %s;
    
    /* Modern, clean typography */
    -fx-font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
    -fx-font-weight: bold;
    -fx-font-size: 18px;
    
    /* Change cursor to pointer on hover */
    -fx-cursor: hand;
""".formatted(colorName));
            //add area for other players' cards

            otherScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            otherScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            otherScrollPane.setFitToHeight(true);
            otherScrollPane.setPannable(true);
            otherScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

            playersCardsBox.getChildren().add(playerButton);
            StackPane.setAlignment(playersCardsBox, Pos.BOTTOM_CENTER);
            playerButton.setOnAction(_ -> {
                selectedPlayer = p;
                createPlayerCardLabel();
                updateSelectedPlayer();
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
        updateSelectedPlayer();



        VBox playerCardsContainer = new VBox(10, scrollPane);

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
                playerResourcesBox, playerCardsContainer, spacer, playersButtonBox);
    }

    /**
     * update all graphics related to player queue
     */
    public void updatePlayerQueue(){
        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));
    }

    /**
     * call all other updates
     */
    public void updateGameElements() {
        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));


        // Update Upper Cards
        updateUpperCards();

        //UpdateTurnCards
        List<Color> currentTurnOrder = readOnlyModel.getOrderedPlayers().stream()
                .map(Player::getColor)
                .toList();

        turnCard.placeTotems(currentTurnOrder);

        // Update Offering Cards
        updateOfferingCards();

        // Update Lower Cards
        updateLowerCards();

        // Update Player stats (Points, Food, Name) and personal board
        createPlayerCardLabel();
        updateSelectedPlayer();
    }

    /**
     * update upper and lower decks graphics by removing picked cards
     */
    public void updateGameCardDecks() {
        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));

        // Update Upper Cards
        upperCardsBox.getChildren().removeIf(node -> {
            Object cardData = node.getUserData();

            // Check if the card still exists in the game model
            boolean stillInTribeRow = readOnlyModel.getUpperTribeRow().contains(cardData);
            boolean stillInBuildingRow = readOnlyModel.getUpperBuildingRow().contains(cardData);

            // If it is NOT in the tribe row AND NOT in the building row, remove it (return true)
            return !stillInTribeRow && !stillInBuildingRow;
        });

        // Update Lower Cards
        lowerCardsBox.getChildren().removeIf(node -> {
            Object cardData = node.getUserData();

            // Check if the card still exists in the game model
            boolean stillInTribeRow = readOnlyModel.getLowerTribeRow().contains(cardData);
            boolean stillInBuildingRow = readOnlyModel.getLowerBuildingRow().contains(cardData);

            // If it is NOT in the tribe row AND NOT in the building row, remove it
            return !stillInTribeRow && !stillInBuildingRow;
        });

        // Update Player stats (Points, Food, Name) and personal board
        createPlayerCardLabel();
        updateSelectedPlayer();
    }

    /**
     * update offering deck's graphics
     */
    public void updateOfferingDeck(){
        // Update turn overlay visibility
        turnOverlay.setVisible(readOnlyModel.isPlayerTurn(localPlayer));

        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }
        for(int i=0; i<offeringCardGUI.size(); i++){
            CardGUI card = offeringCardGUI.get(i);
            Player p = readOnlyModel.getOfferingCards().get(i).getPlayer();
            if (p != null) {
                card.updateTotem(p.getColor().getFxColor());
                turnCard.removePlayerTotem(p.getColor());
            } else {
                // Optional: Reset to default border if no player owns it
                card.updateTotem(javafx.scene.paint.Color.TRANSPARENT);
            }
        }
    }

    /**
     * update upper deck graphics
     */
    private void updateUpperCards() {
        upperCardsBox.getChildren().clear(); // Remove old cards
        for(TribesCard card : readOnlyModel.getUpperTribeRow()){
            CardGUI upperCard = new CardGUI(card.getImagePath());
            upperCard.setUserData(card);
            setOnMouseClickForTribes(upperCard, card);
            upperCardsBox.getChildren().add(upperCard);
        }
        for(BuildingCard card : readOnlyModel.getUpperBuildingRow()){
            CardGUI upperCard = new CardGUI(card.getImagePath());
            upperCard.setUserData(card);
            setOnMouseClickForBuilding(upperCard, card);
            upperCardsBox.getChildren().add(upperCard);
        }
    }

    /**
     * update lower deck graphics
     */
    private void updateLowerCards(){
        lowerCardsBox.getChildren().clear();
        for(TribesCard card : readOnlyModel.getLowerTribeRow()){
            CardGUI lowerCard = new CardGUI(card.getImagePath());
            lowerCard.setUserData(card);
            setOnMouseClickForTribes(lowerCard, card);
            lowerCardsBox.getChildren().add(lowerCard);
        }
        for(BuildingCard card : readOnlyModel.getLowerBuildingRow()){
            CardGUI lowerCard = new CardGUI(card.getImagePath());
            lowerCard.setUserData(card);
            setOnMouseClickForBuilding(lowerCard, card);
            lowerCardsBox.getChildren().add(lowerCard);
        }
    }


    /**
     * Resets offering card colored border and updates totem placement
     */
    private void updateOfferingCards(){

        //set all card to not selected
        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }

        //update the totem on the cards
        for (int i = 0; i < offeringCardGUI.size(); i++) {
            CardGUI card = offeringCardGUI.get(i);

            if (i < readOnlyModel.getOfferingCards().size()) {
                OfferingCard serverCard = readOnlyModel.getOfferingCards().get(i);

                card.setUserData(serverCard);

                if (serverCard.getPlayer() != null) {
                    card.updateTotem(serverCard.getPlayer().getColor().getFxColor());
                } else {
                    card.updateTotem(javafx.scene.paint.Color.TRANSPARENT);
                }
            }
        }

    }

    /**
     * set the action from click on tribes card
     */
    private void setOnMouseClickForTribes(CardGUI cardGUI, TribesCard card) {
        if(card.getCardType().isCharacter()) {
            cardGUI.setOnMouseClicked(_ -> {
                if (!readOnlyModel.isPlayerTurn(localPlayer)) {
                    showWaitTurnAlert();
                    return;
                }

                if (readOnlyModel.isPickOCPhase()) {
                    return; // Not selectable right now
                }

                // Toggle the visual state
                cardGUI.setVisualSelection(!cardGUI.isSelected());

                tribesSelected((CharacterCard) card);
            });
        }
    }

    /**
     * set the action from click on building card
     */
    private void setOnMouseClickForBuilding(CardGUI cardGUI, BuildingCard card ) {
        cardGUI.setOnMouseClicked(_ -> {
            if (!readOnlyModel.isPlayerTurn(localPlayer)) {
                showWaitTurnAlert(); // The main GUI handles the alert, not the card!
                return;
            }

            if (readOnlyModel.isPickOCPhase()) {
                return; // Not selectable right now
            }

            // Toggle the visual state
            cardGUI.setVisualSelection(!cardGUI.isSelected());

            // Handle the game logic
            buildingSelected(card);
        });
    }

    /**
     * set the action from click on offering card
     */
    private void setOnMouseClickForOffering(CardGUI cardGUI, OfferingCard card) {
        cardGUI.setOnMouseClicked(_ -> {
            if (!readOnlyModel.isPlayerTurn(localPlayer)) {
                showWaitTurnAlert(); // The main GUI handles the alert, not the card!
                return;
            }

            if (!readOnlyModel.isPickOCPhase()) {
                return; // Not selectable right now
            }

            OfferingCard thisCard = readOnlyModel.getOfferingCards().stream()
                    .filter(c -> c.equals(card)) // Put your condition inside filter()
                    .findFirst()
                    .orElse(null);

            if(thisCard == null || thisCard.getPlayer()!=null)
                return;

            // Handle the game logic
            offeringSelected(card);

            // Toggle the visual state
            cardGUI.setVisualSelection(!cardGUI.isSelected());
        });
    }

    /**
     * Displays an alert when the user plays out of its turn
     */
    private void showWaitTurnAlert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Wait Your Turn");
        alert.setHeaderText(null);
        alert.setContentText("It is currently the other player's turn!");
        alert.showAndWait();

    }

    /**
     * Handles tribes card selection by adding or removing the card to it
     */
    public void tribesSelected(CharacterCard card) {
        if(tribesSelected.contains(card))
            tribesSelected.remove(card);
        else
            tribesSelected.add(card);
    }

    /**
     * Handles building card selection by adding or removing the card to it
     */
    public void buildingSelected(BuildingCard card) {
        if(buildingSelected.contains(card))
            buildingSelected.remove(card);
        else
            buildingSelected.add(card);
    }

    /**
     * Handles offering card selection
     */
    public void offeringSelected(OfferingCard card) {
        offeringSelected = card;

        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }
    }

    /**
     * update the graphics related to selected player
     */
    private void updateSelectedPlayer(){
        //update selected player
        selectedPlayer = readOnlyModel.findPlayer(selectedPlayer);

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
        for(BuildingCard card : selectedPlayer.getBuildingCards()){
            CardGUI buildingCard = new CardGUI(card.getImagePath());
            playerCardsBox.getChildren().add(buildingCard);
        }
    }

    /**
     * Displays the selected players data (excluding cards)
     */
    private void createPlayerCardLabel(){
        selectedPlayer = readOnlyModel.findPlayer(selectedPlayer);
        Label name = new Label("Name: " + selectedPlayer.getNickname());
        name.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        Label food = new Label("Food: " + selectedPlayer.getFood());
        food.setStyle("""
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 30px;
        """);
        Label points = new Label("Points: " + selectedPlayer.getPp());
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

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}
