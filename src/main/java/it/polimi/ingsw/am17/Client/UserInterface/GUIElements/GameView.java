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
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameView {
    private VBox root;
    private GUI mainGui;
    private VBox turnOverlay;
    private HBox upperCardsBox;
    private HBox lowerCardsBox;
    private HBox offeringCardBox;
    private ClientModel game;
    private Player localPlayer;
    private List<CardGUI> offeringCardGUI = new ArrayList<>();
    private List<CharacterCard> tribesSelected = new ArrayList<>();
    private List<BuildingCard> buildingSelected = new ArrayList<>();
    private OfferingCard offeringSelected = null;
    private Player selectedPlayer;
    private HBox playerCardsBox;
    private HBox playerResourcesBox;
    private Label name;
    private Label food;
    private Label points;


    public GameView(GUI mainGui, ClientModel game, Player localPlayer) {
        this.mainGui = mainGui;
        this.game = game;
        this.localPlayer = localPlayer;
        this.selectedPlayer = localPlayer;
        buildUI();
    }

    private void buildUI() {
        //set fullscreen size
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
        if(game.isPlayerTurn(localPlayer)){
            turnOverlay.setVisible(true);
        }

        root.setStyle("-fx-background-image: url('/images/background_game.png');");
        root.setStyle("""
            -fx-background-image: url('/images/background_game.png');
            -fx-background-size: cover;
            -fx-background-position: center center;
            -fx-background-repeat: no-repeat;
        """);

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
        offeringCardBox = new HBox(20);
        //turnCard first
        CardGUI turnCard = new CardGUI(game.getTURN_CARD_IMAGE_PATH());
        offeringCardBox.getChildren().add(turnCard);

        //offeringCards second
        for(OfferingCard card : game.getOfferingCards()){
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
        sendButton.setOnAction(e ->
        {
            try {
                if (offeringSelected != null) {
                    try{
                        game.validateOfferingCardTurnAction(localPlayer, offeringSelected.getOrderLetter());
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
                    // get players offering card
                    OfferingCard myOfferingCard = game.getOfferingCards().stream()
                            .filter(c->c.getPlayer()!= null && c.getPlayer().equals(localPlayer))
                            .findFirst().orElse(null);

                    if (myOfferingCard == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Azione non consentita");
                        alert.setHeaderText(null);
                        alert.setContentText("Non possiedi ancora una Offering Card per questa fase di gioco!");
                        alert.showAndWait();
                        return;
                    }

                    try {
                        game.validateTribeCardsTurnAction(localPlayer, tribesSelected, buildingSelected);
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
        for(Player p : game.getOrderedPlayers()){
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


    public void updateGameElements() {
        // Update turn overlay visibility
        turnOverlay.setVisible(game.isPlayerTurn(localPlayer));


        // Update Upper Cards
        updateUpperCards();

        // Update Offering Cards
        updateOfferingCards();

        // Update Lower Cards
        updateLowerCards();

        // Update Player stats (Points, Food, Name) and personal board
        createPlayerCardLabel();
        orderPersonalCards();
    }

    public void updateGameCardDecks() {
        // Update turn overlay visibility
        turnOverlay.setVisible(game.isPlayerTurn(localPlayer));

        // Update Upper Cards
        upperCardsBox.getChildren().removeIf(node -> {
            Object cardData = node.getUserData();

            // Check if the card still exists in the game model
            boolean stillInTribeRow = game.getUpperTribeRow().contains(cardData);
            boolean stillInBuildingRow = game.getUpperBuildingRow().contains(cardData);

            // If it is NOT in the tribe row AND NOT in the building row, remove it (return true)
            return !stillInTribeRow && !stillInBuildingRow;
        });

        // Update Lower Cards
        lowerCardsBox.getChildren().removeIf(node -> {
            Object cardData = node.getUserData();

            // Check if the card still exists in the game model
            boolean stillInTribeRow = game.getLowerTribeRow().contains(cardData);
            boolean stillInBuildingRow = game.getLowerBuildingRow().contains(cardData);

            // If it is NOT in the tribe row AND NOT in the building row, remove it
            return !stillInTribeRow && !stillInBuildingRow;
        });

        // Update Player stats (Points, Food, Name) and personal board
        createPlayerCardLabel();
        orderPersonalCards();
    }

    public void updateOfferingDeck(){
        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }
        for(int i=0; i<offeringCardGUI.size(); i++){
            CardGUI card = offeringCardGUI.get(i);
            if (game.getOfferingCards().get(i).getPlayer() != null) {
                card.updateBorderFromPlayer(game.getOfferingCards().get(i).getPlayer().getColor().getFxColor());
            } else {
                // Optional: Reset to default border if no player owns it
                card.updateBorderFromPlayer(javafx.scene.paint.Color.TRANSPARENT);
            }
        }
    }


    private void updateUpperCards() {
        List<TribesCard> upperTribeRow = new ArrayList<>(game.getUpperTribeRow());
        List<BuildingCard> upperBuildingRow = new ArrayList<>(game.getUpperBuildingRow());
        upperCardsBox.getChildren().clear(); // Remove old cards
        for(TribesCard card : upperTribeRow){
            CardGUI upperCard = new CardGUI(card.getImagePath());
            upperCard.setUserData(card);
            setOnMouseClickForTribes(upperCard, card);
            upperCardsBox.getChildren().add(upperCard);
        }
        for(BuildingCard card : upperBuildingRow){
            CardGUI upperCard = new CardGUI(card.getImagePath());
            upperCard.setUserData(card);
            setOnMouseClickForBuilding(upperCard, card);
            upperCardsBox.getChildren().add(upperCard);
        }
    }

    private void updateLowerCards(){
        List<TribesCard> lowerTribeRow = new ArrayList<>(game.getLowerTribeRow());
        List<BuildingCard> lowerBuildingRow = new ArrayList<>(game.getLowerBuildingRow());
        lowerCardsBox.getChildren().clear();
        for(TribesCard card : lowerTribeRow){
            CardGUI lowerCard = new CardGUI(card.getImagePath());
            lowerCard.setUserData(card);
            setOnMouseClickForTribes(lowerCard, card);
            lowerCardsBox.getChildren().add(lowerCard);
        }
        for(BuildingCard card : lowerBuildingRow){
            CardGUI lowerCard = new CardGUI(card.getImagePath());
            lowerCard.setUserData(card);
            setOnMouseClickForBuilding(lowerCard, card);
            lowerCardsBox.getChildren().add(lowerCard);
        }
    }

    private void updateOfferingCards(){
        List<OfferingCard> offeringCardRow = new ArrayList<>(game.getOfferingCards());
        for (CardGUI cardGUI : offeringCardGUI){
            //cardGUI.updateBorderFromPlayer(javafx.scene.paint.Color.TRANSPARENT);
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }
        for (int i = 0; i < offeringCardGUI.size(); i++) {
            CardGUI card = offeringCardGUI.get(i);

            if (i < offeringCardRow.size()) {
                OfferingCard serverCard = offeringCardRow.get(i);

                card.setUserData(serverCard);

                if (serverCard.getPlayer() != null) {
                    card.updateBorderFromPlayer(serverCard.getPlayer().getColor().getFxColor());
                } else {
                    card.updateBorderFromPlayer(javafx.scene.paint.Color.TRANSPARENT);
                }
            }
        }

    }

    private void setOnMouseClickForTribes(CardGUI cardGUI, TribesCard card) {
        if(card.getCardType().isCharacter()) {
            cardGUI.setOnMouseClicked(event -> {
                if (!game.isPlayerTurn(localPlayer)) {
                    showWaitTurnAlert();
                    return;
                }

                if (game.isPickOCPhase()) {
                    return; // Not selectable right now
                }

                // Toggle the visual state
                cardGUI.setVisualSelection(!cardGUI.isSelected());

                tribesSelected((CharacterCard) card);
            });
        }
    }

    private void setOnMouseClickForBuilding(CardGUI cardGUI, BuildingCard card ) {
        cardGUI.setOnMouseClicked(event -> {
            if (!game.isPlayerTurn(localPlayer)) {
                showWaitTurnAlert(); // The main GUI handles the alert, not the card!
                return;
            }

            if (game.isPickOCPhase()) {
                return; // Not selectable right now
            }

            // Toggle the visual state
            cardGUI.setVisualSelection(!cardGUI.isSelected());

            // Handle the game logic
            buildingSelected(card);
        });
    }

    private void setOnMouseClickForOffering(CardGUI cardGUI, OfferingCard card) {
        cardGUI.setOnMouseClicked(event -> {
            if (!game.isPlayerTurn(localPlayer)) {
                showWaitTurnAlert(); // The main GUI handles the alert, not the card!
                return;
            }

            if (!game.isPickOCPhase()) {
                return; // Not selectable right now
            }

            OfferingCard thisCard = game.getOfferingCards().stream()
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

    private void showWaitTurnAlert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Wait Your Turn");
        alert.setHeaderText(null);
        alert.setContentText("It is currently the other player's turn!");
        alert.showAndWait();

    }

    public void tribesSelected(CharacterCard card) {
        if(tribesSelected.contains(card))
            tribesSelected.remove(card);
        else
            tribesSelected.add(card);
    }

    public void buildingSelected(BuildingCard card) {
        if(buildingSelected.contains(card))
            buildingSelected.remove(card);
        else
            buildingSelected.add(card);
    }

    public void offeringSelected(OfferingCard card) {
        if(offeringSelected == card)
            offeringSelected = null;
        else
            offeringSelected = card;

        for (CardGUI cardGUI : offeringCardGUI){
            if(cardGUI.isSelected())
                cardGUI.setVisualSelection(false);
        }
    }

    private void orderPersonalCards(){
        //update selected player
        selectedPlayer = game.getPlayerFromList(selectedPlayer);

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

    // The main GUI will call this to put it in the Scene
    public Parent getRoot() {
        return root;
    }
}
